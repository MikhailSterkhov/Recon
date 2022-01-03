package org.itzstonlex.recon.handler;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public abstract class OutgoingByteHandler extends ChannelListenerAdapter {

    public OutgoingByteHandler(RemoteConnection connection) {
        super(connection);
    }

    public OutgoingByteHandler() {
        super();
    }

    @Override
    public abstract void onWrite(
            RemoteChannel remoteChannel, ContextHandler contextHandler,
            ByteStream.Output buffer
    );

}
