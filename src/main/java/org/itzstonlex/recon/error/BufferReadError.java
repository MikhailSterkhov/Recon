package org.itzstonlex.recon.error;

public class BufferReadError extends ReconRuntimeError {

    public BufferReadError(String message) {
        super(message);
    }

    public BufferReadError(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
