package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.error.ReconRuntimeError;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public final class SocketFactory {

    public static ServerSocket createServerSocket(ChannelOption[] options, InetSocketAddress address) {
        try {
            ServerSocket serverSocket = new ServerSocket();

            for (ChannelOption channelOption : options) {
                channelOption.apply(serverSocket);
            }

            Executors.newCachedThreadPool().submit(() -> {
                try {
                    serverSocket.bind(address);
                }
                catch (IOException exception) {
                    throw new ReconRuntimeError(exception);
                }
            });

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

            Executors.newCachedThreadPool().submit(() -> {
                try {
                    socket.connect(address, timeout);
                }
                catch (IOException exception) {
                    throw new ReconRuntimeError(exception);
                }
            });

            return socket;
        }
        catch (IOException exception) {
            throw new ReconRuntimeError(exception);
        }
    }

}
