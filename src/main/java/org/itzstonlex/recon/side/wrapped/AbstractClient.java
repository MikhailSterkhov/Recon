package org.itzstonlex.recon.side.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.side.Client;

import java.net.InetSocketAddress;

public abstract class AbstractClient {

    private final InetSocketAddress address;

    public AbstractClient(InetSocketAddress address) {
        // unresolved addresses fix.
        this.address = new InetSocketAddress(address.getHostString(), address.getPort());
    }

    public AbstractClient(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public AbstractClient(int port) {
        this("127.0.0.1", port);
    }


    public abstract void initChannel (
            ConnectionLogger logger,
            ChannelConfig channelConfig
    );

    public RemoteChannel connect() {
        return connect(5000);
    }

    public RemoteChannel connect(int timeout) {

        Client client = new Client();
        return client.connect(address, timeout, config -> initChannel(client.logger(), config));
    }

}
