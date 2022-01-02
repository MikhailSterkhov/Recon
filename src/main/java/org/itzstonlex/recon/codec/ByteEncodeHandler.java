package org.itzstonlex.recon.codec;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;

public interface ByteEncodeHandler {

    void handle(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Output buffer);
}
