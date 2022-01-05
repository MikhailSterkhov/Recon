package org.itzstonlex.recon.minecraft.packet.handshake;

import org.itzstonlex.recon.RemoteChannel;

public interface HandshakeInitializer<HS extends Handshake, R> {

    R init(RemoteChannel channel, HS handshake);
}
