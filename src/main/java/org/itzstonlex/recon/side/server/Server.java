package org.itzstonlex.recon.side.server;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.factory.ChannelFactory;
import org.itzstonlex.recon.factory.ConnectionFactory;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Server implements RemoteConnection, RemoteConnection.Binder {

    private RemoteChannel channel;

    private final Set<ChannelListener> listenerSet = new HashSet<>();
    private final Set<ChannelOption> optionSet = new HashSet<>();

    @Override
    public RemoteChannel channel() {
        return channel;
    }

    @Override
    public void addListener(ChannelListener channelListener) {
        listenerSet.add(channelListener);
    }

    @Override
    public void setOption(ChannelOption channelOption) {
        optionSet.add(channelOption);
    }

    @Override
    public void shutdown() throws IOException {

        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public RemoteChannel bindLocal(int port) {
        return bind("localhost", port);
    }

    @Override
    public RemoteChannel bind(String address, int port) {
        return bind(InetSocketAddress.createUnresolved(address, port));
    }

    @Override
    public RemoteChannel bind(InetSocketAddress address) {
        channel = ChannelFactory.createServerChannel(address, this);
        ConnectionFactory.createServerThread(channel, listenerSet.toArray(new ChannelListener[0]), optionSet.toArray(new ChannelOption[0]));

        return channel;
    }

}
