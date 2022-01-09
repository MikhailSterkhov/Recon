package org.itzstonlex.recon.annotation;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.annotation.type.ConnectClient;

public class ConnectTests {

    @ConnectClient(port = 3305, timeout = 5000)
    private RemoteChannel channel;

    public static void main(String[] args) {
        ConnectTests connectTests = new ConnectTests();

        AnnotatedReconScanner.scanInstance(connectTests);
        System.out.println("Channel address: " + connectTests.channel.address());
    }

}
