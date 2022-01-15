package org.itzstonlex.recon.minecraft.api;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.service.MinecraftManagementService;
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeHandler;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.PlayerHandshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.packet.handler.BossHandler;
import org.itzstonlex.recon.minecraft.player.PlayerManager;
import org.itzstonlex.recon.minecraft.server.ServerManager;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

public final class ReconMinecraftApi {

    public static final class ServiceFactory {

        public static ServiceFactory create(
                MinecraftManagementService managementService,
                BiConsumer<BossHandler, ChannelConfig> channelInitializer
        ) {
            return new ServiceFactory(managementService, channelInitializer);
        }

        private final MinecraftManagementService managementService;
        private final BiConsumer<BossHandler, ChannelConfig> channelInitializer;

        private ServiceFactory(
                MinecraftManagementService managementService,
                BiConsumer<BossHandler, ChannelConfig> channelInitializer
        ) {
            this.managementService = managementService;
            this.channelInitializer = channelInitializer;
        }

        public MinecraftManagementService getManagementService() {
            return managementService;
        }

        public BiConsumer<BossHandler, ChannelConfig> getChannelInitializer() {
            return channelInitializer;
        }
    }

    private final class ChannelConfigInitializer{

        private void updateOptions(ChannelConfig config) {
            config.option(ChannelOption.of(ChannelOption.Type.IP_TOS, 0x18));
            config.option(ChannelOption.of(ChannelOption.Type.SO_SNDBUF, 262_144));
            config.option(ChannelOption.of(ChannelOption.Type.TCP_NODELAY, true));
        }

        private void registerHandshake() {
            registry.registerPacket(0, ServerHandshake::new);
            registry.registerPacket(1, PlayerHandshake::new);
        }

        private void init(ServiceFactory serviceFactory, ChannelConfig config) {

            // Change channel options.
            updateOptions(config);

            // Add packet-handlers.
            BossHandler bossHandler = new BossHandler(registry);
            bossHandler.addPacketHandler(new HandshakeHandler(config.pipeline().channel(), bossHandler, serviceFactory.getManagementService()));

            config.pipeline().addLast("@boss-handler", bossHandler);

            // Register handshake packets.
            registerHandshake();

            // Accept thread-handler.
            if (serviceFactory.getChannelInitializer() != null) {
                serviceFactory.getChannelInitializer().accept(bossHandler, config);
            }
        }
    }

    public final class MinecraftApplication {

        public RemoteChannel bindLocal(int port, ServiceFactory serviceFactory) {
            return bind("127.0.0.1", port, serviceFactory);
        }

        public RemoteChannel bind(String host, int port, ServiceFactory serviceFactory) {
            return bind(new InetSocketAddress(host, port), serviceFactory);
        }

        public RemoteChannel bind(InetSocketAddress address, ServiceFactory serviceFactory) {

            // Bind a minecraft application.
            Server server = new Server();
            RemoteChannel channel = server.bind(address, config -> {

                ChannelConfigInitializer initializer = new ChannelConfigInitializer();
                initializer.init(serviceFactory, config);
            });

            // Initialize management service.
            initManagements(serviceFactory.getManagementService());

            return channel;
        }


        public RemoteChannel connectLocal(int port, ServiceFactory serviceFactory) {
            return connect("127.0.0.1", port, serviceFactory);
        }

        public RemoteChannel connect(String host, int port, ServiceFactory serviceFactory) {
            return connect(new InetSocketAddress(host, port), serviceFactory);
        }

        public RemoteChannel connect(InetSocketAddress address, ServiceFactory serviceFactory) {

            // Connect to minecraft application.
            Client client = new Client();
            RemoteChannel channel = client.connect(address, config -> {

                ChannelConfigInitializer initializer = new ChannelConfigInitializer();
                initializer.init(serviceFactory, config);
            });

            // Initialize management service.
            initManagements(serviceFactory.getManagementService());

            return channel;
        }
    }

    public MinecraftApplication newMinecraftApplication() {
        return new MinecraftApplication();
    }


    private final ReconMinecraftRegistry registry = new ReconMinecraftRegistry();
    private MinecraftManagementService managementService;

    private void initManagements(MinecraftManagementService managementService) {
        this.managementService = managementService;
    }

    public PlayerManager<?> getPlayerManager() {
        return managementService.getPlayerManager();
    }

    public ServerManager<?> getServerManager() {
        return managementService.getServerManager();
    }

    public ReconMinecraftRegistry getRegistry() {
        return registry;
    }

}
