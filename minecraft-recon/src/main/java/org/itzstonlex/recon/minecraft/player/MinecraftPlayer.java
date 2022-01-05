package org.itzstonlex.recon.minecraft.player;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.PendingConnection;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;
import org.itzstonlex.recon.minecraft.server.ServerManager;

import java.net.InetSocketAddress;
import java.util.UUID;

public class MinecraftPlayer implements PendingConnection {

    protected final UUID uniqueId;

    protected final InetSocketAddress address;

    protected final String name;
    protected String displayName;

    protected MinecraftServer proxyServer;
    protected MinecraftServer currentServer;

    public MinecraftPlayer(UUID uniqueId, String name, InetSocketAddress address) {
        this.uniqueId = uniqueId;
        this.address = address;
        this.name = name;
    }

    public void init(ServerManager<? extends MinecraftServer> serverManager,
                     String displayName, String proxyServer, String currentServer) {

        this.displayName = displayName;

        this.proxyServer = serverManager.getServer(proxyServer);
        this.currentServer = serverManager.getServer(currentServer);
    }

    @Override
    public void onConnected(RemoteChannel channel) {
        channel.logger().info(String.format("[MinecraftRecon] Player %s success connected%s!", name, (currentServer != null ? " on " + currentServer.getName() : "")));
    }

    @Override
    public void onDisconnected(RemoteChannel channel) {
        channel.logger().info(String.format("[MinecraftRecon] Player %s was disconnected!", name));
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
        return currentServer != null ? currentServer.getChannel() : null;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        if (currentServer != null) {
            currentServer.sendPacket(packet);
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MinecraftServer getProxy() {
        return proxyServer;
    }

    public MinecraftServer getServer() {
        return currentServer;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("@MinecraftPlayer");
        stringBuilder.append("[");

        // Server online
        stringBuilder.append("uuid=");
        stringBuilder.append(uniqueId.toString());

        stringBuilder.append(", ");

        // Server name.
        stringBuilder.append("name=");
        stringBuilder.append(name);

        stringBuilder.append(", ");

        // Server address.
        stringBuilder.append("address=");
        stringBuilder.append(address);

        // Endpoint.
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
