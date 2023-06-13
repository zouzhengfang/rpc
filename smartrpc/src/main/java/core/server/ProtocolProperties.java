package core.server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
@ConfigurationProperties(prefix = "rpc.protocol")
public class ProtocolProperties {
    private final String NAME = "hessian";
    private final String HOST = "127.0.0.1";
    private final int PORT = 9527;
    private String name = NAME;
    private int port = PORT;

    private String host = HOST;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHOST() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
}
