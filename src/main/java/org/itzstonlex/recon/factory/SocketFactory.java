package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.error.ReconRuntimeError;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class SocketFactory {

    public static ServerSocket createServerSocket(ChannelOption[] options, InetSocketAddress address) {
        try {
            ServerSocket serverSocket = new ServerSocket();

            for (ChannelOption channelOption : options) {
                channelOption.apply(serverSocket);
            }

            InetSocketAddress resolvedAddress = new InetSocketAddress(address.getHostString(), address.getPort());
            serverSocket.bind(resolvedAddress);

            return serverSocket;
        }
        catch (IOException exception) {
            throw new ReconRuntimeError(exception);
        }
    }

    public static Socket createClientSocket(ChannelOption[] options, InetSocketAddress address, int timeout) {
        try {
            Socket socket = new Socket();
            socket.setSoTimeout(timeout);

            for (ChannelOption channelOption : options) {
                channelOption.apply(socket);
            }

            try {
                InetSocketAddress resolvedAddress = new InetSocketAddress(address.getHostString(), address.getPort());
                socket.connect(resolvedAddress, timeout);
            }
            catch (IOException ignored) {
            }

            return socket;
        }
        catch (IOException exception) {
            throw new ReconRuntimeError(exception);
        }
    }

}
