package org.itzstonlex.recon.side.client;

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

public class Client implements RemoteConnection, RemoteConnection.Connector {

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
    public RemoteChannel connectLocal(int port) {
        return connect("localhost", port);
    }

    @Override
    public RemoteChannel connect(String address, int port) {
        return connect(InetSocketAddress.createUnresolved(address, port));
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address) {
        return connect(address, 5000);
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address, int timeout) {
        this.channel = ChannelFactory.createClientChannel(address, this);
        ConnectionFactory.createClientThread(channel, listenerSet.toArray(new ChannelListener[0]), optionSet.toArray(new ChannelOption[0]), timeout);

        return channel;
    }

}
