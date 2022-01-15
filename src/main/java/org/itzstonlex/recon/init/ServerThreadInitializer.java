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

                    data.channel.pipeline().fireClientConnectedEvent();

                } catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new SocketThreadError(exception));
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
                    data.channel.pipeline().fireExceptionCaughtEvent(new SocketThreadError(exception));
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

        data.channel.pipeline().fireClientClosedEvent();

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

        data.channel.pipeline().fireReadEvent(buffer);
    }

    private void detectOutgoingStream(Socket socket, RemoteChannel clientChannel)
    throws Exception {

        ByteStream.Output buffer = clientChannel.buffer();
        if (buffer == null) {
            return;
        }

        data.channel.pipeline().fireWriteEvent(buffer);

        socket.getOutputStream().write(buffer.array());
        clientChannel.flush();
    }

    private void shutdown(ServerSocket serverSocket)
    throws Exception {

        // Call event of that connection closed.
        data.channel.pipeline().fireClosedEvent();

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
            data.channel.pipeline().fireThreadActiveEvent();

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
                        data.channel.pipeline().fireExceptionCaughtEvent(new SocketThreadError(exception));
                    }
                }
            }

            shutdown(serverSocket);
        }

        catch (Exception exception) {
            data.channel.pipeline().fireExceptionCaughtEvent(new SocketThreadError(exception));
        }
    }

}
