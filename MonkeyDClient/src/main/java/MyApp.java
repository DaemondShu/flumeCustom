/**
 * Created by monkey_d_asce on 17-5-11.
 */


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import com.sun.corba.se.spi.ior.ObjectId;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class MyApp
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyApp.class);


    @Parameter(names = {"-n", "--num"})
    int num = 10000;

    @Parameter(names = {"-b", "--batchSize"})   //batch batchSize
            int batchSize = 100;

    @Parameter(names = {"-s", "--singleDataSize"})
    int singleDataSize = 10;

    @Parameter(names = {"-d", "--dataItem"})
    String dataItem = "102,110,130,140,qqq,789\n";

    @Parameter(names = {"-h", "--help"}, help = true)
    boolean help = false;

    @Parameter(names = {"-t", "--timeIntervalms"})
    long logTimeInterval = 5000;  //5s


    private static final int clientId;

    static
    {
        try
        {
            final int machinePiece;
            {
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements())
                {
                    NetworkInterface ni = e.nextElement();
                    sb.append(ni.toString());
                }
                machinePiece = sb.toString().hashCode() << 16;
                LOGGER.info("machine piece post: " + Integer.toHexString(machinePiece));
            }
            final int processPiece;
            {
                int processId = new java.util.Random().nextInt();
                try
                {
                    processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
                } catch (Throwable t)
                {
                }
                ClassLoader loader = ObjectId.class.getClassLoader();
                int loaderId = loader != null ? System.identityHashCode(loader) : 0;
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(processId));
                sb.append(Integer.toHexString(loaderId));
                processPiece = sb.toString().hashCode() & 0xFFFF;
                LOGGER.info("process piece: " + Integer.toHexString(processPiece));
            }
            clientId = machinePiece | processPiece;
            LOGGER.info("machine : " + Integer.toHexString(clientId));
        } catch (java.io.IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }


    public static void main(String[] args)
    {
        MyApp myApp = new MyApp();
        JCommander commandParser = JCommander.newBuilder().addObject(myApp).build();
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
        while (singleDataBuilder.length() < singleDataSize)
        {
            singleDataBuilder.append(dataItem);
        }

        String singleData = singleDataBuilder.toString();
        Event singleEvent = EventBuilder.withBody(singleData, Charset.forName("UTF-8"));


        List<Event> events = new ArrayList<Event>();

        for (int bb = 0; bb < batchSize; bb++)
            events.add(singleEvent);


        LOGGER.info("start, singleDataSize: {}", singleData.length());

        MyRpcClientFacade client = new MyRpcClientFacade();
        // Initialize client with the remote Flume agent's host and port
        client.init("localhost", 44444);

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        long failNum = 0;
        for (int i = 0; i < num; i += batchSize)
        {

            if (!client.appendBatchEvent(events))
            {
                failNum += batchSize;
            }

            long curr = System.currentTimeMillis();
            if (curr - previousTime > logTimeInterval)
            {
                previousTime = curr;
                LOGGER.info("done {} {}", i, curr - startTime);
            }
        }
        long endTime = System.currentTimeMillis();

        ObjectNode resultBuilder = new ObjectMapper().createObjectNode();

        resultBuilder.put("time", endTime - startTime);
        resultBuilder.put("fail", failNum);
        resultBuilder.put("num", num);
        resultBuilder.put("batchSize", batchSize);
        resultBuilder.put("singleDataSize", singleDataSize);
        resultBuilder.put("totalMB", (float) (singleDataSize * num) / 1000.0f / 1000.0f);

        LOGGER.info("over: {}", resultBuilder.toString());


        //10 个 file 节点 133932ms n=10000 b=1 s=25

        /*
        1 1000*1(s=1000) 9838
        1 10000/100*10  1500
        1 100000/

        1 time: 4008, props:{"num":100000, "batchSize":500, "singleDataSize":100} 10m

        1 time: 122916, props:{"num":100000, "batchSize":10, "singleDataSize":1000, "totalBytes":100000000}
        1 time: 28183, props:{"num":100000, "batchSize":100, "singleDataSize":1000, }
        1 time: 19278, props:{"num":100000, "batchSize":200, "singleDataSize":1000, "totalMB":100.0}
        1 time: 16886, props:{"num":100000, "batchSize":300, "singleDataSize":1000, "totalMB":100.00}   b>300 网路因素可以忽略不计
        1 time: 19000 props:{"num":100000, "batchSize":500, "singleDataSize":1000}
        1 time: 18438  props:{"num":100000, "batchSize":1000, "singleDataSize":1000 }
        */


        client.cleanUp();
    }

    final static DecimalFormat df = new DecimalFormat(".##");

    @Override
    public String toString()
    {

        return "{" +
                "\"num\":" + num +
                ", \"batchSize\":" + batchSize +
                ", \"singleDataSize\":" + singleDataSize +
                ", \"totalMB\":" + df.format((float) (singleDataSize * num) / 1000.0f / 1000.0f) +
                '}';
    }
}