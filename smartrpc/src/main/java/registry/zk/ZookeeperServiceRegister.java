package registry.zk;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.ZkClient;
import core.BeanFactoryUtils;
import core.RpcConfig;
import core.server.ProtocolProperties;
import registry.BaseServiceRegister;
import registry.RegistryProperties;
import registry.RpcTransportData;
import registry.ServiceMetaData;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.List;

public class ZookeeperServiceRegister extends BaseServiceRegister {


    private RegistryProperties registryProperties = BeanFactoryUtils.getBean("registryProperties");
    private ProtocolProperties protocolProperties = BeanFactoryUtils.getBean("protocolProperties");
    private ZkClient client;
    protected String protocol;
    protected int port;
    private String host;

    public ZookeeperServiceRegister() {
        this.client = new ZkClient(registryProperties.getAddress() + ":" + registryProperties.getPort());
        this.client.setZkSerializer(new ZookeeperSerializer());
        this.port = protocolProperties.getPort();
        this.host = protocolProperties.getHOST();
        this.protocol = protocolProperties.getName();
    }

    @Override
    public void register(List<ServiceMetaData> smdList) throws Exception {
        super.register(smdList);
        for (ServiceMetaData so : smdList) {
            RpcTransportData serviceInfo = new RpcTransportData();
            String address = host + ":" + port;
            serviceInfo.setAddress(address);
            serviceInfo.setName(so.getInterfacename());
            serviceInfo.setProtocol(protocol);
            this.exportService(serviceInfo);
        }
    }

    private void exportService(RpcTransportData serviceInfo) {
        String serviceName = serviceInfo.getName();
        String uri = JSON.toJSONString(serviceInfo);

        try {
            uri = URLEncoder.encode(uri, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = RpcConfig.RPC_PROPERTY_ZK_SERVICE_PATH + "/" + serviceName + "/" + "service";
        if (!client.exists(servicePath)) {
            client.createPersistent(servicePath, true);
        }
        String uriPath = servicePath + "/" + uri;
        if (client.exists(uriPath)) {
            client.delete(uriPath);
        }
        client.createEphemeral(uriPath);
    }
}
