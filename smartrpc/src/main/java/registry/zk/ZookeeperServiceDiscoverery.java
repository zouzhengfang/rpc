package registry.zk;

import com.alibaba.fastjson.JSON;
import core.BeanFactoryUtils;
import org.I0Itec.zkclient.ZkClient;
import core.RpcConfig;
import registry.DiscovererService;
import registry.RegistryProperties;
import registry.RpcTransportData;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZookeeperServiceDiscoverery implements DiscovererService {

    private RegistryProperties registryProperties=BeanFactoryUtils.getBean("registryProperties");
    private ZkClient zkClient;

    public ZookeeperServiceDiscoverery() {
        zkClient = new ZkClient(registryProperties.getAddress() + ":" + registryProperties.getPort());
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }

    /**
     * @param name 服务名->接口全路径
     * @Author lijl
     * @MethodName getService
     * @Description 使用Zookeeper客户端，通过服务名获取服务列表
     * @Date 17:07 2022/3/11
     * @Version 1.0
     * @return: java.util.List<com.huawei.rpc.common.info.ServiceInfo> 服务列表
     **/
    @Override
    public List<RpcTransportData> getService(String name) {
        //从zk中获取已注册的服务
        String servicePath = RpcConfig.RPC_PROPERTY_ZK_SERVICE_PATH + "/" + name + "/" + "service";
        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
            String deCh = null;
            try {
                deCh = URLDecoder.decode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSON.parseObject(deCh, RpcTransportData.class);
        }).collect(Collectors.toList());
    }
}
