package org.itzstonlex.recon.minecraft.packet.handshake.impl;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.minecraft.packet.handshake.Handshake;

import java.net.InetSocketAddress;

public class ServerHandshake extends Handshake {

    private boolean isProxy;

    public ServerHandshake() {
        super();
    }

    public ServerHandshake(int version, String name, InetSocketAddress address,

                           // Server variables.
                           boolean isProxy) {

        super(version, name, address);
        this.isProxy = isProxy;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        super.write(buffer);

        buffer.writeBoolean(isProxy);
    }

    @Override
    public void read(ByteStream.Input buffer) {
        super.read(buffer);

        this.isProxy = buffer.readBoolean();
    }

    public boolean isProxy() {
        return isProxy;
    }
}
