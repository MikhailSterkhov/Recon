package org.itzstonlex.recon.minecraft.packet.handshake;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.util.BufferUtils;
import org.itzstonlex.recon.minecraft.util.MinecraftVersion;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public abstract class Handshake extends MinecraftPacket {

    public enum Status {

        CONNECT,
        DISCONNECT,
        ;
    }

    private int version;

    private String name;
    private InetSocketAddress address;

    private Status status;


    public Handshake() {
    }

    public Handshake(int version, String name, InetSocketAddress address, Status status) {
        this.version = version;

        this.name = name;
        this.address = address;

        this.status = status;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        buffer.writeVarInt(version);
        buffer.writeString(name);

        BufferUtils.writeAddress(buffer, address);

        BufferUtils.writeEnum(buffer, status);
    }

    @Override
    public void read(ByteStream.Input buffer) {
        this.version = buffer.readVarInt();
        this.name = buffer.readString();

        this.address = BufferUtils.readAddress(buffer);

        this.status = BufferUtils.readEnum(buffer, Status.class);
    }

    public MinecraftVersion getVersion() {
        return MinecraftVersion.getByVersionId(version);
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Status getStatus() {
        return status;
    }

}
