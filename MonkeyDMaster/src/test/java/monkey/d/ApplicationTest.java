package monkey.d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


import static org.junit.Assert.*;

/**
 * Created by monkey_d_asce on 17-5-26.
 */
public class ApplicationTest
{
    Logger logger = Logger.getLogger(ApplicationTest.class);
    static final String host = "localhost";
    static final int port =  40999;
    static final String url = "http://" + host + ":" + port + "/";
//    static final String
//    @BeforeClass
//    public void setUp() throws Exception
//    {
//        Application.main(null);
//    }

//    tutorial： http://howtodoinjava.com/spring/spring-restful/spring-restful-client-resttemplate-example/


    class Temp
    {

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        String name;
    }

    @Test
    public void testSync() throws Exception
    {
        RestTemplate temp = new RestTemplate();


        // String user = temp.postForObject(url + "test", t, String.class );

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "test")
                // Add query parameter
                .queryParam("name", "nn");

        for (int i = 0; i < 1; i++)
        {
            try
            {
                String user = temp.postForObject(builder.build().toUri(), null, String.class);
                assertEquals("test nn", user);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void testAsync() throws Exception
    {
        AsyncRestTemplate temp = new AsyncRestTemplate();



        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "test")
                // Add query parameter
                .queryParam("name", "nn");



        ListenableFuture<ResponseEntity<String>> future = temp.getForEntity(url+"test", String.class);
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                logger.error("=====rest response faliure======");
                throwable.printStackTrace();
                System.out.println("rua");
            }
            @Override
            public void onSuccess(ResponseEntity<String> stringResponseEntity)
            {
                logger.info("sss");
                String temp = stringResponseEntity.getBody();
                System.out.println("sss" + temp);
            }
        });

        System.out.println("==no wait");

//        如果主线程提前退出的话，就会收不到回调信息
        Thread.sleep(5000);


        //        for (int i = 0; i < 1; i++)
//        {
//            try
//            {
//                String user = temp.postForObject(builder.build().toUri(), null, String.class);
//                assertEquals("test nn", user);
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//        }


    }

    @Test
    public void path() throws Exception
    {
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("").getPath());

    }



    @Test
    public void accessAgent() throws Exception
    {
    }

}