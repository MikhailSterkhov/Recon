package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.exception.ReconRuntimeException;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class SocketFactory {

    public static ServerSocket createServerSocket(ChannelOption[] options, InetSocketAddress address) {
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();

            for (ChannelOption channelOption : options) {
                try {
                    channelOption.apply(channel);
                }
                catch (UnsupportedOperationException ignored) {
                    // ignored exception.
                }
            }

            InetSocketAddress resolvedAddress = new InetSocketAddress(address.getHostString(), address.getPort());
            channel.bind(resolvedAddress);

            return channel.socket();
        }
        catch (IOException exception) {
            throw new ReconRuntimeException(exception);
        }
    }

    public static Socket createClientSocket(ChannelOption[] options, InetSocketAddress address, int timeout) {
        try {
            SocketChannel channel = SocketChannel.open();

            for (ChannelOption channelOption : options) {
                channelOption.apply(channel);
            }

            Socket socket = channel.socket();
            socket.setSoTimeout(timeout);

            try {
                InetSocketAddress resolvedAddress = new InetSocketAddress(address.getHostString(), address.getPort());
                socket.connect(resolvedAddress, timeout);
            }
            catch (IOException ignored) {
                // ignored exception.
            }

            return socket;
        }
        catch (IOException exception) {
            throw new ReconRuntimeException(exception);
        }
    }
}
