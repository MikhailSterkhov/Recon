package org.itzstonlex.recon.exception;

public class SocketSetOptionException extends ReconRuntimeException {

    public SocketSetOptionException(Throwable throwable) {
        super(throwable);
    }

    public SocketSetOptionException(String message) {
        super(message);
    }

    public SocketSetOptionException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
