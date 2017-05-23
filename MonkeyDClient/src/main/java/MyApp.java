/**
 * Created by monkey_d_asce on 17-5-11.
 *
 */



import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyApp
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApp.class);


    @Parameter(names = {"-n", "--num"} )
    int num = 10000;

    @Parameter(names = {"-s", "--size"})   //batch size
    int size = 1;

    @Parameter(names = {"-b", "--bytes"})
    int bytes = 100;

    @Parameter(names = {"-d", "--dataItem"})
    String dataItem = "102,110,130,140,qqq,789\n";

    @Parameter(names = { "-h" ,"--help"} , help = true)
    boolean help = false;

    @Parameter(names = {"-t", "--timeIntervalms"})
    long logTimeInterval = 5000;  //10s



    public static void main(String[] args)
    {
        MyApp myApp = new MyApp();
        JCommander commandParser =  JCommander.newBuilder().addObject(myApp).build();
        commandParser.parse(args);
        if (myApp.help)
        {
            commandParser.usage();
            return;
        }

        myApp.run();
    }


    public void run()
    {










        // Send 10 events to the remote Flume agent. That agent should be
        // configured to listen with an AvroSource.
        StringBuilder singleDataBuilder = new StringBuilder();
        while (singleDataBuilder.length() < bytes )
        {
            singleDataBuilder.append(dataItem);
        }

        String sampleData = singleDataBuilder.toString();

        LOGGER.info("start, sampleData: {}", sampleData);

        MyRpcClientFacade client = new MyRpcClientFacade();
        // Initialize client with the remote Flume agent's host and port
        client.init("localhost", 44444);

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        for (int i = 0; i < num; i++)
        {
            client.sendDataToFlume(sampleData);

            long curr = System.currentTimeMillis();
            if (curr - previousTime > logTimeInterval)
            {
                previousTime = curr;
                LOGGER.info("done {} {}", i, curr - startTime);
            }
        }
        long endTime = System.currentTimeMillis();

        LOGGER.info("done total:  {}", endTime - startTime);
        //10 个 file 节点 133932ms 10000次

        /*
        1000*1000 9838


         */

        client.cleanUp();
    }

}