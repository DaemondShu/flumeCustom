package monkey.d;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.catalina.mapper.Mapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by monkey_d_asce on 17-5-26.
 */
@RestController
public class ServerController
{
    Logger logger = Logger.getLogger(ServerController.class);
    ObjectMapper objectMapper = new ObjectMapper();
    static final String CONFIG_SOURCE_PORT = "source_port";
    static final String CONFIG_MONITOR_PORT = "monitor_port";
    List<Agent> agentList = null;

    Integer CapacitySum = 0;


    ConcurrentMap<Integer, Integer> clientsCount = new ConcurrentHashMap<>();


    @RequestMapping("/test")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name)
    {
        System.out.println(name);
        return "test " + name;
    }


    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ScheduledFuture<?> future;


    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler()
    {
        return new ThreadPoolTaskScheduler();
    }

    void loadAgentsConfig(String agentConfigStr) throws Exception
    {
        if (agentConfigStr == null || agentConfigStr.isEmpty())
        {
            //            //load props
            //            Properties agentProps = new Properties();
            //            String path = Class.class.getClass().getResource("/").getPath()+"agents.properties";
            //            logger.info("path:" + path);
            //            InputStream is = new FileInputStream(path);
            //            agentProps.load(is);
            //
            //            //load num
            //            Integer num = Integer.parseInt(agentProps.getProperty(num))
            //
            //
            //            logger.debug(agentProps.getProperty("num", "555"));


            String path = Class.class.getClass().getResource("/").getPath() + "agents.json";
            logger.info("path:" + path);

            loadAgentsConfig(objectMapper.readTree(new File(path)));
        } else
        {
            loadAgentsConfig(objectMapper.readTree(agentConfigStr));
        }

    }

    protected void loadAgentsConfig(JsonNode agentConfigJson) throws Exception
    {
        final String CONFIG_AGENTS = "agents";

        ArrayNode agents = (ArrayNode) agentConfigJson.get(CONFIG_AGENTS);
        logger.info("json:" + agents.toString());

        Agent agent = objectMapper.convertValue(agents.get(0), Agent.class);

        logger.info("json:" + agent.toString());


        agentList = objectMapper.convertValue(agents, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Agent.class));

        logger.info("json:" + agentList);

        if (agentList == null || agentList.isEmpty())
        {
            throw new Exception("bad list");
        }


    }



    @RequestMapping("/startCron")
    public String startCron(@RequestParam(value = "time", required = false, defaultValue = "3000") Integer gap, @RequestParam(value = "agents", required = false) String agentConfigStr)
    {

        try
        {
            loadAgentsConfig(agentConfigStr);
            stopCron();// 先停止，在开启.
            logger.info("gap=" + gap);
            PeriodicTrigger periodicTrigger = new PeriodicTrigger(gap, TimeUnit.MILLISECONDS);
            periodicTrigger.setFixedRate(true);

            future = threadPoolTaskScheduler.schedule(new MyRunnable(), periodicTrigger);
            logger.info("startCron");
            return "startCron";
        } catch (Exception e)
        {
            logger.error("error", e);
            return e.getMessage();
        }
    }


    //不会出现cleintId同时掉用的情况
    @RequestMapping("/roundRobin")
    public String roundRobin(@RequestParam(value = "clientId", required = true) Integer ClientId) throws JsonProcessingException
    {
        if (!clientsCount.containsKey(ClientId))
        {
            clientsCount.put(ClientId, 0);
        }

        Integer count = clientsCount.get(ClientId);
        if (count == null)
            count = 0;
        count = (count + 1) % agentList.size();
        clientsCount.put(ClientId, count);
        return objectMapper.writeValueAsString(agentList.get(count));
    }


    @RequestMapping("/hashMap")
    public String hashMap()
    {
        if (future != null)
        {
            future.cancel(true);
        }
        System.out.println("DynamicTask.stopCron()");
        return "stopCron";
    }


    @RequestMapping("/stopCron")
    public String stopCron()
    {

        if (future != null)
        {
            future.cancel(true);
        }
        System.out.println("DynamicTask.stopCron()");
        return "stopCron";
    }


    private class MyRunnable implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                //update info
                for (Agent agent : agentList)
                {
                    logger.debug(agent.getUri().toString());
                }

                logger.debug("DynamicTask.MyRunnable.run() start");
                Thread.sleep(5000);
                logger.debug("DynamicTask.MyRunnable.run() end");
            } catch (Exception e)
            {

            }

        }

    }


}
