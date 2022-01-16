package org.itzstonlex.recon.exception;

public class ReconThreadException extends ReconRuntimeException {

    public ReconThreadException(Throwable throwable) {
        super(throwable);
    }

    public ReconThreadException(String message) {
        super(message);
    }

    public ReconThreadException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
