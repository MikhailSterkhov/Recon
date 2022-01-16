package org.itzstonlex.recon.minecraft.core.server;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.core.CoreService;
import org.itzstonlex.recon.minecraft.core.MinecraftCore;
import org.itzstonlex.recon.minecraft.player.MinecraftPlayer;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CoreServer extends MinecraftServer {

    protected final MinecraftCore minecraftCore;

    public CoreServer(MinecraftCore minecraftCore, RemoteChannel channel,
                      boolean isProxy, String name, InetSocketAddress address) {

        super(MinecraftCore.MINECRAFT_API, channel, isProxy, name, address);
        this.minecraftCore = minecraftCore;
    }

    public CoreService getService() {
        return minecraftCore.getService();
    }

    public List<MinecraftPlayer> getOnlinePlayers() {
        return minecraftCore.getService().getPlayerManager().getPlayers().stream()
                .filter(minecraftPlayer -> minecraftPlayer.getServer() != null && minecraftPlayer.getServer().getName().equalsIgnoreCase(name))

                .collect(Collectors.toList());
    }

    public int getOnlineCount() {
        return getOnlinePlayers().size();
    }
}
