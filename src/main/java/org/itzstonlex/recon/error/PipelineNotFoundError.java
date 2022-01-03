package org.itzstonlex.recon.error;

public class PipelineNotFoundError extends ReconRuntimeError {

    public PipelineNotFoundError(Throwable throwable) {
        super(throwable);
    }

    public PipelineNotFoundError(String message) {
        super(message);
    }

    public PipelineNotFoundError(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
