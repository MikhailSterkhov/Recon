package org.itzstonlex.recon.http.app.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.http.app.HttpApplication;
import org.itzstonlex.recon.util.ReconSimplify;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public final class HttpRequestHandler {

    public static HttpRequestHandler fromExchange(HttpApplication application, HttpExchange exchange) {
        return new HttpRequestHandler(application, exchange);
    }

    private final HttpApplication httpApplication;
    private final HttpExchange exchange;

    private final URI uri;

    private final Headers headers;

    private final String body;
    private final String method;

    private HttpRequestHandler(HttpApplication application, HttpExchange exchange) {
        this.httpApplication = application;
        this.exchange = exchange;

        ByteStream.Input buffer = ReconSimplify.BYTE_BUF.input(exchange.getRequestBody());
        String requestBody = new String(buffer.array(), 0, buffer.size(), StandardCharsets.UTF_8);

        this.uri = exchange.getRequestURI();
        this.headers = exchange.getRequestHeaders();
        this.body = requestBody;
        this.method = exchange.getRequestMethod();
    }

    public HttpApplication getHttpApplication() {
        return httpApplication;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public URI getURI() {
        return uri;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return method;
    }

}
