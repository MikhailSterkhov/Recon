package org.itzstonlex.recon.minecraft;

import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public final class PendingChannelHandler extends ChannelListenerAdapter {

    private final PendingConnection connection;

    public PendingChannelHandler(PendingConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler) {
        remoteChannel.logger().warning("Client connection connected: " + remoteChannel.address());
    }

    @Override
    public void onClientClosed(RemoteChannel remoteChannel, ContextHandler contextHandler) {
        remoteChannel.logger().warning("Client connection closed: " + remoteChannel.address());
    }


    @Override
    public void onConnected(RemoteChannel channel, ContextHandler contextHandler) {
        connection.onConnected(channel);
    }

    @Override
    public void onBind(RemoteChannel channel, ContextHandler contextHandler) {
        connection.onConnected(channel);
    }

    @Override
    public void onClosed(RemoteChannel channel, ContextHandler contextHandler) {
        connection.onDisconnected(channel);
    }


    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
        // nothing.
    }

}
