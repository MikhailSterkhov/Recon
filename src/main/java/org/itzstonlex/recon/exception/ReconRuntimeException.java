package org.itzstonlex.recon.exception;

public class ReconRuntimeException extends RuntimeException {

    public ReconRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public ReconRuntimeException(String message) {
        super(message);
    }

    public ReconRuntimeException(String message, Object... replacements) {
        this(String.format(message, replacements));
    }
}
