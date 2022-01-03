package org.itzstonlex.recon.side;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.handler.PacketHandler;

public class ClientTest {

    public static void main(String[] args) {
        ClientTest clientTest = new ClientTest();
        clientTest.launchApplication(new Client());
    }

    public void launchApplication(Client client) {
        client.connectLocal(1010, config -> {

            config.pipeline().putLast("connection-listener", new ConnectionListener(client));
            config.pipeline().putAfter("connection-listener", "packet-handler", new PacketHandler());

            client.logger().info("[ChannelInitializer]: Init Channel " + config.address());
        });
    }


    public static class ConnectionListener extends ChannelListenerAdapter {

        public ConnectionListener(RemoteConnection connection) {
            super(connection);
        }

        @Override
        public void onActive(ContextHandler contextHandler) {
            connection.logger().info("[Client] Connection was success connected on "
                    + contextHandler.channel().address());
        }

        @Override
        public void onInactive(ContextHandler contextHandler) {
            connection.logger().info("[Client] Connection is closed!");
        }

        @Override
        public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer) {
            connection.logger().info("Bytes receiving:");
            connection.logger().info(" * String value: " + buffer.readString());
            connection.logger().info(" * Boolean value: " + buffer.readBoolean());
        }

        @Override
        public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
            connection.logger().severe(throwable.getMessage());

            super.onExceptionCaught(remoteChannel, throwable);
        }
    }

}
