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

import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class ClientThreadInitializer
        extends Thread {

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
    private final ExecutorService inactiveServerThread = Executors.newCachedThreadPool();

    public ClientThreadInitializer(Data data) {
        this.data = data;
    }

    private void executeEvent(Consumer<ChannelListener> handler) {
        for (ChannelListener channelListener : data.channel.pipeline().nodes()) {
            handler.accept(channelListener);
        }
    }

    private void detectOutgoingStream(Socket socket)
    throws Exception {

        ByteStream.Output buffer = data.channel.buffer();

        if (buffer != null) {
            executeEvent(channelListener -> channelListener.onWrite(data.channel,
                    ContextFactory.createSuccessEventContext(data.channel, channelListener),
                    buffer
            ));

            socket.getOutputStream().write(buffer.toByteArray());
            data.channel.flush();
        }
    }

    private void detectIncomingStream(Socket socket)
    throws Exception {

        InputStream inputStream = socket.getInputStream();

        if (!InputUtils.isEmpty(inputStream)) {
            ByteStream.Input buffer = BufferFactory.createPooledInput(
                    InputUtils.toByteArray(inputStream)
            );

            executeEvent(channelListener -> channelListener.onRead(data.channel,
                    ContextFactory.createSuccessEventContext(data.channel, channelListener),
                    buffer
            ));

            buffer.reset();
        }
    }

    private void detectServerInactive(Socket socket) {
        inactiveServerThread.execute(() -> {

            try {
                if (InputUtils.isClosed(socket)) {
                    shutdown(socket);
                }
            }

            catch (Exception exception) {
                executeEvent(channelListener -> channelListener.onExceptionCaught(data.channel, new SocketThreadError(exception)));
            }
        });
    }

    private void shutdown(Socket socket)
    throws Exception {

        // Call event of that connection closed.
        executeEvent(channelListener -> channelListener.onInactive(
                ContextFactory.createSuccessEventContext(data.channel, channelListener, new SocketThreadError("Channel was closed"))
        ));

        // Close a connections.
        data.channel.close();
        socket.close();

        // Stop the connection thread.
        Thread.currentThread().stop();
    }

    @Override
    public void run() {
        try {
            Socket socket = SocketFactory.createClientSocket(data.options, data.channel.address(), data.timeout);

            executeEvent(channelListener -> channelListener.onActive(
                    ContextFactory.createSuccessEventContext(data.channel, channelListener)
            ));

            while (!socket.isClosed()) {
                try {
                    detectServerInactive(socket);

                    // Detect received bytes from the server.
                    detectIncomingStream(socket);

                    // Get filled buffer & send bytes to connection output.
                    detectOutgoingStream(socket);
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
