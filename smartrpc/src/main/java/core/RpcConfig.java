package core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.nio.channels.spi.SelectorProvider;

public class RpcConfig {
    public static final int RPC_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());
    public static final int RPC_PROPERTY_THREAD_NUM = 16;

    public static final int RPC_PROPERTY_QUEUE_NUM = -1;

    public static final String RPC_PROPERTY_ZK_SERVICE_PATH = "/smart-rpc";
}
