package org.itzstonlex.recon.adapter;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;

public abstract class ChannelListenerAdapter
        implements ChannelListener {

    @Override
    public void onActive(ContextHandler contextHandler) {
    }

    @Override
    public void onInactive(ContextHandler contextHandler) {
    }

    @Override
    public void onServerBind(RemoteChannel remoteChannel) {
    }

    @Override
    public void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler) {
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
