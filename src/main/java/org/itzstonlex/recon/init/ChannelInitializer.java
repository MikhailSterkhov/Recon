package org.itzstonlex.recon.init;

import org.itzstonlex.recon.*;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.option.ChannelOption;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class ChannelInitializer implements RemoteChannel {

    private static class Config implements ChannelConfig {
        private final RemoteConnection connection;
        private final RemoteChannel channel;

        public Config(RemoteConnection connection, RemoteChannel channel) {
            this.connection = connection;
            this.channel = channel;
        }

        @Override
        public void option(ChannelOption option) {
            connection.setOption(option);
        }

        @Override
        public ChannelPipeline pipeline() {
            return channel.pipeline();
        }

        @Override
        public InetSocketAddress address() {
            return channel.address();
        }
    }

    public static void applyConfigValues(RemoteConnection connection, RemoteChannel channel, Consumer<ChannelConfig> config) {
        if (config != null) {
            config.accept(new Config(connection, channel));
        }
    }


    public ByteStream.Output buffer;

    private boolean closed;

    private final RemoteConnection connection;

    private final InetSocketAddress address;

    private final ConnectionLogger logger;
    private final ChannelPipeline pipeline;

    public ChannelInitializer(RemoteConnection connection, InetSocketAddress address, ConnectionLogger logger) {
        this.connection = connection;
        this.address = address;
        this.logger = logger;

        this.pipeline = new PipelineInitializer(this);
    }

    @Override
    public RemoteConnection connection() {
        return connection;
    }

    @Override
    public ConnectionLogger logger() {
        return logger;
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public InetSocketAddress address() {
        return address;
    }

    @Override
    public ByteStream.Output buffer() {
        return buffer;
    }

    @Override
    public void write(int packetID) {
        ByteStream.Output buffer = BufferFactory.createPooledOutput();
        buffer.writeInt(packetID);

        this.write(buffer);
    }

    @Override
    public void write(ByteStream.Output buffer) {
        this.buffer = buffer;
    }

    @Override
    public void flush() {
        if (buffer == null) {
            return;
        }

        this.buffer.reset();
        this.buffer = null;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
