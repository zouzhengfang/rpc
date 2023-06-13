package serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import core.BeanFactoryUtils;
import core.server.ProtocolProperties;
import org.jboss.netty.channel.ChannelHandler;
import spi.ExtensionLoader;
import spi.SPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
@ChannelHandler.Sharable
public class MessageDecoder extends ByteToMessageDecoder {

    private ProtocolProperties protocolProperties = BeanFactoryUtils.getBean("protocolProperties");
    ;

    private MessageCodec messageCodec;

    public MessageDecoder() {
        this.messageCodec = ExtensionLoader.getExtensionLoader(MessageCodec.class)
                .getExtension(protocolProperties.getName());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        in.markReaderIndex();
        int messageLength = in.readInt();

        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object obj = messageCodec.decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
                Logger.getLogger(MessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

