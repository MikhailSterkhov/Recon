package org.itzstonlex.recon.minecraft.packet.handshake.impl;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.minecraft.util.BufferUtils;
import org.itzstonlex.recon.minecraft.packet.handshake.Handshake;

import java.net.InetSocketAddress;
import java.util.UUID;

public class PlayerHandshake extends Handshake {

    private UUID uniqueId;

    private String displayName;

    private String proxyServer;
    private String currentServer;

    public PlayerHandshake() {
        super();
    }

    public PlayerHandshake(int version, String name, InetSocketAddress address,

                           // Player variables.
                           UUID uniqueId, String displayName, String proxyServer, String currentServer) {

        super(version, name, address);

        this.uniqueId = uniqueId;

        this.displayName = displayName;

        this.proxyServer = proxyServer;
        this.currentServer = currentServer;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        super.write(buffer);

        BufferUtils.writeUUID(buffer, uniqueId);

        buffer.writeString(displayName);

        buffer.writeString(currentServer);
        buffer.writeString(proxyServer);
    }

    @Override
    public void read(ByteStream.Input buffer) {
        super.read(buffer);

        this.uniqueId = BufferUtils.readUUID(buffer);

        this.displayName = buffer.readString();

        this.proxyServer = buffer.readString();
        this.currentServer = buffer.readString();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public String getCurrentServer() {
        return currentServer;
    }

}
