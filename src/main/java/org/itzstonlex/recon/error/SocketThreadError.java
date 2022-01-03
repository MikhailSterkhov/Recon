package org.itzstonlex.recon.error;

public class SocketThreadError extends ReconRuntimeError {

    public SocketThreadError(Throwable throwable) {
        super(throwable);
    }

    public SocketThreadError(String message) {
        super(message);
    }

    public SocketThreadError(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
