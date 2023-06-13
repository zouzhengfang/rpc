package serialize;

import io.netty.buffer.ByteBuf;
import spi.SPI;

import java.io.IOException;
@SPI
public interface MessageCodec {
    void encode(final ByteBuf out, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
