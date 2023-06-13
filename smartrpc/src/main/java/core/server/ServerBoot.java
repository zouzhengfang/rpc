package core.server;

import com.google.common.util.concurrent.*;
import core.BeanFactoryUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import core.BeanFactoryUtils;
import core.RpcConfig;
import core.parallel.NamedThreadFactory;
import core.parallel.RPCThreadPool;
import model.RpcResponse;
import registry.ServiceRegister;
import serialize.MessageDecoder;
import serialize.MessageEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.*;

/**
 * 服务端配置类
 **/
public class ServerBoot {

    //工作事件循环组的线程数
    private static final int PARALLEL = RpcConfig.RPC_PROPERTY_PARALLEL * 2;

    private static final int threadNum = RpcConfig.RPC_PROPERTY_THREAD_NUM;

    private static final int queueNum = RpcConfig.RPC_PROPERTY_QUEUE_NUM;
    ThreadFactory threadRpcFactory = new NamedThreadFactory("EventLoopGroup ThreadFactory");
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup(PARALLEL, threadRpcFactory, SelectorProvider.provider());
    @Autowired
    private ProtocolProperties protocolProperties = BeanFactoryUtils.getBean("protocolProperties");
    private static volatile ListeningExecutorService threadPoolExecutor;

    private static class ServerBootHolder {
        static final ServerBoot INSTANCE = new ServerBoot();
    }

    public static ServerBoot getInstance() {
        return ServerBootHolder.INSTANCE;
    }

    protected int port;
    protected String host;

    public ServerBoot() {
        this.port = protocolProperties.getPort();
        this.host = protocolProperties.getHOST();
    }

    private Channel channel;

    /*
     * 开启服务
     * */
    public void start() {

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());
                            pipeline.addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = null;
            future = bootstrap.bind(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * 优雅关闭服务
     * */
    public void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    /*
     * 提交异步任务
     * */
    public static void submit(Callable<Boolean> task, final ChannelHandlerContext ctx) {
        if (threadPoolExecutor == null) {
            synchronized (ServerBoot.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator(
                            (ThreadPoolExecutor) (
                                    RPCThreadPool.getExecutor(threadNum, queueNum))
                    );
                }
            }
        }
        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ctx.writeAndFlush(((ServerTask) task).response);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, threadPoolExecutor);
    }
}
