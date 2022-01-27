package org.itzstonlex.recon.http.app;

public class HttpApplicationException extends RuntimeException {

    public HttpApplicationException() {
        super();
    }

    public HttpApplicationException(String message) {
        super(message);
    }

    public HttpApplicationException(String message, Object... replacement) {
        super(String.format(message, replacement));
    }

}
