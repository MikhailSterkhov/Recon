package org.itzstonlex.recon.minecraft.packet;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.factory.BufferFactory;

public abstract class MinecraftPacket {

    public void write(ByteStream.Output buffer) {
        throw new UnsupportedOperationException("write");
    }

    public void read(ByteStream.Input buffer) {
        throw new UnsupportedOperationException("read");
    }

    public boolean isWriteable() {
        try {
            write(BufferFactory.createPooledOutput());
            return true;
        }
        catch (UnsupportedOperationException exception) {
            return false;
        }
    }

    public boolean isReadable() {
        try {
            read(BufferFactory.createPooledInput(new byte[0]));
            return true;
        }
        catch (UnsupportedOperationException exception) {
            return false;
        }
    }

}
