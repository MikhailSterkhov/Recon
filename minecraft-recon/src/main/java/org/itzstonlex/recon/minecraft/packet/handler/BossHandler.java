package org.itzstonlex.recon.minecraft.packet.handler;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.minecraft.api.ReconMinecraftRegistry;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacketHandler;

import java.util.ArrayList;
import java.util.List;

public final class BossHandler extends ChannelListenerAdapter {

    private final ReconMinecraftRegistry registry;
    private final List<MinecraftPacketHandler> packetHandlers = new ArrayList<>();

    public BossHandler(ReconMinecraftRegistry registry) {
        this.registry = registry;
    }

    public void addPacketHandler(MinecraftPacketHandler handler) {
        packetHandlers.add(handler);
    }

    public List<MinecraftPacketHandler> getPacketHandlers() {
        return packetHandlers;
    }

    @Override
    public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler,
                       ByteStream.Input buffer) {

        if (buffer.size() == 0) {
            return;
        }

        int packetID = buffer.readVarInt();
        MinecraftPacket minecraftPacket = registry.getRegisteredPacket(packetID);

        if (!minecraftPacket.isReadable()) {
            return;
        }

        minecraftPacket.read(buffer);              // fix ConcurrentModificationException
        for (MinecraftPacketHandler packetHandler : new ArrayList<>(getPacketHandlers())) {

            try {
                packetHandler.handle(minecraftPacket);
            }
            catch (Exception exception) {
                onExceptionCaught(remoteChannel, exception);
            }
        }
    }

    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {

        for (MinecraftPacketHandler packetHandler : getPacketHandlers()) {
            packetHandler.onExceptionCaught(remoteChannel, throwable);
        }
    }

}
