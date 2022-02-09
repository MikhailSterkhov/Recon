package org.itzstonlex.recon.minecraft.connector.side;

import org.itzstonlex.recon.minecraft.connector.MinecraftConnector;
import org.itzstonlex.recon.minecraft.packet.handshake.Handshake;
import org.itzstonlex.recon.minecraft.packet.handshake.impl.ServerHandshake;
import org.itzstonlex.recon.minecraft.util.MinecraftVersion;

import java.net.InetSocketAddress;

public final class ProxyConnectorPlugin { // extends Plugin

// -------------------------------- INITIALIZE CONNECTION ---------------------------------------------------------------------------------------------------------------

    private int version;

    private String name;
    private InetSocketAddress address;

    private boolean isProxy;

    private void initialize() {
        this.version = MinecraftVersion.V_1_12_2.getVersionId();
        this.name = "Proxy-1";

        // Getting from Proxy server data.
        this.address = new InetSocketAddress("127.0.0.1", 25565);

        this.isProxy = true;
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
