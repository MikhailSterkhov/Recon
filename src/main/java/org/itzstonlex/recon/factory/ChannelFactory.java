package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.init.ChannelInitializer;

import java.net.InetSocketAddress;

public final class ChannelFactory {

    public static RemoteChannel createChannel(InetSocketAddress remoteAddress, RemoteConnection connection) {
        return new ChannelInitializer(connection, remoteAddress, connection.logger());
    }

}
