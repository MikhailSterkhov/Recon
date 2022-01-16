package org.itzstonlex.recon.thread;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.exception.ReconThreadException;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.SocketFactory;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.util.ReconThreadsStorage;
import org.itzstonlex.recon.util.reconnect.ChannelReconnectListener;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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

    public ReconClientThread(Data data) {
        super("ReconClient-" + threadsCounter++);
        this.data = data;

        ReconThreadsStorage.addClientThread(this);
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

    private int readBuf;
    private void detectServerInactive(Socket socket) {
        if (socket.isClosed() || data.channel.isClosed()) {
            return;
        }

        data.channel.connection().getThread().execute(() -> {
            try {
                readBuf = socket.getInputStream().read();

                if (readBuf > 0) {
                    Thread.sleep(5000L);

                    detectServerInactive(socket);
                }
            }
            catch (SocketTimeoutException timeoutException) {
                try {
                    Thread.sleep(3000L);
                    detectServerInactive(socket);

                } catch (Exception ignored) {
                    // ignored exception.
                }
            }
            catch (Exception ignored) {

                try {
                    shutdown(socket);

                    Thread.currentThread().interrupt();
                    Thread.currentThread().stop();
                }
                catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                }
            }
        });
    }

    private synchronized void shutdown(Socket socket)
    throws Exception {

        // Close a connections.
        data.channel.close();
        socket.close();

        // Call event of that connection closed.
        data.channel.pipeline().fireClosedEvent();

        // Stop the connection thread.
        interrupt();
        stop();
    }

    private void timedOut(Socket socket)
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
        shutdown(socket);
    }

    @Override
    public void run() {
        long maxConnectionMillis = System.currentTimeMillis() + data.timeout + 100L;

        // Call event of active that connection.
        data.channel.pipeline().fireThreadActiveEvent();

        try {
            Socket socket = SocketFactory.createClientSocket(
                    data.options,
                    data.channel.address(),
                    data.timeout
            );

            while (!data.channel.isClosed()) {

                // Check connection timeout.
                if (!socket.isConnected()) {
                    if (maxConnectionMillis > 0 && maxConnectionMillis - System.currentTimeMillis() < 0) {
                        timedOut(socket);
                        return;
                    }

                    continue;
                }

                // Call connected event.
                if (maxConnectionMillis != 0) {
                    maxConnectionMillis = 0;

                    data.channel.pipeline().fireConnectedEvent();

                    detectServerInactive(socket);
                }

                try {
                    // Detect received bytes from the server.
                    detectIncomingStream(socket.getChannel());

                    // Get filled buffer & send bytes to connection output.
                    detectOutgoingStream(socket.getChannel(), data.channel);
                }

                catch (Exception exception) {
                    data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
                }
            }

            shutdown(socket);
        }
        catch (Exception exception) {
            data.channel.pipeline().fireExceptionCaughtEvent(new ReconThreadException(exception));
        }
    }
}
