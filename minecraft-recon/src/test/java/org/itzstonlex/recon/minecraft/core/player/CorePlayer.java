package org.itzstonlex.recon.minecraft.core.player;

import org.itzstonlex.recon.minecraft.packet.PlayerChat;
import org.itzstonlex.recon.minecraft.packet.PlayerRedirect;
import org.itzstonlex.recon.minecraft.player.MinecraftPlayer;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;

import java.net.InetSocketAddress;
import java.util.UUID;

public class CorePlayer extends MinecraftPlayer {

    public CorePlayer(UUID uniqueId, String name, InetSocketAddress address) {
        super(uniqueId, name, address);
    }

    public void redirect(MinecraftServer server) {
        if (server.isProxy()) {
            return;
        }

        super.sendPacket( new PlayerRedirect(name, server.getName()) );
        this.currentServer = server;
    }

    public void sendMessage(String message) {
        currentServer.sendPacket(new PlayerChat(name, message));
    }

}
