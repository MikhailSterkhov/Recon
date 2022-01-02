package org.itzstonlex.recon;

import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface RemoteConnection {

    RemoteChannel channel();

    void addListener(ChannelListener channelListener);

    void setOption(ChannelOption channelOption);

    void shutdown() throws IOException;


    interface Binder {

        RemoteChannel bindLocal(int port);

        RemoteChannel bind(String address, int port);

        RemoteChannel bind(InetSocketAddress address);
    }

    interface Connector {

        RemoteChannel connectLocal(int port);

        RemoteChannel connect(String address, int port);

        RemoteChannel connect(InetSocketAddress address);

        RemoteChannel connect(InetSocketAddress address, int timeout);
    }

}
