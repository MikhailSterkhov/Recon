package org.itzstonlex.recon.metrics.tests;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.log.ConnectionLogger;
import org.itzstonlex.recon.metrics.MetricCounter;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.side.wrapped.AbstractClient;
import org.itzstonlex.recon.side.wrapped.AbstractServer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MetricTests {

    public static void main(String[] args) {
        ReconMetrics reconMetrics = ReconMetrics.newMetricInstance();

        // Server connection
        {
            Server server = new Server(reconMetrics);

            RemoteChannel serverChannel = server.bind();
            serverChannel.logger().info("Channel " + serverChannel.address() + " was success bind");
        }

        // Client`s connection
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {

            int clientsLength = 5;
            for (int i = 0; i < clientsLength; i++) {

                Client client = new Client(reconMetrics);

                RemoteChannel clientChannel = client.connect(1000);
                clientChannel.logger().info("Channel " + clientChannel.address() + " was success connected!");
            }

        }, 2, TimeUnit.SECONDS);

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {

                    System.out.println(ReconMetrics.TOTAL_CLIENTS.currentValue());

                }, 1, 1, TimeUnit.SECONDS);
    }

    private static class Server extends AbstractServer {
        private final ReconMetrics reconMetrics;

        public Server(ReconMetrics reconMetrics) {
            super("localhost", 3305);

            this.reconMetrics = reconMetrics;
        }

        @Override
        public void initChannel(ConnectionLogger logger, ChannelConfig channelConfig) {
            reconMetrics.initPipelines(channelConfig.pipeline());

            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(() -> {

                        logger.info("[Metrics] Total reads length: " + ReconMetrics.TOTAL_READS.valueOf(5, TimeUnit.SECONDS));

            }, 5, 5, TimeUnit.SECONDS);
        }
    }

    private static class Client extends AbstractClient {
        private final ReconMetrics reconMetrics;

        public Client(ReconMetrics reconMetrics) {
            super("localhost", 3305);

            this.reconMetrics = reconMetrics;
        }

        @Override
        public void initChannel(ConnectionLogger logger, ChannelConfig channelConfig) {
            reconMetrics.initPipelines(channelConfig.pipeline());

            channelConfig.pipeline().putLast("connect-handler", new ChannelListenerAdapter() {

                @Override
                public void onConnected(ContextHandler contextHandler) {
                    ByteStream.Output output = BufferFactory.createPooledOutput();

                    output.writeBoolean(true);
                    output.writeLong(32_000_000);

                    contextHandler.channel().write(output);
                }
            });
        }
    }

}
