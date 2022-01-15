package org.itzstonlex.recon;

public interface ChannelListener {

    void onThreadActive(RemoteChannel channel, ContextHandler contextHandler);

    void onClosed(RemoteChannel channel, ContextHandler contextHandler);

    void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer);

    void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Output buffer);

    void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable);

    // Client events.
    void onConnected(RemoteChannel channel, ContextHandler contextHandler);

    void onTimedOut(RemoteChannel channel, ContextHandler contextHandler);

    // Server Events.
    void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler);

    void onClientClosed(RemoteChannel remoteChannel, ContextHandler contextHandler);

}
