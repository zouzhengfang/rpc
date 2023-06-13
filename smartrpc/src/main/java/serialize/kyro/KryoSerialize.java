package serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import io.netty.buffer.ByteBuf;
import serialize.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.Closer;

public class KryoSerialize implements MessageCodec {
    private static Closer closer = Closer.create();

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoPool pool = KryoPoolFactory.getKryoPoolInstance();
            Kryo kryo = pool.borrow();
            Output outPut = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(outPut, message);
            outPut.close();
            byteArrayOutputStream.close();
            pool.release(kryo);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            closer.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoPool pool = KryoPoolFactory.getKryoPoolInstance();
            Kryo kryo = pool.borrow();
            Input in = new Input(byteArrayInputStream);
            Object result = kryo.readClassAndObject(in);
            in.close();
            byteArrayInputStream.close();
            pool.release(kryo);
            return result;
        } finally {
            closer.close();
        }
    }
}
