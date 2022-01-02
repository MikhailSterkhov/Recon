package org.itzstonlex.recon;

import java.io.Closeable;
import java.net.InetSocketAddress;

public interface RemoteChannel extends Closeable {

    ChannelPipeline pipeline();

    InetSocketAddress address();

    InetSocketAddress localAddress();

    ByteStream.Output buffer();

    void write(ByteStream.Output buffer);

    void flush();

    boolean isClosed();

}
