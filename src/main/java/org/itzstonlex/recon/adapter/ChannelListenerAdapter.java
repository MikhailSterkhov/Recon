package org.itzstonlex.recon.adapter;

import org.itzstonlex.recon.*;

public abstract class ChannelListenerAdapter
        implements ChannelListener {

    protected final RemoteConnection connection;

    public ChannelListenerAdapter(RemoteConnection connection) {
        this.connection = connection;
    }

    public ChannelListenerAdapter() {
        this(null);
    }


    public RemoteConnection getConnection() {
        return connection;
    }

    @Override
    public void onThreadActive(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onConnected(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onClosed(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onConnectTimeout(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onBind(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onClientConnected(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onClientClosed(RemoteChannel channel, ContextHandler contextHandler) {
        // override me.
    }

    @Override
    public void onRead(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Input buffer) {
        // override me.
    }

    @Override
    public void onWrite(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Output buffer) {
        // override me.
    }

    @Override
    public void onExceptionCaught(RemoteChannel channel, Throwable throwable) {
        // override me.

        throwable.printStackTrace();
    }

}
