package org.itzstonlex.recon.exception;

public class PipelineNotFoundException extends ReconRuntimeException {

    public PipelineNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public PipelineNotFoundException(String message) {
        super(message);
    }

    public PipelineNotFoundException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
