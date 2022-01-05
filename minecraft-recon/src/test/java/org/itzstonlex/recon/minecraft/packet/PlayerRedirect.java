package org.itzstonlex.recon.minecraft.packet;

import org.itzstonlex.recon.ByteStream;

public final class PlayerRedirect extends MinecraftPacket {

    private String playerName;
    private String server;

    public PlayerRedirect() {
    }

    public PlayerRedirect(String playerName, String server) {
        this.playerName = playerName;
        this.server = server;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        buffer.writeString(playerName);
        buffer.writeString(server);
    }

    @Override
    public void read(ByteStream.Input buffer) {
        this.playerName = buffer.readString();
        this.server = buffer.readString();
    }


    public String getPlayerName() {
        return playerName;
    }

    public String getServer() {
        return server;
    }

}
