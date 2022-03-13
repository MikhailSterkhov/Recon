package org.itzstonlex.recon.thread;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.exception.ReconThreadException;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.SocketFactory;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.util.ReconThreadsStorage;
import org.itzstonlex.recon.util.reconnect.ChannelReconnectListener;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ReconClientThread
        extends Thread {

    private static int threadsCounter = 1;

    public static final class Data {

        public final RemoteChannel channel;
        public final ChannelOption[] options;

        public final int timeout;

        public Data(RemoteChannel channel, ChannelOption[] options, int timeout) {
            this.channel = channel;
            this.options = options;
            this.timeout = timeout;
        }
    }

    private final Data data;
    private final ExecutorService readExecutor = Executors.newCachedThreadPool();

    public ReconClientThread(Data data) {
        super("ReconClient-" + threadsCounter++);
        this.data = data;

        ReconThreadsStorage.addClientThread(this);
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
        ByteBuffer socketBuffer = ByteBuffer.allocate(8192);
        readExecutor.execute(() -> this.detectIncomingStream0(socketBuffer, socketChannel));
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

    private int readBuf;
    private void detectServerInactive0(Socket socket) {
        try {
            readBuf = socket.getInputStream().read();

            if (readBuf > 0) {
                Thread.sleep(5000L);

                this.detectServerInactive0(socket);
            }
        }
        catch (SocketTimeoutException timeoutException) {
            try {
                Thread.sleep(3000L);
                this.detectServerInactive0(socket);

            } catch (Exception ignored) {
                // ignored exception.
            }
        }
        catch (Exception ignored) {

            try {
                this.shutdown(socket);

                Thread.currentThread().interrupt();
                Thread.currentThread().stop();
            }
            catch (Exception exception) {
                data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
            }
        }
    }

    private void detectServerInactive(Socket socket) {
        if (socket.isClosed() || data.channel.isClosed()) {
            return;
        }

        data.channel.connection().getThread().execute(() -> this.detectServerInactive0(socket));
    }

    private synchronized void shutdown(Socket socket)
    throws Exception {

        // Close a connections.
        data.channel.close();
        socket.close();

        // Call event of that connection closed.
        data.channel.pipeline().fireClosedEvent();

        // Stop the connection thread.
        super.interrupt();
        super.stop();
    }

    private void onClientTimedOut(Socket socket)
    throws Exception {

        // Call event of that connection closed.
        data.channel.pipeline().fireConnectTimeoutEvent();

        // Check reconnect status.
        ChannelReconnectListener reconnectListener = data.channel.pipeline().get(ChannelReconnectListener.class);

        if (reconnectListener != null && reconnectListener.isThreadAlive()) {
            data.channel.close();
            return;
        }

        // Shutdown the connection.
        this.shutdown(socket);
    }

    @Override
    public void run() {
        long maxConnectionMillis = System.currentTimeMillis() + data.timeout + 100L;

        // Call event of active that connection.
        data.channel.pipeline().fireThreadActiveEvent();

        try {
            Socket socket = SocketFactory.createClientSocket(
                    data.options, data.channel.address(), data.timeout
            );

            // Detect received bytes from the server.
            this.detectIncomingStream(socket.getChannel());

            while (!data.channel.isClosed()) {

                // Check connection timeout.
                if (!socket.getChannel().isConnected()) {
                    if (maxConnectionMillis > 0 && maxConnectionMillis - System.currentTimeMillis() < 0) {
                        this.onClientTimedOut(socket);
                        return;
                    }

                    continue;
                }

                // Call connected event.
                if (maxConnectionMillis != 0) {
                    maxConnectionMillis = 0;

                    data.channel.pipeline().fireConnectedEvent();

                    this.detectServerInactive(socket);
                }

                try {
                    // Get filled buffer & send bytes to connection output.
                    this.detectOutgoingStream(socket.getChannel(), data.channel);
                }
                catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                }
            }

            this.shutdown(socket);
        }
        catch (Exception exception) {
            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
        }
    }
}
