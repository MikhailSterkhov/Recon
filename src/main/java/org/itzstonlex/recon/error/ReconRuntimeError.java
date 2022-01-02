package org.itzstonlex.recon.error;

public class ReconRuntimeError extends RuntimeException {

    public ReconRuntimeError(Throwable throwable) {
        super(throwable);
    }

    public ReconRuntimeError(String message) {
        super(message);
    }

    public ReconRuntimeError(String message, Object... replacements) {
        this(String.format(message, replacements));
    }
}
