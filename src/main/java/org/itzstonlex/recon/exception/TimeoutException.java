package org.itzstonlex.recon.exception;

public class TimeoutException extends ReconRuntimeException {

    public TimeoutException(Throwable throwable) {
        super(throwable);
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
