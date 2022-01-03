package org.itzstonlex.recon.error;

public class SocketSetOptionError extends ReconRuntimeError {

    public SocketSetOptionError(Throwable throwable) {
        super(throwable);
    }

    public SocketSetOptionError(String message) {
        super(message);
    }

    public SocketSetOptionError(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
