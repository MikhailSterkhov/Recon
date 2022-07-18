package org.itzstonlex.recon.http.client;

import java.nio.charset.Charset;

public final class HttpResponse {

    public static HttpResponse create(int contentLength, int statusCode, String callback) {
        return new HttpResponse(contentLength, statusCode, callback);
    }

    private final int contentLength;

    private final int statusCode;

    private final String body;

    private HttpResponse(int contentLength, int statusCode, String body) {
        this.contentLength = contentLength;
        this.statusCode = statusCode;

        this.body = body;
    }

    public int getContentLength() {
        return contentLength;
    }

    public int getStatusCode() {
        return statusCode;
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
