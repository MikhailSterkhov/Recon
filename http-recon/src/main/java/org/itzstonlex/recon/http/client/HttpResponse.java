package org.itzstonlex.recon.http.client;

import java.nio.charset.Charset;

public final class HttpResponse {

    public static HttpResponse create(int contentLength, int statusCode, String callback, Throwable error) {
        return new HttpResponse(contentLength, statusCode, callback, error);
    }

    private final int contentLength;

    private final int statusCode;

    private final String body;
    private final Throwable inprocessThrowable;

    private HttpResponse(int contentLength, int statusCode, String body, Throwable inprocessThrowable) {
        this.contentLength = contentLength;

        this.statusCode = statusCode;

        this.body = body;
        this.inprocessThrowable = inprocessThrowable;
    }

    public int getContentLength() {
        return contentLength;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Throwable getInprocessThrowable() {
        return inprocessThrowable;
    }

    public byte[] getBody() {
        return body.getBytes();
    }

    public byte[] getBody(Charset charset) {
        return body.getBytes(charset);
    }

    public String getBodyAsString() {
        return body;
    }

    public <T> T getBodyFromJson(Class<T> type) {
        return HttpClient.GSON.fromJson(body, type);
    }

}
