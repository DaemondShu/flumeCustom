package monkey.d;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;

import static monkey.d.ServerController.CONFIG_MONITOR_PORT;
import static monkey.d.ServerController.CONFIG_SOURCE_PORT;

/**
 * Created by monkey_d_asce on 17-5-28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agent implements Serializable
{

     //jackson 在有初始构造函数的时候会执行初始构造函数，否则就看set的
    public Agent(@JsonProperty("ip")  String ip, @JsonProperty(CONFIG_SOURCE_PORT)Integer sourcePort, @JsonProperty(CONFIG_MONITOR_PORT)Integer monitorPort)
    {
        Preconditions.checkNotNull(ip);
        Preconditions.checkNotNull(sourcePort);
        Preconditions.checkNotNull(monitorPort);
        this.ip = ip;
        this.sourcePort = sourcePort;
        this.monitorPort = monitorPort;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://" + ip + ":"  + monitorPort + "/metrics");
        this.monitorUri = builder.build().toUri();
    }

    @JsonProperty("ip")
    public String getIp()
    {
        return ip;
    }

//    public void setIp(String ip)
//    {
//        this.ip = ip;
//    }

    //        @JsonProperty("ip")

    @JsonProperty(CONFIG_SOURCE_PORT)
    public Integer getSourcePort()
    {
        return sourcePort;
    }

//    public void setSourcePort(Integer sourcePort)
//    {
//        this.sourcePort = sourcePort;
//    }

    @JsonProperty(CONFIG_MONITOR_PORT)
    public Integer getMonitorPort()
    {
        return monitorPort;
    }

//    public void setMonitorPort(Integer monitorPort)
//    {
//        this.monitorPort = monitorPort;
//    }

    private String ip;
    private Integer sourcePort;
    private Integer monitorPort;


    @JsonIgnore
    private URI monitorUri;

    public URI getMonitorUri()
    {
        return monitorUri;
    }

    @JsonIgnore
    private Integer capacity;


    public Integer getCapacity()
    {
        return capacity;
    }

    public void setCapacity(Integer capacity)
    {
        this.capacity = capacity;
    }
}