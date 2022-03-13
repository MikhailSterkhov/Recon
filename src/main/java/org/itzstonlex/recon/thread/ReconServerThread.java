package org.itzstonlex.recon.thread;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.exception.ReconThreadException;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.ChannelFactory;
import org.itzstonlex.recon.factory.SocketFactory;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.util.ReconThreadsStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ReconServerThread
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

    private final Map<Socket, RemoteChannel> connected = new ConcurrentHashMap<>();
    private final Map<Socket, ExecutorService> readExecutors = new ConcurrentHashMap<>();

    public ReconServerThread(Data data) {
        super("ReconServer-" + threadsCounter++);
        this.data = data;

        ReconThreadsStorage.addServerThread(this);
    }

    private RemoteChannel newClientChannel(InetSocketAddress address) {
        return ChannelFactory.createChannel(address, new Client());
    }

    private void detectNewConnections(ServerSocket serverSocket) {
        data.channel.connection().getThread().execute(() -> {

            while (!data.channel.isClosed()) {
                if (!serverSocket.isBound()) {
                    continue;
                }

                try {
                    Socket accept = serverSocket.accept();
                    RemoteChannel clientChannel = this.newClientChannel((InetSocketAddress) accept.getRemoteSocketAddress());

                    for (ChannelOption channelOption : data.options) {
                        try {
                            channelOption.apply(accept);
                        }
                        catch (UnsupportedOperationException ignored) {
                            // ignored exception.
                        }
                    }

                    connected.put(accept, clientChannel);
                    readExecutors.put(accept, Executors.newCachedThreadPool());

                    data.channel.pipeline().fireClientConnectedEvent(clientChannel);

                    this.addSocketToAutoInactiveDetect(accept, clientChannel);

                } catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                }
            }
        });
    }

    private int readBuf;
    private void addSocketToAutoInactiveDetect(Socket socket, RemoteChannel channel) {
        if (socket.isClosed() || channel.isClosed()) {
            return;
        }

        channel.connection().getThread().execute(() -> {
            try {
                readBuf = socket.getInputStream().read();

                if (readBuf >= 0) {
                    Thread.sleep(1500L);

                    this.addSocketToAutoInactiveDetect(socket, channel);
                }
            }
            catch (Exception ignored) {
                try {
                    this.disconnectClient(socket);
                }
                catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                }
            }
        });
    }

    private void disconnectClient(Socket socket)
    throws Exception {

        RemoteChannel channel = connected.remove(socket);
        ExecutorService readExecutor = readExecutors.remove(socket);

        if (readExecutor != null) {
            readExecutor.shutdown();
        }

        if (channel == null) {
            return;
        }

        data.channel.pipeline().fireClientClosedEvent(channel);

        channel.close();
        socket.close();
    }

    private void detectIncomingStream0(ByteBuffer socketBuffer, SocketChannel socketChannel) {
        int size = 0;
        try {
            size = socketChannel.read(socketBuffer);
        }
        catch (IOException ignored) {
            // ignored exception.
        }

        if (size <= 0) {
            return;
        }

        // Push buf read value.
        ByteStream.Output converter = BufferFactory.createPooledOutput();

        if (readBuf > 0) {
            converter.writeByte((byte) readBuf);
        }

        converter.write(Arrays.copyOf(socketBuffer.array(), size));
        readBuf = -1;

        // Read bytes handle.
        ByteStream.Input inputBuffer = BufferFactory.createPooledInput(
                converter.array()
        );

        data.channel.pipeline().fireReadEvent(inputBuffer);

        socketBuffer.clear();
        this.detectIncomingStream0(socketBuffer, socketChannel);
    }

    private void detectIncomingStream(SocketChannel socketChannel) {
        ExecutorService executorService = readExecutors.get(socketChannel.socket());
        if (executorService == null) {
            return;
        }

        ByteBuffer socketBuffer = ByteBuffer.allocate(8192);
        executorService.execute(() -> this.detectIncomingStream0(socketBuffer, socketChannel));
    }

    private void detectOutgoingStream(SocketChannel socketChannel, RemoteChannel clientChannel)
    throws Exception {

        ByteStream.Output buffer = clientChannel.buffer();

        if (buffer != null) {
            socketChannel.write(ByteBuffer.wrap(buffer.array()));

            data.channel.pipeline().fireWriteEvent(buffer);
            clientChannel.resetBuf();
        }
    }

    private void shutdown(ServerSocket serverSocket)
    throws Exception {

        for (Socket client : connected.keySet()) {
            this.disconnectClient(client);
        }

        // Close connections & threads.
        data.channel.connection().getThread().shutdown();
        data.channel.close();

        serverSocket.close();

        // Call event of that connection closed.
        data.channel.pipeline().fireClosedEvent();

        // Stop the connection thread.
        super.interrupt();
        super.stop();
    }

    @Override
    public void run() {
        // Call event of active that connection.
        data.channel.pipeline().fireThreadActiveEvent();

        try {
            ServerSocket serverSocket = SocketFactory.createServerSocket(
                    data.options, data.channel.address()
            );

            // Call event of bind that socket.
            data.channel.pipeline().fireBindEvent();

            // Detect new client connections.
            this.detectNewConnections(serverSocket);

            // While server connection channel was active.
            while (!data.channel.isClosed()) {

                // Check all connected clients.
                for (Map.Entry<Socket, RemoteChannel> clientEntry : connected.entrySet()) {

                    Socket clientSocket = clientEntry.getKey();
                    RemoteChannel clientChannel = clientEntry.getValue();

                    try {
                        // Detect received bytes from the client.
                        this.detectIncomingStream(clientSocket.getChannel());

                        // Get filled buffer & send bytes to connection output.
                        this.detectOutgoingStream(clientSocket.getChannel(), clientChannel);
                    }
                    catch (Exception exception) {

                        if (exception.getMessage() == null || !exception.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
                            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                        }
                    }
                }
            }

            this.shutdown(serverSocket);
        }

        catch (Exception exception) {
            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
        }
    }

}
