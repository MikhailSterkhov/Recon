package org.itzstonlex.recon.side.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.side.Server;

import java.net.InetSocketAddress;

public abstract class AbstractServer {

    protected final InetSocketAddress address;
    protected final Server server = new Server();

    public AbstractServer(InetSocketAddress address) {
        if (address.isUnresolved()) {
            this.address = new InetSocketAddress(address.getHostString(), address.getPort());

        } else {

            this.address = address;
        }
    }

    public AbstractServer(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public AbstractServer(int port) {
        this("127.0.0.1", port);
    }

    public abstract void initChannel(
            ReconLog logger, ChannelConfig channelConfig
    );

    public RemoteChannel bind() {
        return server.bind(address, config -> initChannel(server.logger(), config));
    }

}
