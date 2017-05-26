package monkey.d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by monkey_d_asce on 17-5-26.
 */
public class testApp
{
    static final String host = "localhost";
    static final int port =  40999;
    static final String url = "http://" + host + ":" + port + "/";

    public static void main(String[] args) throws Exception
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
}
