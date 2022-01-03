package org.itzstonlex.recon;

import org.itzstonlex.recon.option.ChannelOption;

import java.net.InetSocketAddress;

public interface ChannelConfig {

    void option(ChannelOption option);

    ChannelPipeline pipeline();

    InetSocketAddress address();
}