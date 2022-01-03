package org.itzstonlex.recon.handler;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public abstract class IncomingByteHandler extends ChannelListenerAdapter {

    public IncomingByteHandler(RemoteConnection connection) {
        super(connection);
    }

    public IncomingByteHandler() {
        super();
    }

    @Override
    public abstract void onRead(
            RemoteChannel remoteChannel, ContextHandler contextHandler,
            ByteStream.Input buffer
    );

}
