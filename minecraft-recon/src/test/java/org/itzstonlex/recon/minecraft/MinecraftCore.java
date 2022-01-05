package org.itzstonlex.recon.minecraft;

import org.itzstonlex.recon.minecraft.api.ReconMinecraftApi;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.packet.PlayerChat;
import org.itzstonlex.recon.minecraft.packet.PlayerRedirect;
import org.itzstonlex.recon.minecraft.player.CorePlayer;
import org.itzstonlex.recon.minecraft.server.CoreServer;
import org.itzstonlex.recon.minecraft.server.MinecraftServersGroup;
import org.itzstonlex.recon.minecraft.server.impl.Bukkit;
import org.itzstonlex.recon.minecraft.server.impl.Proxy;

import java.util.UUID;

public class MinecraftCore {

    public static final int BIND_PORT = 1000;

    public static final ReconMinecraftApi MINECRAFT_API = new ReconMinecraftApi();

    public static void main(String[] args) {
        MinecraftCore minecraftCore = new MinecraftCore();
        minecraftCore.launchCore();
    }


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
        ReconMinecraftApi.MinecraftApplication server = MINECRAFT_API.newMinecraftApplication();

        // Create a service-factory.
        ReconMinecraftApi.ServiceFactory serviceFactory = ReconMinecraftApi.ServiceFactory.create(coreService,
                (bossHandler, channelConfig) -> {

                    registerServersGroups(MINECRAFT_API.getRegistry());

                    registerPackets(MINECRAFT_API.getRegistry());
                });

        // Bind a local server on BIND_PORT (1000)
        server.bindLocal(BIND_PORT, serviceFactory);
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

}
