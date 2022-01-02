package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.side.client.Client;
import org.itzstonlex.recon.side.server.Server;

import java.net.InetSocketAddress;

public final class ChannelFactory {

    public static RemoteChannel createServerChannel(InetSocketAddress address, Server server) {
        return new ChannelImpl(address, address);
    }

    public static RemoteChannel createClientChannel(InetSocketAddress address, Client client) {
        return new ChannelImpl(address, address);
    }

    private static class ChannelImpl implements RemoteChannel {

        public ByteStream.Output buffer;

        private boolean closed;

        private final InetSocketAddress address;
        private final InetSocketAddress localAddress;

        public ChannelImpl(InetSocketAddress address, InetSocketAddress localAddress) {
            this.address = address;
            this.localAddress = localAddress;
        }

        // TODO FIXME OK DA
        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public InetSocketAddress address() {
            return address;
        }

        @Override
        public InetSocketAddress localAddress() {
            return localAddress;
        }

        @Override
        public ByteStream.Output buffer() {
            return buffer;
        }

        @Override
        public void write(ByteStream.Output buffer) {
            this.buffer = buffer;
        }

        @Override
        public void flush() {
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

}
