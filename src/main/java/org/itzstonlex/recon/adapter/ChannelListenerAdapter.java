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
    public void onActive(ContextHandler contextHandler) {
    }

    @Override
    public void onInactive(ContextHandler contextHandler) {
    }

    @Override
    public void onNewClientActive(RemoteChannel remoteChannel, ContextHandler contextHandler) {
    }

    @Override
    public void onClientInactive(RemoteChannel remoteChannel, ContextHandler contextHandler) {
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
