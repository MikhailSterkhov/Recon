package org.itzstonlex.recon.side;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.handler.PacketHandler;
import org.itzstonlex.recon.util.reconnect.ChannelReconnectListener;
import org.itzstonlex.recon.util.reconnect.ClientReconnectionUtils;

import java.util.concurrent.TimeUnit;

public class ClientTest {

    public static void main(String[] args) {
        ClientTest clientTest = new ClientTest();
        clientTest.launchApplication(new Client());
    }

    public void launchApplication(Client client) {
        client.connectLocal(1010, config -> {

            // Init channel-reconnector.
            ClientReconnectionUtils.setDebug(true);
            ClientReconnectionUtils.addReconnector(config.pipeline(), 5, TimeUnit.SECONDS);

            // Init other pipeline listeners.
            config.pipeline().addLast("connection-listener", new ConnectionListener());
            config.pipeline().addAfter("connection-listener", "packet-handler", new PacketHandler());

            client.logger().info("[ChannelInitializer]: Init Channel " + config.address());
        });
    }


    public static class ConnectionListener extends ChannelListenerAdapter {

        @Override
        public void onThreadActive(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("[Client] Connecting... ");
        }

        @Override
        public void onConnected(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("[Client] Connection was success connected on "
                    + contextHandler.channel().address());
        }

        @Override
        public void onClosed(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("[Client] Connection is closed!");
        }

        @Override
        public void onRead(RemoteChannel channel, ContextHandler contextHandler, ByteStream.Input buffer) {
            channel.logger().info("Bytes receiving:");
            channel.logger().info(" * String value: " + buffer.readString());
            channel.logger().info(" * Boolean value: " + buffer.readBoolean());
        }

        @Override
        public void onExceptionCaught(RemoteChannel channel, Throwable throwable) {

            // Check reconnect status.
            ChannelReconnectListener reconnectListener = channel.pipeline().get(ChannelReconnectListener.class);
            if (reconnectListener != null && reconnectListener.isThreadAlive()) {
                return;
            }

            // throw exceptions.
            channel.logger().severe(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

}
