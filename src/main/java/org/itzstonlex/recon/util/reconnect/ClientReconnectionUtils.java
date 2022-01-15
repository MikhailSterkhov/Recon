package org.itzstonlex.recon.util.reconnect;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.side.Client;

import java.util.concurrent.TimeUnit;

public final class ClientReconnectionUtils {

    private static boolean hasDebug = false;

    public static void setDebug(boolean hasDebug) {
        ClientReconnectionUtils.hasDebug = hasDebug;
    }

    public static boolean canDebug() {
        return hasDebug;
    }

    public static void addReconnector(Client client, long reconnectDelay, TimeUnit reconnectUnit) {
        addReconnector(client.channel(), reconnectDelay, reconnectUnit);
    }

    public static void addReconnector(RemoteChannel channel, long reconnectDelay, TimeUnit reconnectUnit) {
        addReconnector(channel.pipeline(), reconnectDelay, reconnectUnit);
    }

    public static void addReconnector(ChannelPipeline pipeline, long reconnectDelay, TimeUnit reconnectUnit) {
        ChannelListener reconnectListener = new ChannelReconnectListener(

                pipeline.channel().connection(),
                reconnectDelay, reconnectUnit, canDebug()
        );

        pipeline.putLast(ChannelReconnectListener.PIPELINE_ID, reconnectListener);
    }

}
