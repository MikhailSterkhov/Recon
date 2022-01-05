package org.itzstonlex.recon.minecraft.server;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerManager<Server extends MinecraftServer> {

    public static <Server extends MinecraftServer>
    ServerManager<Server> newManager(Class<Server> serverClass, HandshakeInitializer<ServerHandshake, Server> serverInit) {

        return new ServerManager<>(serverInit);
    }

    private final Map<String, Server> serversMap;
    private final HandshakeInitializer<ServerHandshake, Server> serverInit;

    private ServerManager(HandshakeInitializer<ServerHandshake, Server> serverInit) {
        this.serversMap = new ConcurrentHashMap<>();
        this.serverInit = serverInit;
    }

    public Server newServerInstance(RemoteChannel channel, ServerHandshake handshake) {
        return serverInit.init(channel, handshake);
    }

    public void connectServer(RemoteChannel channel, Server server) {
        synchronized (serversMap) {

            serversMap.put(server.getName().toLowerCase(), server);
            server.onConnected(channel);
        }
    }

    public void disconnectServer(RemoteChannel channel, Server server) {
        synchronized (serversMap) {

            serversMap.remove(server.getName().toLowerCase());
            server.onDisconnected(channel);
        }
    }

    public synchronized Server getServer(String name) {
        return serversMap.get(name.toLowerCase());
    }

    public synchronized Collection<Server> getConnectedServers() {
        return serversMap.values();
    }

}
