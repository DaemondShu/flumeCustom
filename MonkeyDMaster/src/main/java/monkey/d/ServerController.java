package monkey.d;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/**
 * Created by monkey_d_asce on 17-5-26.
 */
@RestController
public class ServerController
{
    @RequestMapping("/test")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name)
    {
        System.out.println(name);
        return "test " + name;
    }


    @RequestMapping("/async")
    public Callable<String> api() {
        System.out.println("=====hello");
        return new Callable<String>() {
            @Override
            public String call() throws Exception {

                return "ttt";
            }
        };
    }


}
