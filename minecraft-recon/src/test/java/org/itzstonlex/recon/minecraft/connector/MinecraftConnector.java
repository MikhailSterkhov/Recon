package org.itzstonlex.recon.minecraft.connector;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.PendingChannelHandler;
import org.itzstonlex.recon.minecraft.PendingConnection;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftApi;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.packet.PlayerChat;
import org.itzstonlex.recon.minecraft.packet.PlayerRedirect;
import org.itzstonlex.recon.minecraft.packet.handshake.Handshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.server.MinecraftServersGroup;
import org.itzstonlex.recon.minecraft.service.DefaultMinecraftManagementService;
import org.itzstonlex.recon.util.reconnect.ClientReconnectionUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MinecraftConnector implements PendingConnection {

// ====================================================================================================================================== //

    public static final int CONNECT_PORT = 1000;

    public static final ReconMinecraftApi MINECRAFT_API = new ReconMinecraftApi();

// ====================================================================================================================================== //


    private final String name;
    private final InetSocketAddress address;

    private final Consumer<MinecraftConnector> onConnectedHandler;

    private RemoteChannel channel;

    public MinecraftConnector(String name, InetSocketAddress address, Consumer<MinecraftConnector> onConnectedHandler) {
        this.name = name;
        this.address = address;
        this.onConnectedHandler = onConnectedHandler;
    }

    /**
     * Process of the application launch.
     */
    public void launchConnector() {
        // ...your launch code

        connectLocal();
    }

    /**
     * Bind a Local Server on
     * the port of 1000.
     */
    private void connectLocal() {
        ReconMinecraftApi.MinecraftApplication application = MINECRAFT_API.newMinecraftApplication();

        // Create a service-factory.
        ReconMinecraftApi.ServiceFactory serviceFactory = ReconMinecraftApi.ServiceFactory.create(
                DefaultMinecraftManagementService.create(MINECRAFT_API),

                (bossHandler, channelConfig) -> {

                    // Registry managements.
                    ReconMinecraftRegistry registry = MINECRAFT_API.getRegistry();

                    registerServersGroups( registry );
                    registerPackets( registry );

                    // Pipeline managements.
                    channelConfig.pipeline().addLast("channel-handler", new PendingChannelHandler(MinecraftConnector.this));

                    ClientReconnectionUtils.setDebug(true);
                    ClientReconnectionUtils.addReconnector(channelConfig.pipeline(), 5, TimeUnit.SECONDS);
                });

        // Connect to local server on CONNECT_PORT (1000)
        this.channel = application.connectLocal(CONNECT_PORT, serviceFactory);
    }

    /**
     * Registering all packets.
     *
     * ATTENTION! The system has already pre-occupied
     * identifiers 0 and 1 handshake packages,
     * so if you want to specify identifiers
     * manually, pay attention to this information
     *
     * @param registry - Recon Minecraft registry-service.
     */
    private void registerPackets(ReconMinecraftRegistry registry) {
        registry.registerPacket(PlayerChat::new);
        registry.registerPacket(PlayerRedirect::new);
    }

    /**
     * Registering all packets.
     *
     * This is where the basic server
     * groups are registered.
     *
     * Thanks to them, it will be possible
     * to find out which department
     * a certain server belongs to.
     *
     * It is important to know that it
     * works exclusively by prefixes from
     * the names of the connected servers.
     *
     * @param registry - Recon Minecraft registry-service.
     */
    private void registerServersGroups(ReconMinecraftRegistry registry) {
        registry.registerServersGroup(MinecraftServersGroup.create(1, "Proxy", "bungee"));
        registry.registerServersGroup(MinecraftServersGroup.create(2, "Bukkit", null));
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
        return channel;
    }

    @Override
    public void onConnected(RemoteChannel channel) {
        channel.logger().info("[MinecraftConnector] Channel was success listening on " + channel.address());

        if (onConnectedHandler != null) {
            onConnectedHandler.accept(this);
        }
    }

    @Override
    public void onDisconnected(RemoteChannel channel) {
        channel.logger().info("[MinecraftConnector] Channel was closed!");
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        MINECRAFT_API.sendPacket(channel, packet);
    }

}
