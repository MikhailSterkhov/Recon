package org.itzstonlex.recon.minecraft.packet;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;

import java.lang.reflect.Method;

public abstract class MinecraftPacketHandler {

    public final void handle(MinecraftPacket packet)
    throws Exception {

        for (Method method : getClass().getMethods()) {
            if (!method.isAnnotationPresent(PacketHandler.class)) {
                continue;
            }

            Class<?>[] params = method.getParameterTypes();

            if (params.length != 1) {
                continue;
            }

            if (params[0].isAssignableFrom(MinecraftPacket.class)) {
                method.invoke(this, packet);
            }
        }
    }

    public void onExceptionCaught(RemoteChannel channel, Throwable throwable) {
        // override me.
    }

}
