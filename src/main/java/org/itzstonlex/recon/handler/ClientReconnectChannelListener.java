package org.itzstonlex.recon.handler;

import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.adapter.ChannelListenerAdapter;
import org.itzstonlex.recon.error.ReconRuntimeError;
import org.itzstonlex.recon.init.ClientThreadInitializer;
import org.itzstonlex.recon.side.Client;

import java.util.concurrent.*;

public final class ClientReconnectChannelListener
        extends ChannelListenerAdapter {

    public static final String PIPELINE_ID = ("@client-reconnect-channel-listener");

    public static class TaskStats {

        public final long delay;
        public final TimeUnit unit;

        public final boolean hasDebug;

        public TaskStats(long delay, TimeUnit unit, boolean hasDebug) {
            this.delay = delay;
            this.unit = unit;
            this.hasDebug = hasDebug;
        }
    }

    public final TaskStats reconnectInfo;

    private final ScheduledExecutorService reconnectTaskService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> reconnectTask;

    public ClientReconnectChannelListener(RemoteConnection connection,
                                          long delay, TimeUnit unit, boolean debug) {
        super(connection);
        this.reconnectInfo = new TaskStats(delay, unit, debug);

        if (!(connection instanceof RemoteConnection.Connector)) {
            throw new ReconRuntimeError("That connection is`nt instance by RemoteConnection.Connector");
        }
    }

    public boolean isThreadAlive() {
        return reconnectTask != null;
    }

    private void startReconnectTask() {
        if (isThreadAlive()) {
            return;
        }

        // Debug the reconnect handle.
        if (reconnectInfo.hasDebug) {
            connection.logger().info(String.format("[Reconnect] Failed to connect! Wait for %d seconds...",
                    reconnectInfo.unit.convert(reconnectInfo.delay, TimeUnit.SECONDS)));
        }

        // Schedule reconnection handler
        reconnectTask = reconnectTaskService.scheduleAtFixedRate(() -> {
            if (!connection.channel().isClosed()) {
                return;
            }

            connection.channel().forceOpen();

            // Start client connection task
            Client client = ((Client) connection);
            ClientThreadInitializer.Data clientData = new ClientThreadInitializer.Data (
                    connection.channel(), client.options(), client.timeout()
            );

            new ClientThreadInitializer(clientData).start();

            // Debug the reconnect handle.
            if (reconnectInfo.hasDebug) {
                connection.logger().info(String.format("[Reconnect] Try to reconnect to the server (%s)...", connection.channel().address()));
            }

        }, reconnectInfo.delay, reconnectInfo.delay, reconnectInfo.unit);
    }

    private void shutdownReconnectTask() {
        if (!isThreadAlive()) {
            return;
        }

        reconnectTask.cancel(true);
        reconnectTask = null;
    }

    @Override
    public void onConnected(ContextHandler contextHandler) {
        shutdownReconnectTask();
    }

    @Override
    public void onClosed(ContextHandler contextHandler) {
        startReconnectTask();
    }

    @Override
    public void onTimedOut(RemoteChannel channel, ContextHandler contextHandler) {
        startReconnectTask();
    }

    @Override
    public void onExceptionCaught(RemoteChannel remoteChannel, Throwable throwable) {

        if (!isThreadAlive()) {
            super.onExceptionCaught(remoteChannel, throwable);
        }
    }

}
