import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;


class MyRpcClientFacade
{
    private String hostname;
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(MyRpcClientFacade.class);

    // 如果消息发送失败，尝试发送的消息次数，默认为3次
    private int attemptTimes = 3;

    private RpcClient client;

    private int maxBatchSize = 1000;

    public void init(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;

        client = RpcClientFactory.getDefaultInstance(hostname, port, maxBatchSize);
    }

    /**
     * 发送一条记录，如果发送失败，该方法会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 建议使用appendBatch以获得更好的性能。
     *
     * @param event 发送内容
     * @return 发送成功返回true，失败返回false
     */
    public boolean append(Event event){
        boolean flag = false;

        int current = 0;

        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.append(event);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.debug("发送失败，当前已尝试" + current + "次", e);
                logger.debug("失败消息");
            }
        }

        return flag;
    }


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
    public void cleanUp()
    {
        // Close the RPC connection
        client.close();
    }

}