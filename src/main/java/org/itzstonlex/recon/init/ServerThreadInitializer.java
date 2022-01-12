package org.itzstonlex.recon.init;

import org.itzstonlex.recon.*;
import org.itzstonlex.recon.error.SocketThreadError;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.ChannelFactory;
import org.itzstonlex.recon.factory.ContextFactory;
import org.itzstonlex.recon.factory.SocketFactory;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.util.InputUtils;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ServerThreadInitializer
        extends Thread {

    private static int threadsCounter = 1;

    public static final class Data {

        public final RemoteChannel channel;
        public final ChannelOption[] options;

        public Data(RemoteChannel channel, ChannelOption[] options) {
            this.channel = channel;
            this.options = options;
        }
    }


    private final Data data;

    private final ExecutorService newConnectionsExecutor = Executors.newSingleThreadExecutor();

    private final Map<Socket, RemoteChannel> connected = new ConcurrentHashMap<>();

    public ServerThreadInitializer(Data data) {
        super("recon-server-" + threadsCounter++);
        this.data = data;
    }

    private RemoteChannel newClientChannel(InetSocketAddress address) {
        return ChannelFactory.createChannel(address, new Client());
    }

    private void executeEvent(Consumer<ChannelListener> handler) {
        for (ChannelListener channelListener : data.channel.pipeline().nodes()) {
            handler.accept(channelListener);
        }
    }

    private void detectNewConnections(ServerSocket serverSocket) {
        newConnectionsExecutor.execute(() -> {

            while (!data.channel.isClosed()) {
                if (!serverSocket.isBound()) {
                    continue;
                }

                try {
                    Socket accept = serverSocket.accept();
                    RemoteChannel clientChannel = newClientChannel((InetSocketAddress) accept.getRemoteSocketAddress());

                    connected.put(accept, clientChannel);
                    addSocketToAutoInactiveDetect(accept, clientChannel);

                    executeEvent(channelListener -> channelListener.onClientConnected(clientChannel,
                            ContextFactory.createSuccessEventContext(clientChannel, channelListener)
                    ));

                } catch (Exception exception) {
                    executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
                }
            }
        });
    }

    private int readBuf;
    private void addSocketToAutoInactiveDetect(Socket socket, RemoteChannel channel) {
        channel.connection().getThread().execute(() -> {
            try {
                readBuf = socket.getInputStream().read();
            }
            catch (Exception ignored) {
                try {
                    disconnectChannel(socket);
                }
                catch (Exception exception) {
                    executeEvent(channelListener -> channelListener.onExceptionCaught(channel, new SocketThreadError(exception)));
                }
            }
        });
    }

    private void disconnectChannel(Socket socket)
    throws Exception {
        RemoteChannel channel = connected.remove(socket);

        if (channel == null) {
            return;
        }

        executeEvent(channelListener -> channelListener.onClientClosed(channel,
                ContextFactory.createSuccessEventContext(channel, channelListener)
        ));

        channel.close();
        socket.close();
    }

    private void detectIncomingStream(Socket socket, RemoteChannel clientChannel)
    throws Exception {

        InputStream inputStream = socket.getInputStream();
        if (InputUtils.isEmpty(inputStream)) {
            return;
        }

        // Push buf read value.
        ByteStream.Output transformer = BufferFactory.createPooledOutput();

        if (readBuf >= 0) {
            transformer.writeByte((byte) readBuf);
        }

        transformer.write(InputUtils.toByteArray(inputStream));
        readBuf = -1;

        // Read bytes handle.
        ByteStream.Input buffer = BufferFactory.createPooledInput(
                transformer.array()
        );

        executeEvent(channelListener -> channelListener.onRead(clientChannel,
                ContextFactory.createSuccessEventContext(clientChannel, channelListener),
                buffer
        ));
    }

    private void detectOutgoingStream(Socket socket, RemoteChannel clientChannel)
    throws Exception {

        ByteStream.Output buffer = clientChannel.buffer();
        if (buffer == null) {
            return;
        }

        executeEvent(channelListener -> channelListener.onWrite(data.channel,
                ContextFactory.createSuccessEventContext(data.channel, channelListener),
                buffer
        ));

        socket.getOutputStream().write(buffer.array());
        clientChannel.flush();
    }

    private void shutdown(ServerSocket serverSocket)
    throws Exception {

        // Call event of that connection closed.
        executeEvent(channelListener -> channelListener.onClosed(
                ContextFactory.createSuccessEventContext(data.channel, channelListener, new SocketThreadError("Channel was closed"))
        ));

        // Close a connections.
        data.channel.close();
        serverSocket.close();

        // Stop the connection thread.
        Thread.currentThread().stop();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = SocketFactory.createServerSocket(
                    data.options,
                    data.channel.address()
            );

            // Call event of active that connection.
            executeEvent(channelListener -> channelListener.onThreadActive(
                    ContextFactory.createSuccessEventContext(data.channel, channelListener)
            ));

            // Detect new client connections.
            detectNewConnections(serverSocket);

            // While server connection channel was active.
            while (!data.channel.isClosed()) {

                // Check all connected clients.
                for (Map.Entry<Socket, RemoteChannel> clientEntry : connected.entrySet()) {

                    Socket clientSocket = clientEntry.getKey();
                    RemoteChannel clientChannel = clientEntry.getValue();

                    try {
                        // Get filled buffer & send bytes to connection output.
                        detectOutgoingStream(clientSocket, clientChannel);

                        // Detect received bytes from the client.
                        detectIncomingStream(clientSocket, clientChannel);
                    }

                    catch (Exception exception) {
                        executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
                    }
                }
            }

            shutdown(serverSocket);
        }

        catch (Exception exception) {
            executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
        }
    }

}
