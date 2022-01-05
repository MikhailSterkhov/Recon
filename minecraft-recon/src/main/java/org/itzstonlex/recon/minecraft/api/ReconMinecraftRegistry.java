package org.itzstonlex.recon.minecraft.api;

import org.itzstonlex.recon.minecraft.exception.PacketException;
import org.itzstonlex.recon.minecraft.packet.MinecraftPacket;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;
import org.itzstonlex.recon.minecraft.server.MinecraftServersGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ReconMinecraftRegistry {

// ===================================================================================================== //

    private final Map<Integer, Supplier<MinecraftPacket>> packetsByIdsMap = new HashMap<>();
    private final Map<Class<? extends MinecraftPacket>, Integer> IdsByPacketsMap = new HashMap<>();

    public void registerPacket(int packetID, Supplier<MinecraftPacket> packet) {
        if (packetID < 0) {
            throw new PacketException("Packet ID (%d) cannot be < 0", packetID);
        }

        if (packetsByIdsMap.containsKey(packetID)) {
            throw new PacketException("Packet %d already registered", packetID);
        }

        packetsByIdsMap.put(packetID, packet);
        IdsByPacketsMap.put(packet.get().getClass(), packetID);
    }

    public void registerPacket(Supplier<MinecraftPacket> packet) {
        registerPacket(packetsByIdsMap.size(), packet);
    }

    public final MinecraftPacket getRegisteredPacket(int packetID) {
        Supplier<MinecraftPacket> packetGetter = packetsByIdsMap.get(packetID);

        if (packetGetter == null) {
            throw new PacketException("Packet %d is`nt exists", packetID);
        }

        return packetGetter.get();
    }

    public final int getRegisteredPacketId(Class<? extends MinecraftPacket> packetClass) {
        return IdsByPacketsMap.get(packetClass);
    }


// ===================================================================================================== //

    private final List<MinecraftServersGroup> serversGroupList = new ArrayList<>();

    public List<MinecraftServersGroup> getServersGroups() {
        return serversGroupList;
    }

    public void registerServersGroup(MinecraftServersGroup minecraftServersGroup) {
        serversGroupList.add(minecraftServersGroup);
    }

    public MinecraftServersGroup getRegisteredServersGroup(int id) {
        for (MinecraftServersGroup serversGroup : serversGroupList) {

            if (serversGroup.getId() == id) {
                return serversGroup;
            }
        }

        return null;
    }

    public MinecraftServersGroup getRegisteredServersGroup(String prefix) {
        MinecraftServersGroup nullablePrefix = null;

        for (MinecraftServersGroup serversGroup : serversGroupList) {
            if (serversGroup.getPrefix() == null) {
                nullablePrefix = serversGroup;

                continue;
            }

            if (serversGroup.getPrefix().equalsIgnoreCase(prefix)) {
                return serversGroup;
            }
        }

        return nullablePrefix;
    }

    public MinecraftServersGroup getServerGroup(String serverName) {
        for (MinecraftServersGroup serversGroup : serversGroupList) {

            if (serversGroup.getPrefix() == null) {
                return serversGroup;
            }

            if (serverName.toLowerCase().startsWith(serversGroup.getPrefix().toLowerCase())) {
                return serversGroup;
            }
        }

        return null;
    }

    public MinecraftServersGroup getServerGroup(MinecraftServer minecraftServer) {
        return getServerGroup(minecraftServer.getName());
    }

}
