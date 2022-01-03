package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.init.ChannelInitializer;
import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;

import java.net.InetSocketAddress;

public final class ChannelFactory {

    public static RemoteChannel createServerChannel(InetSocketAddress address, Server server) {
        return new ChannelInitializer(server, address, server.logger());
    }

    public static RemoteChannel createClientChannel(InetSocketAddress address, Client client) {
        return new ChannelInitializer(client, address, client.logger());
    }

}
