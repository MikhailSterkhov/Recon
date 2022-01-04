package org.itzstonlex.recon.handler;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;

public class PacketHandler extends IncomingByteHandler {

    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
    }

    @Override
    public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler,
                       ByteStream.Input buffer) {

        remoteChannel.logger().info("PacketHandler#onRead()");
    }

}
