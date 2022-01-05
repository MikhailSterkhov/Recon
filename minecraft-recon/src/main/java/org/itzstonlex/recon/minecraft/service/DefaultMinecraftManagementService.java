package org.itzstonlex.recon.minecraft.service;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.PlayerHandshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.player.MinecraftPlayer;
import org.itzstonlex.recon.minecraft.player.PlayerManager;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;
import org.itzstonlex.recon.minecraft.server.ServerManager;

import java.net.InetSocketAddress;
import java.util.UUID;

@SuppressWarnings("unchecked")
public final class DefaultMinecraftManagementService implements MinecraftManagementService {

    public static DefaultMinecraftManagementService create(ReconMinecraftRegistry registry) {
        return new DefaultMinecraftManagementService(registry);
    }

    private final ReconMinecraftRegistry registry;

    private DefaultMinecraftManagementService(ReconMinecraftRegistry registry) {
        this.registry = registry;
    }

    private final PlayerManager<MinecraftPlayer> playerManager = PlayerManager.newManager(MinecraftPlayer.class, new PlayerInitializer());
    private final ServerManager<MinecraftServer> serverManager = ServerManager.newManager(MinecraftServer.class, new ServerInitializer());

    @Override
    public PlayerManager<MinecraftPlayer> getPlayerManager() {
        return playerManager;
    }

    @Override
    public ServerManager<MinecraftServer> getServerManager() {
        return serverManager;
    }


    private final class PlayerInitializer
            implements HandshakeInitializer<PlayerHandshake, MinecraftPlayer> {

        @Override
        public MinecraftPlayer init(RemoteChannel channel, PlayerHandshake handshake) {
            UUID uniqueId = handshake.getUniqueId();

            String playerName = handshake.getName();
            InetSocketAddress address = handshake.getAddress();

            MinecraftPlayer instance = new MinecraftPlayer(uniqueId, playerName, address);

            // Init non-final other data.
            instance.init(serverManager,
                    handshake.getDisplayName(), handshake.getProxyServer(), handshake.getCurrentServer()
            );

            return instance;
        }
    }

    private final class ServerInitializer
            implements HandshakeInitializer<ServerHandshake, MinecraftServer> {

        @Override
        public MinecraftServer init(RemoteChannel channel, ServerHandshake handshake) {
            String serverName = handshake.getName();
            String mainWorld = handshake.getMainWorld();

            InetSocketAddress address = handshake.getAddress();

            return new MinecraftServer(registry, channel, serverName, mainWorld, address);
        }
    }
}
