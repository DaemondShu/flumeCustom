package monkey.d;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

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

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);

//        SpringApplication.run(Application.class, args);
    }
}
