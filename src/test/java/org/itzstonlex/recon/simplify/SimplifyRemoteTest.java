package org.itzstonlex.recon.simplify;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.util.ReconSimplify;
import org.itzstonlex.recon.util.ReconThreadsStorage;

public class SimplifyRemoteTest {

    public static void main(String[] args) {
        int startPort = 12_000;

        bindLocal(startPort, 10);
        connectLocal(startPort, 8);

        System.out.println(ReconThreadsStorage.getAllThreads().size());
    }

    public static void bindLocal(int startPort, int count) {

        for (int appender = 0; appender < count; appender++) {

            RemoteChannel channel = ReconSimplify.REMOTE.bindLocal(startPort + appender);
            System.out.println("[Server] New Channel: " + channel.address());
        }
    }

    public static void connectLocal(int startPort, int count) {

        for (int appender = 0; appender < count; appender++) {

            RemoteChannel channel = ReconSimplify.REMOTE.connectLocal(startPort + appender);
            System.out.println("[Client] New Channel: " + channel.address());
        }
    }
}
