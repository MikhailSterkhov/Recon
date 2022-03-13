package org.itzstonlex.recon.http.app;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.ChannelPipeline;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.init.PipelineInitializer;
import org.itzstonlex.recon.log.ReconLog;

import java.net.InetSocketAddress;

public class HttpChannel implements RemoteChannel {

    private boolean closed;

    private final HttpApplication httpApplication;
    private final ChannelPipeline pipeline;

    private ByteStream.Output buffer;

    public HttpChannel(HttpApplication httpApplication) {
        this.httpApplication = httpApplication;
        this.pipeline = new PipelineInitializer(this);
    }

    @Override
    public RemoteConnection connection() {
        return httpApplication;
    }

    @Override
    public ReconLog logger() {
        return httpApplication.logger();
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public InetSocketAddress address() {
        return httpApplication.getAddress();
    }

    @Override
    public ByteStream.Output buffer() {
        return buffer;
    }

    @Override
    public void write(ByteStream.Output buffer) {
        this.buffer = buffer;
    }

    @Override
    public void resetBuf() {
        write(null);
    }

    @Override
    public void forceOpen() {
        this.closed = false;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        this.closed = true;
    }

}
