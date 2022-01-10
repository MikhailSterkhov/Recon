package org.itzstonlex.recon.metrics.handler.type;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ContextHandler;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.metrics.handler.PipelineMetricHandler;

public final class WriteMetricHandler extends PipelineMetricHandler {

    public WriteMetricHandler() {
        super("write_metric_handler");
    }

    @Override
    public void onRead(RemoteChannel remoteChannel, ContextHandler contextHandler, ByteStream.Input buffer) {
        ReconMetrics.TOTAL_WRITES.increment();

        ReconMetrics.TOTAL_BYTES.add(buffer.size());
        ReconMetrics.TOTAL_BYTES_WRITE.add(buffer.size());
    }

}
