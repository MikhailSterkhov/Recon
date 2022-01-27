package org.itzstonlex.recon.http.app.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.itzstonlex.recon.http.app.HttpApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class HttpResponseHandler {

    public static HttpResponseHandler fromExchange(HttpApplication application, HttpExchange exchange) {
        return new HttpResponseHandler(application, exchange);
    }

    private final HttpApplication httpApplication;
    private final HttpExchange exchange;

    private final ByteArrayOutputStream responseBody;

    private HttpResponseHandler(HttpApplication application, HttpExchange exchange) {
        this.httpApplication = application;
        this.exchange = exchange;

        this.responseBody = new ByteArrayOutputStream();
    }

    public HttpApplication getHttpApplication() {
        return httpApplication;
    }

    public HttpExchange getExchange() {
        return exchange;
    }

    public int getStatusCode() {
        return exchange.getResponseCode();
    }

    public Headers getHeaders() {
        return exchange.getResponseHeaders();
    }

    public void reset() {
        responseBody.reset();
    }

    public void write(String text) {
        try {
            responseBody.write(text.getBytes());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendResponse(int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, responseBody.size());
            exchange.getResponseBody().write(responseBody.toByteArray());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
