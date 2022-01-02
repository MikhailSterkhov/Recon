package org.itzstonlex.recon;

public interface ContextHandler {

    boolean isSuccess();

    Throwable error();

    RemoteChannel channel();
}
