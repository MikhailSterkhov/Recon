package org.itzstonlex.recon.option;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;

public class ChannelOption {

    Type type;
    Object value;

    public ChannelOption(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void apply(ServerSocket serverSocket) {
        type.apply(serverSocket, value);
    }

    public void apply(Socket socket) {
        type.apply(socket, value);
    }


    @SuppressWarnings("unchecked")
    public enum Type {

        SO_BROADCAST(StandardSocketOptions.SO_BROADCAST),
        SO_KEEPALIVE(StandardSocketOptions.SO_KEEPALIVE),
        SO_SNDBUF(StandardSocketOptions.SO_SNDBUF),
        SO_RCVBUF(StandardSocketOptions.SO_RCVBUF),
        SO_REUSEADDR(StandardSocketOptions.SO_REUSEADDR),
        SO_LINGER(StandardSocketOptions.SO_LINGER),
        IP_TOS(StandardSocketOptions.IP_TOS),
        IP_MULTICAST_IF(StandardSocketOptions.IP_MULTICAST_IF),
        IP_MULTICAST_TTL(StandardSocketOptions.IP_MULTICAST_TTL),
        IP_MULTICAST_LOOP(StandardSocketOptions.IP_MULTICAST_LOOP),
        TCP_NODELAY(StandardSocketOptions.TCP_NODELAY),
        ;

        private final SocketOption impl;

        <T> Type(SocketOption<T> impl) {
            this.impl = impl;
        }

        public void apply(ServerSocket serverSocket, Object value) {
            try {
                serverSocket.setOption(impl, value);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        public void apply(Socket socket, Object value) {
            try {
                socket.setOption(impl, value);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
