package org.itzstonlex.recon.http.client;

public final class HttpResponse {

    public static HttpResponse create(int contentLength, int statusCode, String callback, Throwable error) {
        return new HttpResponse(contentLength, statusCode, callback, error);
    }

    private final int contentLength;

    private final int statusCode;

    private final String callback;
    private final Throwable error;

    private HttpResponse(int contentLength, int statusCode, String callback, Throwable error) {
        this.contentLength = contentLength;

        this.statusCode = statusCode;

        this.callback = callback;
        this.error = error;
    }

    public int getContentLength() {
        return contentLength;
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
