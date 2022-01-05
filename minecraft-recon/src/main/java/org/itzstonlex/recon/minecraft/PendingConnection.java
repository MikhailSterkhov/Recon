package org.itzstonlex.recon.minecraft;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;

import java.net.InetSocketAddress;

public interface PendingConnection {

    String getName();

    InetSocketAddress getAddress();

    RemoteChannel getChannel();

    void onConnected(RemoteChannel channel);

    void onDisconnected(RemoteChannel channel);

    void sendPacket(MinecraftPacket packet);
}
