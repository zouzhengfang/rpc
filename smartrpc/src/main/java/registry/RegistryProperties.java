package registry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc.registry")
public class RegistryProperties {
   /* private final String Registry = "zk";
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 2181;*/

    private String address;
    private int port;
    private String registry;
/*
    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }*/
}
