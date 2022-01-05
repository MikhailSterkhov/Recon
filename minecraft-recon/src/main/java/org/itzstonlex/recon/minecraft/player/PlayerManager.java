package org.itzstonlex.recon.minecraft.player;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.packet.handshake.HandshakeInitializer;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.PlayerHandshake;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerManager<Player extends MinecraftPlayer> {

    public static <Player extends MinecraftPlayer>
    PlayerManager<Player> newManager(Class<Player> playerClass, HandshakeInitializer<PlayerHandshake, Player> playerInit) {

        return new PlayerManager<>(playerInit);
    }

    private final Map<UUID, Player> playersUuidsMap;
    private final Map<String, Player> playersNamesMap;

    private final HandshakeInitializer<PlayerHandshake, Player> playerInit;

    private PlayerManager(HandshakeInitializer<PlayerHandshake, Player> playerInit) {
        this.playersUuidsMap = new ConcurrentHashMap<>();
        this.playersNamesMap = new ConcurrentHashMap<>();

        this.playerInit = playerInit;
    }

    public Player newPlayerInstance(RemoteChannel channel, PlayerHandshake handshake) {
        return playerInit.init(channel, handshake);
    }

    public void connectPlayer(RemoteChannel channel, Player player) {
        synchronized (playersUuidsMap) {

            playersUuidsMap.put(player.getUniqueId(), player);
            playersNamesMap.put(player.getName().toLowerCase(), player);

            player.onConnected(channel);
        }
    }

    public void disconnectPlayer(RemoteChannel channel, Player player) {
        synchronized (playersUuidsMap) {

            playersUuidsMap.remove(player.getUniqueId());
            playersNamesMap.remove(player.getName().toLowerCase());

            player.onDisconnected(channel);
        }
    }

    public synchronized Player getPlayer(UUID uniqueId) {
        return playersUuidsMap.get(uniqueId);
    }

    public synchronized Player getPlayer(String name) {
        return playersNamesMap.get(name.toLowerCase());
    }

    public synchronized Collection<Player> getPlayers() {
        return playersUuidsMap.values();
    }

}
