package org.itzstonlex.recon.init;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelListener;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.error.SocketThreadError;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.ContextFactory;
import org.itzstonlex.recon.factory.SocketFactory;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.util.InputUtils;
import org.itzstonlex.recon.util.reconnect.ChannelReconnectListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

public final class ClientThreadInitializer
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

    public ClientThreadInitializer(Data data) {
        super("recon-client-" + threadsCounter++);
        this.data = data;
    }

    private void executeEvent(Consumer<ChannelListener> handler) {
        for (ChannelListener channelListener : data.channel.pipeline().nodes()) {
            handler.accept(channelListener);
        }
    }

    private void detectOutgoingStream(OutputStream outputStream)
    throws Exception {

        ByteStream.Output buffer = data.channel.buffer();

        if (buffer != null) {
            executeEvent(channelListener -> channelListener.onWrite(data.channel,
                    ContextFactory.createSuccessEventContext(data.channel, channelListener),
                    buffer
            ));

            outputStream.write(buffer.array());
            data.channel.flush();
        }
    }

    private void detectIncomingStream(InputStream inputStream) {
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

        executeEvent(channelListener -> channelListener.onRead(data.channel,
                ContextFactory.createSuccessEventContext(data.channel, channelListener),
                buffer
        ));
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
                }
            }
            catch (Exception ignored) {

                try {
                    shutdown(socket);

                    Thread.currentThread().interrupt();
                    Thread.currentThread().stop();
                }
                catch (Exception exception) {
                    executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
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
        executeEvent(channelListener -> channelListener.onClosed(
                ContextFactory.createSuccessEventContext(data.channel, channelListener)
        ));

        // Stop the connection thread.
        interrupt();
        stop();
    }

    private void timedOut(Socket socket)
            throws Exception {

        // Call event of that connection closed.
        executeEvent(channelListener -> channelListener.onTimedOut(data.channel,
                ContextFactory.createErrorEventContext(data.channel, channelListener, new SocketThreadError("timed out"))));

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
        try {

            Socket socket = SocketFactory.createClientSocket(data.options, data.channel.address(), data.timeout);
            long maxConnectionMillis = System.currentTimeMillis() + data.timeout;

            executeEvent(channelListener -> channelListener.onThreadActive(
                    ContextFactory.createSuccessEventContext(data.channel, channelListener)
            ));

            while (!data.channel.isClosed()) {

                // Check timed out.
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

                    executeEvent(channelListener -> channelListener.onConnected(
                            ContextFactory.createSuccessEventContext(data.channel, channelListener)
                    ));

                    detectServerInactive(socket);
                }

                try {
                    // Get filled buffer & send bytes to connection output.
                    detectOutgoingStream(socket.getOutputStream());

                    // Detect received bytes from the server.
                    detectIncomingStream(socket.getInputStream());
                }

                catch (Exception exception) {
                    executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
                }
            }

            shutdown(socket);
        }

        catch (Exception exception) {
            executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
        }
    }
}
