package org.itzstonlex.recon;

import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface RemoteConnection {

    ExecutorService getThread();

    ReconLog logger();

    RemoteChannel channel();

    ChannelOption[] options();

    void setOption(ChannelOption channelOption);

    void shutdown();

    interface Binder {

        RemoteChannel bindLocal(int port);

        RemoteChannel bindLocal(int port, Consumer<ChannelConfig> config);

        RemoteChannel bind(String address, int port);

        RemoteChannel bind(String address, int port, Consumer<ChannelConfig> config);

        RemoteChannel bind(InetSocketAddress address);

        RemoteChannel bind(InetSocketAddress address, Consumer<ChannelConfig> config);
    }

    interface Connector {

        int timeout();

        RemoteChannel connectLocal(int port);

        RemoteChannel connectLocal(int port, Consumer<ChannelConfig> config);

        RemoteChannel connect(String address, int port);

        RemoteChannel connect(String address, int port, Consumer<ChannelConfig> config);

        RemoteChannel connect(InetSocketAddress address);

        RemoteChannel connect(InetSocketAddress address, Consumer<ChannelConfig> config);

        RemoteChannel connect(InetSocketAddress address, int timeout);

        RemoteChannel connect(InetSocketAddress address, int timeout, Consumer<ChannelConfig> config);
    }

}
