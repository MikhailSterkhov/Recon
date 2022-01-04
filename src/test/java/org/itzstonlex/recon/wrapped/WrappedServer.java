package org.itzstonlex.recon.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.handler.PacketHandler;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.side.wrapped.AbstractServer;

public class WrappedServer extends AbstractServer {

    public WrappedServer() {
        super(5_000);
    }

    @Override
    public void initChannel(ConnectionLogger logger, ChannelConfig channelConfig) {
        channelConfig.pipeline().putLast("packet-handler", new PacketHandler());
    }

}
