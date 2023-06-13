package core.client;

import core.BeanFactoryUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import core.BeanFactoryUtils;
import core.server.ServerBoot;
import model.RpcRequest;
import model.RpcResponse;
import registry.DiscovererService;
import registry.RegistryProperties;
import registry.RpcTransportData;
import registry.ServiceRegister;
import serialize.MessageDecoder;
import serialize.MessageEncoder;
import spi.ExtensionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;

/*
 * 客户端配置类
 * */
public class ClientBoot {


    private RegistryProperties registryProperties = BeanFactoryUtils.getBean("registryProperties");
    ;
    private DiscovererService discovererService;

    private static class ClientBootHolder {
        static final ClientBoot INSTANCE = new ClientBoot();
    }

    public ClientBoot() {
        discovererService = ExtensionLoader.getExtensionLoader(DiscovererService.class).getExtension(registryProperties.getRegistry());
    }

    public static ClientBoot getInstance() {
        return ClientBoot.ClientBootHolder.INSTANCE;
    }

    public <T> T getProxy(Class<T> clazz, String version) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new ClientInvocationHandler(clazz, version));
    }

    private class ClientInvocationHandler implements InvocationHandler {

        private Class<?> aClass;
        private String version;

        public ClientInvocationHandler(Class<?> aClass, String version) {
            super();
            this.aClass = aClass;
            this.version = version;
        }

        private Random random = new Random();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if ("toString".equals(methodName)) {
                return proxy.getClass().toString();
            }
            if (("hashCode").equals(methodName)) {
                return 0;
            }
            //1、获取服务信息
            String serviceName = this.aClass.getSimpleName();
            if (StringUtils.hasLength(this.version)) {
                serviceName += ":" + version;
            }
            List<RpcTransportData> serviceInfoList = discovererService.getService(serviceName);

            if (serviceInfoList == null || serviceInfoList.isEmpty()) {
                throw new ClassCastException("No provider available");
            }
            //多个服务的话随机获取一个
            RpcTransportData serviceInfo = serviceInfoList.get(random.nextInt(serviceInfoList.size()));

            //2、构造request对象
            RpcRequest request = new RpcRequest();
            request.setInterfaceName(serviceInfo.getName());
            request.setMethodName(methodName);
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            //3、条用网络层发送请求
            RpcResponse response = sendRequest(request, serviceInfo);

            //4、处理结果
            if (response.getException() != null) {
                throw response.getException();
            }
            return response.getReturnValue();
        }

        public RpcResponse sendRequest(RpcRequest data, RpcTransportData serviceInfo) throws InterruptedException {
            String[] addrInfoArray = serviceInfo.getAddress().split(":");
            String serverAddress = addrInfoArray[0];
            String serverPort = addrInfoArray[1];
            ClientHandler sendHandler = new ClientHandler(data);
            RpcResponse respData;
            NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new MessageEncoder());
                                pipeline.addLast(new MessageDecoder());
                                pipeline.addLast(sendHandler);
                            }
                        });
                bootstrap.connect(serverAddress, Integer.parseInt(serverPort)).sync();
                respData = sendHandler.ReadDataFinish();
            } finally {
                //释放线程组资源
                group.shutdownGracefully();
            }
            return respData;
        }
    }
}
