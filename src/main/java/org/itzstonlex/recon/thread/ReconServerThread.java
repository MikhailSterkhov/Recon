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

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
                    RemoteChannel clientChannel = newClientChannel((InetSocketAddress) accept.getRemoteSocketAddress());

                    for (ChannelOption channelOption : data.options) {
                        try {
                            channelOption.apply(accept);
                        }
                        catch (UnsupportedOperationException ignored) {
                            // ignored exception.
                        }
                    }

                    connected.put(accept, clientChannel);
                    addSocketToAutoInactiveDetect(accept, clientChannel);

                    data.channel.pipeline().fireClientConnectedEvent(clientChannel);

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

                    addSocketToAutoInactiveDetect(socket, channel);
                }
            }
            catch (Exception ignored) {
                try {
                    disconnectChannel(socket);
                }
                catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
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

        data.channel.pipeline().fireClientClosedEvent(channel);

        channel.close();
        socket.close();
    }

    private void detectIncomingStream(SocketChannel socketChannel)
            throws Exception {

        ByteBuffer channelBuffer = ByteBuffer.wrap(new byte[8192]);
        int size = socketChannel.read(channelBuffer);

        if (size <= 0) {
            return;
        }

        // Push buf read value.
        ByteStream.Output converter = BufferFactory.createPooledOutput();

        if (readBuf > 0) {
            converter.writeByte((byte) readBuf);
        }

        converter.write(Arrays.copyOf(channelBuffer.array(), size));
        readBuf = -1;

        // Read bytes handle.
        ByteStream.Input inputBuffer = BufferFactory.createPooledInput(
                converter.array()
        );

        data.channel.pipeline().fireReadEvent(inputBuffer);
    }

    private void detectOutgoingStream(SocketChannel socketChannel, RemoteChannel clientChannel)
            throws Exception {

        ByteStream.Output buffer = clientChannel.buffer();

        if (buffer != null) {
            socketChannel.write( ByteBuffer.wrap(buffer.array()) );

            data.channel.pipeline().fireWriteEvent(buffer);
            clientChannel.flush();
        }
    }

    private void shutdown(ServerSocket serverSocket)
    throws Exception {

        // Close a connections.
        data.channel.close();
        serverSocket.close();

        // Call event of that connection closed.
        data.channel.pipeline().fireClosedEvent();

        // Stop the connection thread.
        interrupt();
        stop();
    }

    @Override
    public void run() {
        // Call event of active that connection.
        data.channel.pipeline().fireThreadActiveEvent();

        try {
            ServerSocket serverSocket = SocketFactory.createServerSocket(
                    data.options,
                    data.channel.address()
            );

            // Call event of bind that socket.
            data.channel.pipeline().fireBindEvent();

            // Detect new client connections.
            detectNewConnections(serverSocket);

            // While server connection channel was active.
            while (!data.channel.isClosed()) {

                // Check all connected clients.
                for (Map.Entry<Socket, RemoteChannel> clientEntry : connected.entrySet()) {

                    Socket clientSocket = clientEntry.getKey();
                    RemoteChannel clientChannel = clientEntry.getValue();

                    try {
                        // Detect received bytes from the client.
                        detectIncomingStream(clientSocket.getChannel());

                        // Get filled buffer & send bytes to connection output.
                        detectOutgoingStream(clientSocket.getChannel(), clientChannel);
                    }
                    catch (Exception exception) {

                        if (exception.getMessage() == null || !exception.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
                            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                        }
                    }
                }
            }

            shutdown(serverSocket);
        }

        catch (Exception exception) {
            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
        }
    }

}
