package org.itzstonlex.recon.side;

import org.itzstonlex.recon.*;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.handler.PacketHandler;

public class ServerTest {

    public static void main(String[] args) {
        ServerTest serverTest = new ServerTest();
        serverTest.launchApplication(new Server());
    }

    public void launchApplication(Server server) {
        server.bindLocal(1010, config -> {

            ChannelPipeline channelPipeline = config.pipeline();

            channelPipeline.addLast("connection-listener", new ConnectionListener());
            channelPipeline.addAfter("connection-listener", "packet-handler", new PacketHandler());

            server.logger().info("[ChannelInitializer]: Init Channel " + config.address());
        });
    }

    public static class ConnectionListener extends ChannelListenerAdapter {

        @Override
        public void onThreadActive(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("[Server] Connection was success bind on "
                    + contextHandler.channel().address());
        }

        @Override
        public void onClosed(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("[Server] Connection is closed!");
        }

        @Override
        public void onClientConnected(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("New client connection: " + channel.address());

            // Write a test bytes.
            ByteStream.Output output = BufferFactory.createPooledOutput();

            output.writeString("ItzStonlex");
            output.writeBoolean(true);

            channel.write(output);
        }

        @Override
        public void onClientClosed(RemoteChannel channel, ContextHandler contextHandler) {
            channel.logger().info("Client connection " + channel.address() + " was disconnected");
        }

        @Override
        public void onExceptionCaught(RemoteChannel channel, Throwable throwable) {
            channel.logger().severe(throwable.getMessage());

            throwable.printStackTrace();
        }
    }

}
