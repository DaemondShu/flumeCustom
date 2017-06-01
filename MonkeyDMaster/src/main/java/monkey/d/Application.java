package monkey.d;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by monkey_d_asce on 17-5-26.
 */

@SpringBootApplication
public class Application
{
    //    public void run(String... args) {
    //        System.out.println("run over");
    //
    //    }

    public static void main(String[] args)
    {


        System.out.println("user dir:/n" + System.getProperty("user.dir"));

        for (String config : Arrays.asList("log4j.properties", "application.properties"))
        {
            try
            {
                PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + config);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "application.properties");

        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);

        //        SpringApplication.run(Application.class, args);
    }
}
