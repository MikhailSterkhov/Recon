package org.itzstonlex.recon.minecraft.packet;

import org.itzstonlex.recon.ByteStream;

public abstract class MinecraftPacket {

    public void write(ByteStream.Output buffer) {
        throw new UnsupportedOperationException("write");
    }

    public void read(ByteStream.Input buffer) {
        throw new UnsupportedOperationException("read");
    }

    public boolean isWriteable() {
        try {
            write(null);
            return true;
        }
        catch (UnsupportedOperationException exception) {
            return false;
        }
        catch (NullPointerException exception) {
            return true;
        }
    }

    public boolean isReadable() {
        try {
            read(null);
            return true;
        }
        catch (UnsupportedOperationException exception) {
            return false;
        }
        catch (NullPointerException exception) {
            return true;
        }
    }

}
