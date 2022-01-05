package org.itzstonlex.recon.minecraft.service;

import org.itzstonlex.recon.minecraft.player.MinecraftPlayer;
import org.itzstonlex.recon.minecraft.player.PlayerManager;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;
import org.itzstonlex.recon.minecraft.server.ServerManager;

public interface MinecraftManagementService {

    <Player extends MinecraftPlayer> PlayerManager<Player> getPlayerManager();

    <Server extends MinecraftServer> ServerManager<Server> getServerManager();
}
