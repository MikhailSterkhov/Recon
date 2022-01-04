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
    public void onThreadActive(ContextHandler contextHandler) {
    }

    @Override
    public void onConnected(ContextHandler contextHandler) {
    }

    @Override
    public void onClosed(ContextHandler contextHandler) {
    }

    @Override
    public void onTimedOut(RemoteChannel channel, ContextHandler contextHandler) {
    }

    @Override
    public void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler) {
    }

    @Override
    public void onClientClosed(RemoteChannel remoteChannel, ContextHandler contextHandler) {
    }

    @Override
    public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer) {
    }

    @Override
    public void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Output buffer) {
    }

    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
        throwable.printStackTrace();
    }

}
