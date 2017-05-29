package monkey.d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.primitives.UnsignedInteger;
import org.apache.log4j.Logger;
import org.apache.tomcat.jni.Thread;
import org.hibernate.validator.internal.util.logging.Log;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by monkey_d_asce on 17-5-29.
 */
public class ServerControllerTest
{


    Logger logger = Logger.getLogger(ServerControllerTest.class);
    static final String host = "localhost";
    static final int port =  40999;
    static final String url = "http://" + host + ":" + port + "/";

    @Test
    public void greeting() throws Exception
    {

    }

    @Test
    public void threadPoolTaskScheduler() throws Exception
    {

    }

    @Test
    public void loadAgentsConfig() throws Exception
    {

    }

    @Test
    public void loadAgentsConfig1() throws Exception
    {

    }

    @Test
    public void startCron() throws Exception
    {

    }

    @Test
    public void roundRobin() throws Exception
    {
        RestTemplate temp = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "roundRobin")
                // Add query parameter
                .queryParam("clientId", 1 );

        long start = System.currentTimeMillis();
        //失败的就会报错
        for (int i = 0; i < 3000; i++)
        {
            Integer agentId = null;
            try
            {
                agentId = temp.postForObject(builder.build().toUri(),null, Integer.class);

            }catch (Exception e)  //链接失败或者一些奇怪的错误
            {
                logger.error(e.getMessage());
            }finally
            {
                assertEquals(new Integer(i % 2), agentId);
            }
        }
        logger.info(System.currentTimeMillis() - start);
    }

    @Test
    public void consistHash() throws Exception
    {
        RestTemplate temp = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "consistHash");

        long start = System.currentTimeMillis();
        //失败的就会报错
        int[] counter = new int[2];
        counter[0] = 0;
        counter[1] = 1;
        for (int i = 0; i < 3000; i++)
        {
            Integer agentId = null;
            try
            {
                agentId = temp.postForObject(builder.build().toUri(),null, Integer.class);
                //logger.info(agentId);
                counter[agentId] += 1;

            }catch (Exception e)  //链接失败或者一些奇怪的错误
            {
                logger.error(e.getMessage());
            }finally
            {
            }
        }
        logger.info("0: "+ counter[0]);
        logger.info("1: "+ counter[1]);
        logger.info("time：" + (System.currentTimeMillis() - start));

        //3000条 5846
    }

    @Test
    public void agentList() throws Exception
    {
        RestTemplate temp = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "agentList")
                // Add query parameter
                .queryParam("clientId", 1 );

        long start = System.currentTimeMillis();
        //失败的就会报错

        String ok = temp.getForObject(builder.build().toUri(), String.class);
        logger.info(ok);

        Agent[] agentList = temp.getForObject(builder.build().toUri(), Agent[].class);

        logger.info(agentList);

        //Class = new ParameterizedTypeReference<List<Agent>>(){};

        Agent agent0 = agentList[0];

        logger.info(agent0.getMonitorUri());

        logger.info(System.currentTimeMillis() - start);

        JsonNode monitorInfo = temp.getForObject(agent0.getMonitorUri(), JsonNode.class);
        logger.info(monitorInfo);

        Iterator<Map.Entry<String, JsonNode>> iter = monitorInfo.fields();
        while (iter.hasNext())
        {
            Map.Entry<String, JsonNode> item = iter.next();
            String key = item.getKey();
            if (key.indexOf("mem") >= 0)
            {
                JsonNode menMonitor = item.getValue();
                Double channelCapacity = menMonitor.get("ChannelCapacity").asDouble();
                Double channelSize = menMonitor.get("ChannelSize").asDouble();
                Double channelCapacityRemain = channelCapacity - channelSize;

                break;
                //logger.debug(channelCapacity);
                //logger.debug(channelSize);
            }
        }

    }

    public static int hashCode(long... a) {
        if (a == null)
            return 0;

        int result = 1;
        for (long element : a) {
            int elementHash = (int)(element ^ (element >>> 32));
            result = 31 * result + elementHash;
        }

        return result;
    }



    @Test
    public void testHashCode() throws Exception
    {
        System.currentTimeMillis();

        logger.info(hashCode(System.currentTimeMillis(), 2) % 50000);
        java.lang.Thread.sleep(10);
        logger.info(hashCode(System.currentTimeMillis(), 2) % 50000);
        java.lang.Thread.sleep(10);
        logger.info(hashCode(System.currentTimeMillis(), 2) % 50000);

        java.util.Random r = new java.util.Random();
        int max=5000;
        int min=0;

        for (int i = 0; i < 10; i++)
        {
            System.out.println(r.nextInt(max));
        }

        System.out.println((new Integer[4]).length);
    }

}