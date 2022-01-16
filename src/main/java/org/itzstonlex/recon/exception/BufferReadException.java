package org.itzstonlex.recon.exception;

public class BufferReadException extends ReconRuntimeException {

    public BufferReadException(String message) {
        super(message);
    }

    public BufferReadException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
