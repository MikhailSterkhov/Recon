package org.itzstonlex.recon.minecraft.util;

import org.itzstonlex.recon.ByteStream;

import java.net.InetSocketAddress;
import java.util.UUID;

public final class BufferUtils {

    public static UUID readUUID(ByteStream.Input buffer) {
        return new UUID(buffer.readLong(), buffer.readLong());
    }

    public static void writeUUID(ByteStream.Output buffer, UUID uuid) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
    }

    public static InetSocketAddress readAddress(ByteStream.Input buffer) {
        return new InetSocketAddress(buffer.readString(), buffer.readVarInt());
    }

    public static void writeAddress(ByteStream.Output buffer, InetSocketAddress address) {
        buffer.writeString(address.getHostString());
        buffer.writeVarInt(address.getPort());
    }

    public static <E extends Enum<?>> E readEnum(ByteStream.Input buffer, Class<E> enumClass) {
        int ordinal = buffer.readVarInt();
        return enumClass.getEnumConstants()[ordinal];
    }

    public static void writeEnum(ByteStream.Output buffer, Enum<?> value) {
        buffer.writeVarInt(value.ordinal());
    }

}
