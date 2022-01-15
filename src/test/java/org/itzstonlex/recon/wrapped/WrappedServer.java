package org.itzstonlex.recon.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.handler.PacketHandler;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.side.wrapped.AbstractServer;

public class WrappedServer extends AbstractServer {

    public WrappedServer() {
        super(5_000);
    }

    @Override
    public void initChannel(ReconLog logger, ChannelConfig channelConfig) {
        channelConfig.pipeline().addLast("packet-handler", new PacketHandler());
    }

}
