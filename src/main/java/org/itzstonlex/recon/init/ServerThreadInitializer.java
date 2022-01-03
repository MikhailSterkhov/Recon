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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ServerThreadInitializer
        extends Thread {

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
        this.data = data;
    }

    private RemoteChannel newClientChannel(InetSocketAddress address) {
        return ChannelFactory.createClientChannel(address, new Client());
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

                try (Socket accept = serverSocket.accept()) {

                    RemoteChannel clientChannel = newClientChannel((InetSocketAddress) accept.getRemoteSocketAddress());
                    connected.put(accept, clientChannel);

                    executeEvent(channelListener -> channelListener.onNewClientActive(clientChannel,
                            ContextFactory.createSuccessEventContext(clientChannel, channelListener)
                    ));
                }
                catch (Exception exception) {
                    executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
                }
            }
        });
    }

    private void detectInactiveConnections(Set<Socket> clientSockets) {
        // ...
    }

    private void detectIncomingStream(Socket clientSocket, RemoteChannel clientChannel)
    throws Exception {

        InputStream receivedBytes = clientSocket.getInputStream();

        if (!InputUtils.isEmpty(receivedBytes)) {
            ByteStream.Input buffer = BufferFactory.createPooledInput(
                    InputUtils.toByteArray(receivedBytes)
            );

            if (buffer.size() <= 0) {
                return;
            }

            executeEvent(channelListener -> channelListener.onRead(clientChannel,
                    ContextFactory.createSuccessEventContext(clientChannel, channelListener),
                    buffer
            ));

            buffer.reset();
        }
    }

    private void detectOutgoingStream(Socket clientSocket, RemoteChannel clientChannel)
    throws Exception {

        ByteStream.Output buffer = clientChannel.buffer();

        if (buffer == null || buffer.toByteArray().length <= 0) {
            return;
        }

        executeEvent(channelListener -> channelListener.onWrite(data.channel,
                ContextFactory.createSuccessEventContext(data.channel, channelListener),
                buffer
        ));

        clientSocket.getOutputStream().write(buffer.toByteArray());
        clientChannel.flush();
    }

    private void shutdown(ServerSocket serverSocket)
    throws Exception {

        // Call event of that connection closed.
        executeEvent(channelListener -> channelListener.onInactive(
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
            executeEvent(channelListener -> channelListener.onActive(
                    ContextFactory.createSuccessEventContext(data.channel, channelListener)
            ));

            // Detect new client connections.
            detectNewConnections(serverSocket);

            // While server connection channel was active.
            while (!data.channel.isClosed()) {
                System.out.println(0);
                Thread.sleep(1000);

                // Check all connected clients.
                for (Map.Entry<Socket, RemoteChannel> clientEntry : connected.entrySet()) {

                    Socket clientSocket = clientEntry.getKey();
                    RemoteChannel clientChannel = clientEntry.getValue();

                    try {
                        // Detect received bytes from the client.
                        detectIncomingStream(clientSocket, clientChannel);

                        // Get filled buffer & send bytes to connection output.
                        detectOutgoingStream(clientSocket, clientChannel);
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
