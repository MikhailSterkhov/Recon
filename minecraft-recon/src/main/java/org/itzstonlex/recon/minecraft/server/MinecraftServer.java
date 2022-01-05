package org.itzstonlex.recon.minecraft.server;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.minecraft.PendingConnection;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacketHandler;

import java.net.InetSocketAddress;

public class MinecraftServer
        extends MinecraftPacketHandler implements PendingConnection {

    protected final ReconMinecraftRegistry minecraftRegistry;

    protected final String name;

    protected final RemoteChannel channel;
    protected final InetSocketAddress address;

    protected final boolean isProxy;

    public MinecraftServer(ReconMinecraftRegistry minecraftRegistry, RemoteChannel channel,
                           boolean isProxy, String name, InetSocketAddress address) {

        this.minecraftRegistry = minecraftRegistry;

        this.name = name;
        this.isProxy = isProxy;

        this.channel = channel;
        this.address = address;
    }

    public boolean isProxy() {
        return isProxy;
    }

    @Override
    public void onConnected(RemoteChannel channel) {
        channel.logger().info(String.format("[MinecraftRecon] Server %s success connected as %s!", address, name));
    }

    @Override
    public void onDisconnected(RemoteChannel channel) {
        channel.logger().info(String.format("[MinecraftRecon] Server %s was disconnected!", name));
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public RemoteChannel getChannel() {
        return channel;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        if (channel == null || channel.isClosed() || !packet.isWriteable()) {
            return;
        }

        int packetID = minecraftRegistry.getRegisteredPacketId(packet.getClass());
        if (packetID < 0) {
            return;
        }

        ByteStream.Output buffer = BufferFactory.createPooledOutput();
        buffer.writeInt(packetID);

        packet.write(buffer);

        channel.write(buffer);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("@MinecraftServer");
        stringBuilder.append("[");

        // Server name.
        stringBuilder.append("name=");
        stringBuilder.append(name);

        stringBuilder.append(", ");

        // Server address.
        stringBuilder.append("address=");
        stringBuilder.append(address);

        // Server online
        stringBuilder.append(", ");

        stringBuilder.append("isProxy=");
        stringBuilder.append(isProxy);

        // Endpoint.
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
