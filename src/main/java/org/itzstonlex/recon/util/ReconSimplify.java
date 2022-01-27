package org.itzstonlex.recon.util;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ReconSimplify {

    public static final Buf BYTE_BUF    = new Buf();
    public static final Web WEB         = new Web();
    public static final Remote REMOTE   = new Remote();

    public static final class Buf {
        private Buf() { }

        public ByteStream.Input input(int size) {
            return input(new byte[size]);
        }

        public ByteStream.Input input(byte[] array) {
            return BufferFactory.createPooledInput(array);
        }

        public ByteStream.Input input(InputStream inputStream) {
            return input(InputUtils.toByteArray(inputStream));
        }

        public ByteStream.Input convert(ByteStream.Output output) {
            return input(output.array());
        }

        public ByteStream.Output output() {
            return BufferFactory.createPooledOutput();
        }

        public ByteStream.Output output(byte[] array) {
            return BufferFactory.transformOutput(array);
        }

        public ByteStream.Output output(OutputStream outputStream) {
            return BufferFactory.transformOutput(((ByteArrayOutputStream) outputStream).toByteArray());
        }

        public ByteStream.Output convert(InputStream inputStream) {
            return output(InputUtils.toByteArray(inputStream));
        }

        public ByteStream.Output convert(ByteStream.Input input) {
            return output(input.array());
        }

        public void write(RemoteChannel channel, byte[] bytes) {
            channel.write(output(bytes));
        }
    }

    public static final class Web {
        private Web() { }

        private static final ExecutorService WEB_EXECUTOR = Executors.newCachedThreadPool(
                ReconThreadFactory.asInstance("ReconSimplify-Web-%s")
        );

        public boolean executeSync(String url, String method, Map<String, String> headers,
                                   BiConsumer<Integer, String> responseHandler) {
            try {
                HttpURLConnection http = ((HttpURLConnection) new URL(url).openConnection());

                http.setConnectTimeout(2500);
                http.setReadTimeout(5000);

                if (method != null) {
                    http.setRequestMethod(method);
                }

                if (headers != null && !headers.isEmpty()) {
                    headers.forEach(http::setRequestProperty);
                }

                // Handle response.
                if (responseHandler != null) {
                    InputStream inputStream = http.getInputStream();

                    if (InputUtils.isEmpty(inputStream)) {
                        return false;
                    }

                    int responseCode = http.getResponseCode();
                    byte[] responseBytes = InputUtils.toByteArray(inputStream);

                    responseHandler.accept(responseCode,
                            new String(responseBytes, 0, responseBytes.length, StandardCharsets.UTF_8));
                }

                http.disconnect();
                return true;
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return false;
            }
        }

        public boolean executeSync(String url, String method, BiConsumer<Integer, String> responseHandler) {
            return executeSync(url, method, null, responseHandler);
        }

        public boolean executeSync(String url, String method) {
            return executeSync(url, method, null);
        }

        public boolean executeSync(String url, BiConsumer<Integer, String> responseHandler) {
            return executeSync(url, null, responseHandler);
        }

        public void executeAsync(String url, String method, Map<String, String> headers,
                                 BiConsumer<Integer, String> responseHandler) {

            WEB_EXECUTOR.submit(() -> executeSync(url, method, headers, responseHandler));
        }

        public void executeAsync(String url, String method, BiConsumer<Integer, String> responseHandler) {
            executeAsync(url, method, null, responseHandler);
        }

        public void executeAsync(String url, String method) {
            executeAsync(url, method, null);
        }

        public void executeAsync(String url, BiConsumer<Integer, String> responseHandler) {
            executeAsync(url, null, responseHandler);
        }
    }

    public static final class Remote {
        private Remote() { }

        public InetSocketAddress resolveInetAddress(InetSocketAddress address) {
            if (!address.isUnresolved()) {
                return address;
            }

            return new InetSocketAddress(address.getHostString(), address.getPort());
        }

        public RemoteChannel bind(InetSocketAddress address, Consumer<ChannelConfig> channelInitializer) {
            return new Server().bind(resolveInetAddress(address), channelInitializer);
        }

        public RemoteChannel bind(InetSocketAddress address) {
            return new Server().bind(resolveInetAddress(address));
        }

        public RemoteChannel bindLocal(int localPort, Consumer<ChannelConfig> channelInitializer) {
            return new Server().bindLocal(localPort, channelInitializer);
        }

        public RemoteChannel bindLocal(int localPort) {
            return new Server().bindLocal(localPort);
        }

        public RemoteChannel connect(InetSocketAddress address, int timeout, Consumer<ChannelConfig> channelInitializer) {
            return new Client().connect(resolveInetAddress(address), timeout, channelInitializer);
        }

        public RemoteChannel connect(InetSocketAddress address, int timeout) {
            return new Client().connect(resolveInetAddress(address), timeout);
        }

        public RemoteChannel connect(InetSocketAddress address, Consumer<ChannelConfig> channelInitializer) {
            return new Client().connect(resolveInetAddress(address), channelInitializer);
        }

        public RemoteChannel connect(InetSocketAddress address) {
            return new Client().connect(resolveInetAddress(address));
        }

        public RemoteChannel connectLocal(int localPort, int timeout, Consumer<ChannelConfig> channelInitializer) {
            return new Client().connect(new InetSocketAddress("127.0.0.1", localPort), timeout, channelInitializer);
        }

        public RemoteChannel connectLocal(int localPort, Consumer<ChannelConfig> channelInitializer) {
            return new Client().connectLocal(localPort, channelInitializer);
        }

        public RemoteChannel connectLocal(int localPort) {
            return new Client().connectLocal(localPort);
        }
    }

}
