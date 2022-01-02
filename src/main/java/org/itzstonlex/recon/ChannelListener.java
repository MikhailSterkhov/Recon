package org.itzstonlex.recon;

public interface ChannelListener {

    void onActive(ContextHandler contextHandler);

    void onInactive(ContextHandler contextHandler);

    void onServerBind(RemoteChannel remoteChannel);

    void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler);

    void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer);

    void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Output buffer);

    void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable);
}
