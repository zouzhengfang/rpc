package serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import core.BeanFactoryUtils;
import core.server.ProtocolProperties;
import org.jboss.netty.channel.ChannelHandler;
import spi.ExtensionLoader;
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<Object> {

    private ProtocolProperties protocolProperties = BeanFactoryUtils.getBean("protocolProperties");
    private MessageCodec messageCodec = null;

    public MessageEncoder() {
        this.messageCodec = ExtensionLoader.getExtensionLoader(MessageCodec.class)
                .getExtension(protocolProperties.getName());
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        messageCodec.encode(out, msg);
    }
}

