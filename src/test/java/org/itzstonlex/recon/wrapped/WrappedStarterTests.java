package org.itzstonlex.recon.wrapped;

public class WrappedStarterTests {

    public static void main(String[] args) {
        new WrappedServer().bind();
        new WrappedClient().connect();
    }
}
