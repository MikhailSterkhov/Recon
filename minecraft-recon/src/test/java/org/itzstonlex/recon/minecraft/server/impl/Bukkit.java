package org.itzstonlex.recon.minecraft.server.impl;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.CoreService;
import org.itzstonlex.recon.minecraft.MinecraftCore;
import org.itzstonlex.recon.minecraft.packet.PacketHandler;
import org.itzstonlex.recon.minecraft.packet.PlayerChat;
import org.itzstonlex.recon.minecraft.player.CorePlayer;
import org.itzstonlex.recon.minecraft.server.CoreServer;

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
