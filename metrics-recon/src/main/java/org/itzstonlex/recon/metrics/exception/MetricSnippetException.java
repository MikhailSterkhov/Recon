package org.itzstonlex.recon.metrics.exception;

import org.itzstonlex.recon.exception.ReconRuntimeException;

public class MetricSnippetException extends ReconRuntimeException {

    public MetricSnippetException(String message) {
        super(message);
    }

    public MetricSnippetException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
