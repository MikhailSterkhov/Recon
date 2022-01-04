package org.itzstonlex.recon.util;

import org.itzstonlex.recon.*;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.error.BufferReadError;
import org.itzstonlex.recon.error.SocketThreadError;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class FastRecon {

    public static RemoteConnectionBuilder newConnection(InetSocketAddress address) {
        return new RemoteConnectionBuilder(address);
    }

    public static RemoteConnectionBuilder newConnection(String host, int port) {
        return newConnection(new InetSocketAddress(host, port));
    }

    public static RemoteConnectionBuilder newLocalConnection(int port) {
        return newConnection("127.0.0.1", port);
    }

    public static final class RemoteConnectionBuilder {

        private static class PipelineNode {
            public final String target;
            public final String id;
            public final ChannelListener handler;

            public PipelineNode(String target, String id, ChannelListener handler) {
                this.target = target;
                this.id = id;
                this.handler = handler;
            }

            public PipelineNode(String id, ChannelListener handler) {
                this(null, id, handler);
            }
        }

        private final InetSocketAddress address;

        private final List<Consumer<ChannelConfig>> channel_configActions = new ArrayList<>();

        private final List<PipelineNode> pipeline_lastNodes = new ArrayList<>();
        private final List<PipelineNode> pipeline_afterNodes = new ArrayList<>();
        private final List<PipelineNode> pipeline_beforeNodes = new ArrayList<>();

        private int client_timeoutValue = 5000;

        private RemoteConnectionBuilder(InetSocketAddress address) {
            this.address = address;
        }


        public RemoteConnectionBuilder channel_addConfigAction(Consumer<ChannelConfig> configAction) {
            channel_configActions.add(configAction);
            return this;
        }

        public RemoteConnectionBuilder pipeline_addLast(String id, ChannelListener byteHandler) {
            pipeline_lastNodes.add(new PipelineNode(id, byteHandler));
            return this;
        }

        public RemoteConnectionBuilder pipeline_addAfter(String target, String id, ChannelListener byteHandler) {
            pipeline_afterNodes.add(new PipelineNode(target, id, byteHandler));
            return this;
        }

        public RemoteConnectionBuilder pipeline_addBefore(String target, String id, ChannelListener byteHandler) {
            pipeline_beforeNodes.add(new PipelineNode(target, id, byteHandler));
            return this;
        }

        public RemoteConnectionBuilder client_setTimeout(int timeout) {
            this.client_timeoutValue = timeout;
            return this;
        }


        private void initPipeline(ChannelPipeline pipeline) {
            for (PipelineNode pipelineNode : pipeline_lastNodes) {
                pipeline.putLast(pipelineNode.id, pipelineNode.handler);
            }

            for (PipelineNode pipelineNode : pipeline_afterNodes) {
                pipeline.putAfter(pipelineNode.target, pipelineNode.id, pipelineNode.handler);
            }

            for (PipelineNode pipelineNode : pipeline_beforeNodes) {
                pipeline.putBefore(pipelineNode.target, pipelineNode.id, pipelineNode.handler);
            }
        }

        private void initConfigActions(ChannelConfig config) {
            for (Consumer<ChannelConfig> configAction : channel_configActions) {
                configAction.accept(config);
            }
        }


        public Client asClient() {
            return Machine.fastConnect(address, client_timeoutValue, config -> {

                initPipeline(config.pipeline());
                initConfigActions(config);
            });
        }

        public Server asServer() {
            return Machine.fastBind(address, config -> {

                initPipeline(config.pipeline());
                initConfigActions(config);
            });
        }
    }

    public static final class Machine {

        public static Server fastBind(String host, int port, Consumer<ChannelConfig> config) {
            return fastBind(new InetSocketAddress(host, port), config);
        }

        public static Server fastBind(InetSocketAddress address, Consumer<ChannelConfig> config) {
            Server server = new Server();
            server.bind(address, config);

            return server;
        }

        public static Client fastConnect(String host, int port, Consumer<ChannelConfig> config) {
            return fastConnect(new InetSocketAddress(host, port), config);
        }

        public static Client fastConnect(InetSocketAddress address, Consumer<ChannelConfig> config) {
            Client client = new Client();
            client.connect(address, config);

            return client;
        }

        public static Client fastConnect(InetSocketAddress address, int timeout, Consumer<ChannelConfig> config) {
            Client client = new Client();
            client.connect(address, timeout, config);

            return client;
        }

        public static void writeSingletonBytes(String host, int port, Consumer<ByteStream.Output> bufferAppender) {
            writeSingletonBytes(new InetSocketAddress(host, port), bufferAppender);
        }

        public static void writeSingletonBytes(InetSocketAddress address, Consumer<ByteStream.Output> bufferAppender) {
            if (bufferAppender == null) {
                return;
            }

            fastConnect(address, config -> config.pipeline().putLast("singleton-buf-write-handler", new ChannelListenerAdapter(null) {

                @Override
                public void onThreadActive(ContextHandler contextHandler) {
                    ByteStream.Output buffer = BufferFactory.createPooledOutput();
                    bufferAppender.accept(buffer);

                    contextHandler.channel().write(buffer);
                }

                @Override
                public void onWrite(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Output buffer) {
                    try {
                        contextHandler.channel().close();
                    } catch (Exception exception) {
                        throw new SocketThreadError(exception);
                    }
                }
            }));
        }

    }

    public static final class Buffer {

        public static byte[] initBytes(Consumer<ByteStream.Output> bufferAppender) {
            ByteStream.Output buffer = BufferFactory.createPooledOutput();

            if (bufferAppender != null) {
                bufferAppender.accept(buffer);
            }

            return buffer.toByteArray();
        }

        public static byte[] initBytes(int packetID, Consumer<ByteStream.Output> bufferAppender) {
            ByteStream.Output buffer = BufferFactory.createPooledOutput();
            buffer.writeInt(packetID);

            if (bufferAppender != null) {
                bufferAppender.accept(buffer);
            }

            return buffer.toByteArray();
        }

        public static byte[] transformInput(ByteStream.Input input) {
            return BufferFactory.transformOutput(input).toByteArray();
        }
    }

    public static final class HTTP {

        private static final ExecutorService HTTP_CONNECTION_THREAD
                = Executors.newCachedThreadPool();

        public static void fastHttpConnect(String url, BiConsumer<String, Throwable> callback) {
            if (url == null || callback == null) {
                return;
            }

            HTTP_CONNECTION_THREAD.submit(() -> {

                try (InputStream inputStream = new URL(url).openStream();
                     Scanner scanner = new Scanner(inputStream)) {

                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNext()) {
                        stringBuilder.append(scanner.nextLine()).append("\n");
                    }

                    String result = stringBuilder.toString();

                    if (!result.isEmpty()) {
                        callback.accept(result, null);

                    } else {

                        callback.accept(null, new BufferReadError("Empty HTTP callback"));
                    }
                } catch (Exception exception) {
                    callback.accept(null, exception);
                }
            });
        }
    }

}
