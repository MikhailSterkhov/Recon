package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.error.ConnectionThreadError;
import org.itzstonlex.recon.error.ReconRuntimeError;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ConnectionFactory {

    private static ServerSocket createServerSocket(ChannelOption[] options, InetSocketAddress address) {
        try {
            ServerSocket serverSocket = new ServerSocket();

            for (ChannelOption channelOption : options) {
                channelOption.apply(serverSocket);
            }

            Executors.newCachedThreadPool().submit(() -> {
                try {
                    serverSocket.bind(address);
                    serverSocket.close();
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

    private static void callEventSync(ChannelListener[] listeners, Consumer<ChannelListener> handler) {
        for (ChannelListener channelListener : listeners) {
            handler.accept(channelListener);
        }
    }

    private static Socket createClientSocket(ChannelOption[] options, InetSocketAddress address, int timeout) {
        try {
            Socket socket = new Socket();

            for (ChannelOption channelOption : options) {
                channelOption.apply(socket);
            }

            socket.connect(address, timeout);
            return socket;
        }
        catch (IOException exception) {
            throw new ReconRuntimeError(exception);
        }
    }

    public static void createServerThread(RemoteChannel channel,

                                          ChannelListener[] listeners,
                                          ChannelOption[] options) {

        ExecutorService thread = Executors.newCachedThreadPool();
        thread.submit(() -> {
            Set<Socket> connected = new HashSet<>();

            try {
                ServerSocket serverSocket = createServerSocket(
                        options,
                        channel.address()
                );

                callEventSync(listeners, channelListener -> channelListener.onActive(
                        ContextFactory.createContext(channel, channelListener, true, null)
                ));

                while (!serverSocket.isClosed() && !channel.isClosed()) {

                    if (serverSocket.isBound()) {
                        Socket accept = serverSocket.accept();

                        if (accept != null) {
                            connected.add(accept);
                        }
                    }

                    if (channel.buffer() != null) {
                        for (Socket socket : connected) {
                            socket.getOutputStream().write(channel.buffer().toByteArray());
                        }

                        channel.flush();
                    }

                    // ...
                }

                callEventSync(listeners, channelListener -> channelListener.onInactive(
                        ContextFactory.createContext(channel, channelListener, true, new ConnectionThreadError("Channel was closed"))
                ));

                serverSocket.close();
                thread.shutdown();
            }
            catch (IOException exception) {
                callEventSync(listeners, channelListener ->
                        channelListener.onExceptionCaught(channel, new ConnectionThreadError(exception)));
            }
        });
    }

    public static void createClientThread(RemoteChannel channel,

                                          ChannelListener[] listeners,
                                          ChannelOption[] options,

                                          int timeout) {

        ExecutorService thread = Executors.newCachedThreadPool();
        thread.submit(() -> {

            try {
                Socket socket = createClientSocket(options, channel.address(), timeout);

                callEventSync(listeners, channelListener -> channelListener.onActive(
                        ContextFactory.createContext(channel, channelListener, true, null)
                ));

                while (!socket.isClosed()) {
                    System.out.println(socket.getInputStream().read());
                    System.out.println(socket.getInputStream().available());
                }

                callEventSync(listeners, channelListener -> channelListener.onInactive(
                        ContextFactory.createContext(channel, channelListener, true, new ConnectionThreadError("Channel was closed"))
                ));

                socket.close();
                thread.shutdown();
            }
            catch (IOException exception) {
                callEventSync(listeners, channelListener ->
                        channelListener.onExceptionCaught(channel, new ConnectionThreadError(exception)));
            }
        });
    }

}
