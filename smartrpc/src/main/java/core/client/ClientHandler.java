package core.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import model.RpcRequest;
import model.RpcResponse;

import java.util.concurrent.CountDownLatch;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private CountDownLatch cdl;
    private RpcResponse response = null;
    private RpcRequest data;

    public ClientHandler(RpcRequest data) {
        this.cdl = new CountDownLatch(1);
        this.data = data;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(data);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = (RpcResponse) msg;
        cdl.countDown();
    }

    /**
     * @param
     * @Author lijl
     * @MethodName rspData
     * @Description 等待读取数据完成
     * @Date 17:02 2022/2/14
     * @Version 1.0
     * @return: java.lang.Object
     **/
    public RpcResponse ReadDataFinish() throws InterruptedException {
        cdl.await();
        return response;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
