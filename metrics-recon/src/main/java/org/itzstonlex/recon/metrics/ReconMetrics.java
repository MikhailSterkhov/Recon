package org.itzstonlex.recon.metrics;

import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.metrics.handler.PipelineMetricHandler;
import org.itzstonlex.recon.metrics.handler.type.ReadMetricHandler;
import org.itzstonlex.recon.metrics.handler.type.ServerConnectionsMetricHandler;
import org.itzstonlex.recon.metrics.handler.type.WriteMetricHandler;

import java.util.HashSet;
import java.util.Set;

public final class ReconMetrics {

// ================================================================================================================================================ //

    public static final MetricCounter TOTAL_CLIENTS         = new MetricCounter("total_clients", "Connected clients length");

    public static final MetricCounter TOTAL_BYTES           = new MetricCounter("total_bytes", "Bytes handled size");

    public static final MetricCounter TOTAL_READS           = new MetricCounter("total_read", "Size of read handles");
    public static final MetricCounter TOTAL_BYTES_READ      = new MetricCounter("total_bytes_read", "Size of bytes read handles");

    public static final MetricCounter TOTAL_WRITES          = new MetricCounter("total_write", "Size of write handles");
    public static final MetricCounter TOTAL_BYTES_WRITE     = new MetricCounter("total_bytes_write", "Size of bytes write handles");

// ================================================================================================================================================ //

    private static void registerDefaults(ReconMetrics reconMetrics) {
        reconMetrics.addPipelineHandler(new ReadMetricHandler());
        reconMetrics.addPipelineHandler(new WriteMetricHandler());

        reconMetrics.addPipelineHandler(new ServerConnectionsMetricHandler());
    }

    public static ReconMetrics newMetricInstance() {
        ReconMetrics reconMetrics = new ReconMetrics();
        registerDefaults(reconMetrics);

        return reconMetrics;
    }

// ================================================================================================================================================ //

    private final Set<PipelineMetricHandler> metricHandlers;

    private ReconMetrics() {
        this.metricHandlers = new HashSet<>();
    }

    public void addPipelineHandler(PipelineMetricHandler metricHandler) {
        metricHandlers.add(metricHandler);
    }

    public void initPipelines(ChannelPipeline channelPipeline) {
        assert channelPipeline != null;

        for (PipelineMetricHandler metricHandler : metricHandlers) {
            channelPipeline.putLast(metricHandler.getPipelineID(), metricHandler);
        }
    }

    public void initPipelines(RemoteChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        initPipelines(pipeline);
    }

}
