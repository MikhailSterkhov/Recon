package org.itzstonlex.recon.error;

public class ConnectionThreadError extends ReconRuntimeError {

    public ConnectionThreadError(Throwable throwable) {
        super(throwable);
    }

    public ConnectionThreadError(String message) {
        super(message);
    }

    public ConnectionThreadError(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
