package org.itzstonlex.recon.metrics.handler.type;

import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.metrics.handler.PipelineMetricHandler;

public final class ServerConnectionsMetricHandler extends PipelineMetricHandler {

    public ServerConnectionsMetricHandler() {
        super("serverConnections_metric_handler");
    }

    @Override
    public void onClientConnected(RemoteChannel remoteChannel, ContextHandler contextHandler) {
        ReconMetrics.TOTAL_CLIENTS.increment();
    }

    @Override
    public void onClientClosed(RemoteChannel remoteChannel, ContextHandler contextHandler) {
        ReconMetrics.TOTAL_CLIENTS.decrement();
    }
}
