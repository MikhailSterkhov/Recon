package org.itzstonlex.recon.side;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.side.server.Server;

public class ServerTest {

    public static void main(String[] args) {
        Server server = new Server();

        server.addListener(new ConnectionListener());
        server.bindLocal(1010);

        server.channel().write(createPacket());
    }

    public static ByteStream.Output createPacket() {
        ByteStream.Output output = BufferFactory.createPooledOutput();

        output.writeString("ItzStonlex");
        output.writeBoolean(true);

        return output;
    }

    public static class ConnectionListener extends ChannelListenerAdapter {

        @Override
        public void onActive(ContextHandler contextHandler) {
            System.out.println("[Server] Connection was success bind on "
                    + contextHandler.channel().address());
        }

        @Override
        public void onInactive(ContextHandler contextHandler) {
            System.out.println("[Server] Connection is closed!");
        }

        @Override
        public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
            super.onExceptionCaught(remoteChannel, throwable);
        }
    }

}
