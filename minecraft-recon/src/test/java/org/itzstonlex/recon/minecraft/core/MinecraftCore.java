package org.itzstonlex.recon.minecraft.core;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.PendingChannelHandler;
import org.itzstonlex.recon.minecraft.PendingConnection;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftApi;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.core.player.CorePlayer;
import org.itzstonlex.recon.minecraft.core.server.CoreServer;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.packet.PlayerChat;
import org.itzstonlex.recon.minecraft.packet.PlayerRedirect;
import org.itzstonlex.recon.minecraft.core.server.impl.Bukkit;
import org.itzstonlex.recon.minecraft.core.server.impl.Proxy;
import org.itzstonlex.recon.minecraft.server.MinecraftServersGroup;

import java.net.InetSocketAddress;
import java.util.UUID;

public class MinecraftCore implements PendingConnection {

// ====================================================================================================================================== //

    public static final int BIND_PORT = 1000;

    public static final ReconMinecraftApi MINECRAFT_API = new ReconMinecraftApi();

    public static void main(String[] args) {
        MinecraftCore minecraftCore = new MinecraftCore();
        minecraftCore.launchCore();
    }

// ====================================================================================================================================== //

    private RemoteChannel channel;
    private final CoreService coreService = new CoreService(this);

    public final CoreService getService() {
        return coreService;
    }

    /**
     * Process of the application launch.
     */
    public void launchCore() {
        // ...your launch code

        bindLocal();
    }

    /**
     * Bind a Local Server on
     * the port of 1000.
     */
    private void bindLocal() {
        ReconMinecraftApi.MinecraftApplication application = MINECRAFT_API.newMinecraftApplication();

        // Create a service-factory.
        ReconMinecraftApi.ServiceFactory serviceFactory = ReconMinecraftApi.ServiceFactory.create(coreService,
                (bossHandler, channelConfig) -> {

                    // Registry managements.
                    ReconMinecraftRegistry registry = MINECRAFT_API.getRegistry();

                    registerServersGroups( registry );
                    registerPackets( registry );

                    // Pipeline managements.
                    channelConfig.pipeline().addLast("channel-handler", new PendingChannelHandler(MinecraftCore.this));
                });

        // Bind a local server on BIND_PORT (1000)
        this.channel = application.bindLocal(BIND_PORT, serviceFactory);
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


    /**
     * Getting a player by UUID
     *
     * @param uuid - Player UUID
     */
    public CorePlayer getPlayer(UUID uuid) {
        return coreService.getPlayerManager().getPlayer(uuid);
    }

    /**
     * Getting a player by name.
     *
     * @param name - Player name.
     */
    public CorePlayer getPlayer(String name) {
        return coreService.getPlayerManager().getPlayer(name);
    }

    /**
     * Getting a proxy server by name.
     *
     * @param name - Proxy server name.
     */
    public Proxy getProxy(String name) {
        CoreServer coreServer = coreService.getServerManager().getServer(name);

        if (coreServer.isProxy()) {
            return (Proxy) coreServer;
        }

        return null;
    }

    /**
     * Getting a bukkit server by name.
     *
     * @param name - Bukkit server name.
     */
    public Bukkit getBukkit(String name) {
        CoreServer coreServer = coreService.getServerManager().getServer(name);

        if (!coreServer.isProxy()) {
            return (Bukkit) coreServer;
        }

        return null;
    }

    @Override
    public String getName() {
        return "MinecraftCore";
    }

    @Override
    public InetSocketAddress getAddress() {
        return channel.address();
    }

    @Override
    public RemoteChannel getChannel() {
        return channel;
    }

    @Override
    public void onConnected(RemoteChannel channel) {
        channel.logger().info("[MinecraftCore] Channel was success listening on " + channel.address());
    }

    @Override
    public void onDisconnected(RemoteChannel channel) {
        channel.logger().info("[MinecraftCore] Channel was closed!");

        System.exit(0);
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        MINECRAFT_API.sendPacket(channel, packet);
    }
}
