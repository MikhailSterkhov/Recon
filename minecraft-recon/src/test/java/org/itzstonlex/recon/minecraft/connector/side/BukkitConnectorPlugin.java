package org.itzstonlex.recon.minecraft.connector.side;

import org.itzstonlex.recon.minecraft.connector.MinecraftConnector;
import org.itzstonlex.recon.minecraft.packet.handshake.Handshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.util.MinecraftVersion;

import java.net.InetSocketAddress;

public final class BukkitConnectorPlugin { // extends JavaPlugin

// -------------------------------- INITIALIZE CONNECTION ---------------------------------------------------------------------------------------------------------------

    private int version;

    private String name;
    private InetSocketAddress address;

    private boolean isProxy;

    private void initialize() {
        this.version = MinecraftVersion.V_1_16_5.getVersionId();
        this.name = "Lobby-1";

        this.address = new InetSocketAddress("127.0.0.1", 25567);

        this.isProxy = false;
    }

// -------------------------------- MINECRAFT CONNECTION ---------------------------------------------------------------------------------------------------------------

    private MinecraftConnector minecraftConnector;

    public void onEnable() {
        initialize();

        minecraftConnector = new MinecraftConnector(name, address,
                onConnected -> onConnected.sendPacket(new ServerHandshake(version, name, address, Handshake.Status.CONNECT, isProxy)));

        minecraftConnector.launchConnector();
    }

    public void onDisable() {

        if (!minecraftConnector.getChannel().isClosed()) {
            minecraftConnector.sendPacket(new ServerHandshake(version, name, address, Handshake.Status.DISCONNECT, isProxy));
        }
    }

    public MinecraftConnector getConnector() {
        return minecraftConnector;
    }

}
