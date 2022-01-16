package org.itzstonlex.recon.minecraft.packet.handshake;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;
import org.itzstonlex.recon.minecraft.server.ServerManager;
import org.itzstonlex.recon.minecraft.service.MinecraftManagementService;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.PlayerHandshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacketHandler;
import org.itzstonlex.recon.minecraft.packet.PacketHandler;
import org.itzstonlex.recon.minecraft.packet.handler.BossHandler;
import org.itzstonlex.recon.minecraft.player.MinecraftPlayer;
import org.itzstonlex.recon.minecraft.player.PlayerManager;

public class HandshakeHandler extends MinecraftPacketHandler {

    private final RemoteChannel channel;

    private final BossHandler bossHandler;
    private final MinecraftManagementService managementService;

    protected PlayerHandshake lastPlayerHandshake;
    protected ServerHandshake lastServerHandshake;

    public HandshakeHandler(RemoteChannel channel, BossHandler bossHandler, MinecraftManagementService managementService) {
        this.channel = channel;
        this.bossHandler = bossHandler;
        this.managementService = managementService;
    }

    @PacketHandler
    public void handle(PlayerHandshake handshake) {
        this.lastPlayerHandshake = handshake;

        initMinecraftPlayer(handshake);
    }

    @PacketHandler
    public void handle(ServerHandshake handshake) {
        this.lastServerHandshake = handshake;

        initMinecraftServer(handshake);
    }

    private void initMinecraftServer(ServerHandshake handshake) {
        ServerManager<MinecraftServer> serverManager = managementService.getServerManager();
        MinecraftServer minecraftServer = serverManager.newServerInstance(channel, handshake);

        switch (handshake.getStatus()) {

            case CONNECT: {
                serverManager.connectServer(channel, minecraftServer);

                bossHandler.addPacketHandler(minecraftServer);
                break;
            }

            case DISCONNECT: {
                serverManager.disconnectServer(channel, minecraftServer);
                break;
            }
        }
    }

    private void initMinecraftPlayer(PlayerHandshake handshake) {
        PlayerManager<MinecraftPlayer> playerManager = managementService.getPlayerManager();
        MinecraftPlayer minecraftPlayer = playerManager.newPlayerInstance(channel, handshake);

        switch (handshake.getStatus()) {

            case CONNECT: {
                playerManager.connectPlayer(channel, minecraftPlayer);
                break;
            }

            case DISCONNECT: {
                playerManager.disconnectPlayer(channel, minecraftPlayer);
                break;
            }
        }
    }

}
