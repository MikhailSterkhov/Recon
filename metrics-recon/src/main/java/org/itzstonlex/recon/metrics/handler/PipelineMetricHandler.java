package org.itzstonlex.recon.metrics.handler;

import org.itzstonlex.recon.adapter.ChannelListenerAdapter;

public abstract class PipelineMetricHandler extends ChannelListenerAdapter {

    private final String pipelineID;

    public PipelineMetricHandler(String pipelineID) {
        this.pipelineID = pipelineID;
    }

    public String getPipelineID() {
        return pipelineID;
    }

}
