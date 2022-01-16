package org.itzstonlex.recon.minecraft.core.server.impl;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.core.CoreService;
import org.itzstonlex.recon.minecraft.core.MinecraftCore;
import org.itzstonlex.recon.minecraft.core.player.CorePlayer;
import org.itzstonlex.recon.minecraft.core.server.CoreServer;
import org.itzstonlex.recon.minecraft.packet.PacketHandler;
import org.itzstonlex.recon.minecraft.packet.PlayerChat;

import java.net.InetSocketAddress;

public class Bukkit extends CoreServer {

    public Bukkit(MinecraftCore minecraftCore, RemoteChannel channel,
                  String name, InetSocketAddress address) {

        super(minecraftCore, channel, false, name, address);
    }

    @PacketHandler
    public void handle(PlayerChat packet) {
        CoreService coreService = super.getService();

        CorePlayer player = coreService.getPlayerManager().getPlayer(packet.getPlayerName());
        String message = packet.getMessage();

        if (player != null) {
            player.sendMessage(message);
        }
    }

}
