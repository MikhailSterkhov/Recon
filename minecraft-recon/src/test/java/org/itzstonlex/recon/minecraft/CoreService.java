package org.itzstonlex.recon.minecraft;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.PlayerHandshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.player.CorePlayer;
import org.itzstonlex.recon.minecraft.player.PlayerManager;
import org.itzstonlex.recon.minecraft.server.CoreServer;
import org.itzstonlex.recon.minecraft.server.ServerManager;
import org.itzstonlex.recon.minecraft.server.impl.Bukkit;
import org.itzstonlex.recon.minecraft.server.impl.Proxy;
import org.itzstonlex.recon.minecraft.service.MinecraftManagementService;

import java.net.InetSocketAddress;
import java.util.UUID;

@SuppressWarnings("unchecked")
public final class CoreService implements MinecraftManagementService {

    private final MinecraftCore minecraftCore;

    private final PlayerManager<CorePlayer> playerManager = PlayerManager.newManager(CorePlayer.class, new PlayerInitializer());
    private final ServerManager<CoreServer> serverManager = ServerManager.newManager(CoreServer.class, new ServerInitializer());

    public CoreService(MinecraftCore minecraftCore) {
        this.minecraftCore = minecraftCore;
    }

    @Override
    public PlayerManager<CorePlayer> getPlayerManager() {
        return playerManager;
    }

    @Override
    public ServerManager<CoreServer> getServerManager() {
        return serverManager;
    }


    private final class PlayerInitializer
            implements HandshakeInitializer<PlayerHandshake, CorePlayer> {

        @Override
        public CorePlayer init(RemoteChannel channel, PlayerHandshake handshake) {
            UUID uniqueId = handshake.getUniqueId();

            String playerName = handshake.getName();
            InetSocketAddress address = handshake.getAddress();

            CorePlayer instance = new CorePlayer(uniqueId, playerName, address);

            // Init non-final other data.
            instance.init(serverManager,
                    handshake.getDisplayName(), handshake.getProxyServer(), handshake.getCurrentServer()
            );

            return instance;
        }
    }

    private final class ServerInitializer
            implements HandshakeInitializer<ServerHandshake, CoreServer> {

        @Override
        public CoreServer init(RemoteChannel channel, ServerHandshake handshake) {
            String serverName = handshake.getName();
            InetSocketAddress address = handshake.getAddress();

            CoreServer coreServer;

            if (handshake.isProxy()) {
                coreServer = new Proxy(minecraftCore, channel, serverName, address);

            } else {

                coreServer = new Bukkit(minecraftCore, channel, serverName, address);
            }

            return coreServer;
        }
    }

}
