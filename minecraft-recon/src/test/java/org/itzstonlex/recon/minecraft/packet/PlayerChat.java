package org.itzstonlex.recon.minecraft.packet;

import org.itzstonlex.recon.ByteStream;

public final class PlayerChat extends MinecraftPacket {

    private String playerName;
    private String message;

    public PlayerChat() {
    }

    public PlayerChat(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        buffer.writeString(playerName);
        buffer.writeString(message);
    }

    @Override
    public void read(ByteStream.Input buffer) {
        this.playerName = buffer.readString();
        this.message = buffer.readString();
    }


    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

}
