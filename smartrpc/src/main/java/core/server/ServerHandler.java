package core.server;

import core.BeanFactoryUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import model.RpcRequest;
import model.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import model.Status;
import org.jboss.netty.channel.ChannelHandler;
import org.springframework.stereotype.Component;
import registry.RegistryProperties;
import registry.ServiceMetaData;
import registry.ServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import spi.ExtensionLoader;

/*
 * 服务提供者channel初始化
 * */
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private RegistryProperties registryProperties = BeanFactoryUtils.getBean("registryProperties");
    private ServiceRegister serviceRegister;

    public ServerHandler() {
        serviceRegister = ExtensionLoader.getExtensionLoader(ServiceRegister.class).getExtension(registryProperties.getRegistry());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        ServiceMetaData so = this.serviceRegister.getServiceMetaData(request.getInterfaceName());
        RpcResponse response = RpcResponse.builder().status(Status.SUCCESS).build();
        if (so == null) {
            response = RpcResponse.builder().status(Status.NOT_FOUND).build();
            ctx.writeAndFlush(response);
        } else {
            ServerTask task = new ServerTask(request, response, so);
            ServerBoot.submit(task, ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
