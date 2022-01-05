package org.itzstonlex.recon.fastrecon;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.handler.IncomingByteHandler;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.util.FastRecon;

import java.util.Arrays;

public class ConnectionBuilderTest {

    public static void main(String[] args) {
        Client client = FastRecon.newLocalConnection(1000)
                .client_setTimeout(5000)

                .pipeline_addLast("read-handler", new ReadHandler())
                .pipeline_addLast("connection-handler", new ConnectionListener())

                .asClient();
    }

    public static class ConnectionListener extends ChannelListenerAdapter {

        public ConnectionListener() {
            super(null);
        }

        @Override
        public void onThreadActive(ContextHandler contextHandler) {
            contextHandler.channel().logger().info("Server was success bind on " + contextHandler.channel().address());
        }
    }

    public static class ReadHandler extends IncomingByteHandler {

        @Override
        public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {
            super.onExceptionCaught(remoteChannel, throwable);
        }

        @Override
        public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer) {
            byte[] bytes = BufferFactory.transformOutput(buffer).array();

            remoteChannel.logger().info("Bytes read: " + Arrays.toString(bytes));
            buffer.flush();
        }
    }

}
