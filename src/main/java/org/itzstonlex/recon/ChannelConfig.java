package org.itzstonlex.recon;

import org.itzstonlex.recon.option.ChannelOption;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public interface ChannelConfig {

    void addClientReconnector(boolean hasDebug, long delay, TimeUnit unit);

    void option(ChannelOption option);

    ChannelPipeline pipeline();

    InetSocketAddress address();
}