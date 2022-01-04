package org.itzstonlex.recon.side.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.side.Server;

import java.net.InetSocketAddress;

public abstract class AbstractServer {

    private final InetSocketAddress address;

    public AbstractServer(InetSocketAddress address) {
        // unresolved addresses fix.
        this.address = new InetSocketAddress(address.getHostString(), address.getPort());
    }

    public AbstractServer(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public AbstractServer(int port) {
        this("127.0.0.1", port);
    }


    public abstract void initChannel (
            ConnectionLogger logger,
            ChannelConfig channelConfig
    );

    public RemoteChannel bind() {

        Server server = new Server();
        return server.bind(address, config -> initChannel(server.logger(), config));
    }

}
