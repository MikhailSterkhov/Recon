package org.itzstonlex.recon.side;

import org.itzstonlex.recon.*;
import org.itzstonlex.recon.factory.ChannelFactory;
import org.itzstonlex.recon.init.ChannelInitializer;
import org.itzstonlex.recon.init.ServerThreadInitializer;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server implements RemoteConnection, RemoteConnection.Binder {

    private RemoteChannel channel;

    private final ExecutorService thread
            = Executors.newSingleThreadExecutor();

    private final ReconLog logger = new ReconLog("Server");
    private final Set<ChannelOption> optionSet = new HashSet<>();

    @Override
    public ExecutorService getThread() {
        return thread;
    }

    @Override
    public ReconLog logger() {
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
    public void shutdown() {
        try {
            if (channel != null && !channel.isClosed()) {
                channel.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public RemoteChannel bindLocal(int port) {
        return bind("127.0.0.1", port, null);
    }

    @Override
    public RemoteChannel bindLocal(int port, Consumer<ChannelConfig> config) {
        return bind("127.0.0.1", port, config);
    }

    @Override
    public RemoteChannel bind(String address, int port) {
        return bind(address, port, null);
    }

    @Override
    public RemoteChannel bind(String address, int port, Consumer<ChannelConfig> config) {
        return bind(new InetSocketAddress(address, port), config);
    }

    @Override
    public RemoteChannel bind(InetSocketAddress address) {
        return bind(address, null);
    }

    @Override
    public RemoteChannel bind(InetSocketAddress address, Consumer<ChannelConfig> config) {
        channel = ChannelFactory.createChannel(address, this);
        ChannelInitializer.applyConfigValues(this, channel, config);

        ServerThreadInitializer.Data serverData = new ServerThreadInitializer.Data (
                channel,
                optionSet.toArray(new ChannelOption[0])
        );

        new ServerThreadInitializer(serverData).start();
        return channel;
    }

}
