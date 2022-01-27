package org.itzstonlex.recon;

public interface ChannelListener {

    void onThreadActive(RemoteChannel channel, ContextHandler contextHandler);

    void onClosed(RemoteChannel channel, ContextHandler contextHandler);

    void onRead(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Input buffer);

    void onWrite(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Output buffer);

    void onExceptionCaught(RemoteChannel channel, Throwable throwable);

    // Client events.
    void onConnected(RemoteChannel channel, ContextHandler contextHandler);

    void onConnectTimeout(RemoteChannel channel, ContextHandler contextHandler);

    // Server Events.
    void onBind(RemoteChannel channel, ContextHandler contextHandler);

    void onClientConnected(RemoteChannel channel, ContextHandler contextHandler);

    void onClientClosed(RemoteChannel channel, ContextHandler contextHandler);

}
