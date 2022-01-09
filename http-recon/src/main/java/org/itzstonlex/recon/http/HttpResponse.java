package org.itzstonlex.recon.http;

public final class HttpResponse {

    public static HttpResponse create(int statusCode, String callback, Throwable error) {
        return new HttpResponse(statusCode, callback, error);
    }

    private final int statusCode;

    private final String callback;
    private final Throwable error;

    private HttpResponse(int statusCode, String callback, Throwable error) {
        this.statusCode = statusCode;

        this.callback = callback;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCallback() {
        return callback;
    }

    public Throwable getError() {
        return error;
    }

}
