package org.itzstonlex.recon.side;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.factory.ChannelFactory;
import org.itzstonlex.recon.init.ChannelInitializer;
import org.itzstonlex.recon.init.ClientThreadInitializer;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Client implements RemoteConnection, RemoteConnection.Connector {

    private RemoteChannel channel;
    private int timeout = -1;

    private final ExecutorService thread
            = Executors.newSingleThreadExecutor();

    private final ConnectionLogger logger = new ConnectionLogger("Client");
    private final Set<ChannelOption> optionSet = new HashSet<>();

    @Override
    public ExecutorService getThread() {
        return thread;
    }

    @Override
    public ConnectionLogger logger() {
        return logger;
    }

    @Override
    public RemoteChannel channel() {
        return channel;
    }

    @Override
    public ChannelOption[] options() {
        return optionSet.toArray(new ChannelOption[0]);
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
    public int timeout() {
        return timeout;
    }

    @Override
    public RemoteChannel connectLocal(int port) {
        return connect("127.0.0.1", port, null);
    }

    @Override
    public RemoteChannel connectLocal(int port, Consumer<ChannelConfig> config) {
        return connect("127.0.0.1", port, config);
    }

    @Override
    public RemoteChannel connect(String address, int port) {
        return connect(address, port, null);
    }

    @Override
    public RemoteChannel connect(String address, int port, Consumer<ChannelConfig> config) {
        return connect(new InetSocketAddress(address, port), config);
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address) {
        return connect(address, null);
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address, Consumer<ChannelConfig> config) {
        return connect(address, 5000, config);
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address, int timeout) {
        return connect(address, timeout, null);
    }

    @Override
    public RemoteChannel connect(InetSocketAddress address, int timeout, Consumer<ChannelConfig> config) {
        initChannel(address);
        ChannelInitializer.applyConfigValues(this, channel, config);

        ClientThreadInitializer.Data clientData = new ClientThreadInitializer.Data (
                channel,
                optionSet.toArray(new ChannelOption[0]),
                this.timeout = timeout
        );

        new ClientThreadInitializer(clientData).start();
        return channel;
    }

    public void initChannel(InetSocketAddress address) {
        this.channel = ChannelFactory.createChannel(address, this);
    }

}
