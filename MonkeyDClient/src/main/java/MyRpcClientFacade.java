import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import monkey.d.Agent;
import monkey.d.ServerController;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


class MyRpcClientFacade
{
    private String hostname;
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(MyRpcClientFacade.class);

    // 如果消息发送失败，尝试发送的消息次数，默认为3次
    private int attemptTimes = 3;

    private RpcClient[] sourceClients = null;

    private int maxBatchSize = 1000;

    private RestTemplate restTemplate = new RestTemplate();







    URI loadBalanceUri = null;
    int[] clientsCounter = null;

    public void init(String hostname, int port, int gap, String loadbalance)
    {
        this.hostname = hostname;
        this.port = port;
        UriComponentsBuilder lbbuilder = UriComponentsBuilder.fromUriString("http://" + this.hostname + ":" + this.port + "/"+ loadbalance);
        loadBalanceUri = lbbuilder.build().toUri();
        logger.info("loadBalance" + loadBalanceUri);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://" + this.hostname + ":" + this.port + "/"+ "agentList");

        Agent[] agentList = restTemplate.getForObject(builder.build().toUri(), Agent[].class);
        sourceClients = new RpcClient[agentList.length];

        clientsCounter = new int[agentList.length];
        for (int i = 0; i< sourceClients.length; i++)
        {
            clientsCounter[i] = 0;
            Agent agent = agentList[i];
            sourceClients[i] = RpcClientFactory.getDefaultInstance(agent.getIp(), agent.getSourcePort());
        }

        PeriodicTrigger periodicTrigger = new PeriodicTrigger(gap, TimeUnit.MILLISECONDS);
        periodicTrigger.setFixedRate(true);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new MyRunnable(), gap, gap, TimeUnit.MILLISECONDS);
//        future = threadPoolTaskScheduler().schedule(new MyRunnable(), periodicTrigger);
        //client = RpcClientFactory.getDefaultInstance(hostname, port, maxBatchSize);
    }

    public RpcClient getClient()
    {
        clientsCounter[sourceClientId] ++;
        return sourceClients[sourceClientId];
    }

    private int sourceClientId = 0;

    private class MyRunnable implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                //logger.debug("DynamicTask.MyRunnable.run() start");

                sourceClientId = restTemplate.getForObject(loadBalanceUri, int.class);

                //logger.debug("DynamicTask.MyRunnable.run() end");

            } catch (Exception e)
            {
                logger.error(e.getMessage());
            }

        }

    }

//    /**
//     * 发送一条记录，如果发送失败，该方法会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
//     * 建议使用appendBatch以获得更好的性能。
//     *
//     * @param event 发送内容
//     * @return 发送成功返回true，失败返回false
//     */
//    public boolean append(Event event){
//        boolean flag = false;
//
//        int current = 0;
//
//        while (!flag && current < attemptTimes) {
//            current++;
//            try {
//                sourceClients[0].append(event);
//                flag = true;
//            } catch (EventDeliveryException e) {
//                logger.debug("发送失败，当前已尝试" + current + "次", e);
//                logger.debug("失败消息");
//            }
//        }
//
//        return flag;
//    }


    /**
     * 批量发送多条记录，如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * appendBatch性能远高于append，建议使用。
     *
     * @param events 内容列表
     * @return 发送成功返回true，失败返回false
     */
    public boolean appendBatchEvent(List<Event> events){
        boolean flag = false;

        // 如果参数不符合要求，则退出
        if(events == null || events.size() < 1){
            return flag;
        }

        // 当前尝试发送的次数
        int current = 0;
        RpcClient client = getClient();
        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.appendBatch(events);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.debug("批量发送失败，当前已尝试" + current + "次", e);
            }
        }

        return flag;
    }

    public void cleanUp() throws JsonProcessingException
    {
        // Close the RPC connection
        logger.info("counts:"+  new ObjectMapper().writeValueAsString(clientsCounter));
        if (sourceClients != null)
        {
            for (RpcClient client : sourceClients)
                client.close();
        }
    }

}