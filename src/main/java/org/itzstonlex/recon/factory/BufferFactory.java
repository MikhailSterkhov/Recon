package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.init.BufferInitializer;

public final class BufferFactory {

    public static final ByteStream BYTESTREAM
            = new BufferInitializer.PooledBuffer();

    public static ByteStream.Input createPooledInput(byte[] bytes) {
        return BYTESTREAM.newInput(bytes);
    }

    public static ByteStream.Output transformOutput(byte[] bytes) {
        return BYTESTREAM.transform(bytes);
    }

    public static ByteStream.Output transformOutput(ByteStream.Input input) {
        return BYTESTREAM.transform(input);
    }

    public static ByteStream.Output createPooledOutput() {
        return BYTESTREAM.newOutput();
    }

}
