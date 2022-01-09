package org.itzstonlex.recon.annotation;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.annotation.type.BindServer;

public class BindTests {

    @BindServer(port = 3305)
    private RemoteChannel channel;

    public static void main(String[] args) {
        BindTests bindTests = new BindTests();

        AnnotatedReconScanner.scanInstance(bindTests);
        System.out.println("Channel address: " + bindTests.channel.address());
    }

}
