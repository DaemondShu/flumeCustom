package monkey.d;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    static RestTemplate restTemplate = new RestTemplate();
    List<Agent> agentList = null;

    Integer CapacitySum = 0;
    Integer[] capacityList = null;
    Random r = new java.util.Random();


    Lock updateAgentlock = new ReentrantLock();

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


    void loadAgentsConfig(JsonNode agentConfigJson) throws Exception
    {
        final String CONFIG_AGENTS = "agents";

        ArrayNode agents = (ArrayNode) agentConfigJson.get(CONFIG_AGENTS);
        logger.info("json:" + agents.toString());

        agentList = objectMapper.convertValue(agents, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Agent.class));

        logger.info("json:" + agentList);

        if (agentList == null || agentList.isEmpty())
        {
            throw new Exception("bad list");
        }
    }



    @RequestMapping("/startCron")
    public String startCron(@RequestParam(value = "time", required = false, defaultValue = "1000") Integer gap, @RequestParam(value = "agents", required = false) String agentConfigStr)
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
    public Integer roundRobin(@RequestParam(value = "clientId", required = true) Integer ClientId) throws JsonProcessingException
    {

        Integer count = clientsCount.get(ClientId);
        if (count == null)
            count = 0;
        else
        {
            count = (count + 1) % agentList.size();
        }

        clientsCount.put(ClientId, count);
        return count;
    }


    @RequestMapping("/consistHash")
    public Integer consistHash() throws Exception
    {
//        ObjectNode objectNode = objectMapper.createObjectNode();
//        objectNode.put("ok","ssss");
        //return objectNode;
        if (CapacitySum <=0)
            throw new Exception("no capacity");
        try
        {
            updateAgentlock.lock();
            int hashcode = r.nextInt(CapacitySum);

            for (int i =0; i< capacityList.length; i++)
            {
                hashcode -= capacityList[i];
                if (hashcode < 0)
                    return i;
            }
        }
        catch (Throwable e)
        {
            logger.error(e.getMessage());
        }
        finally
        {
            updateAgentlock.unlock();
        }

        //return 0;

        throw new Exception("should not happen");
    }

    @RequestMapping("/agentList")
    public List<Agent> agentList() throws Exception
    {
        return agentList;
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
                logger.debug("DynamicTask.MyRunnable.run() start");
                Integer tempSumSize = 0;
                Integer[] tempCapacityList = new Integer[agentList.size()];
                //update info
                for (int i=0; i< agentList.size(); i++)
                {
                    Agent agent = agentList.get(i);

                    Integer channelCapacityRemain = 0;
                    try
                    {
                        logger.debug(agent.getMonitorUri().toString());
                        JsonNode monitorInfo = restTemplate.getForObject(agent.getMonitorUri(), JsonNode.class);
                        logger.debug(monitorInfo);
                        Iterator<Map.Entry<String, JsonNode>> iter = monitorInfo.fields();
                        while (iter.hasNext())
                        {
                            Map.Entry<String, JsonNode> item = iter.next();
                            String key = item.getKey();
                            if (key.contains("mem"))
                            {
                                JsonNode menMonitor = item.getValue();
                                Integer channelCapacity = menMonitor.get("ChannelCapacity").asInt();
                                Integer channelSize = menMonitor.get("ChannelSize").asInt();
                                channelCapacityRemain = channelCapacity - channelSize;
                                break;
                                //logger.debug(channelCapacity);
                                //logger.debug(channelSize);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error(agent.toString() + e.getMessage());
                    }
                    finally
                    {

                        //agent.setCapacity(channelCapacityRemain);
                        tempSumSize += channelCapacityRemain;
                        tempCapacityList[i] = channelCapacityRemain;
                    }
                }
                //多鲜橙
                updateAgentlock.lock();
                CapacitySum = tempSumSize;
                capacityList = tempCapacityList;
                updateAgentlock.unlock();


                Thread.sleep(5000);
                logger.debug("DynamicTask.MyRunnable.run() end");
            } catch (Exception e)
            {
                logger.error(e);
            }

        }

    }


}
