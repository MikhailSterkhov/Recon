package org.itzstonlex.recon.http.app.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.http.app.HttpApplication;
import org.itzstonlex.recon.http.app.util.HttpContentUtils;
import org.itzstonlex.recon.http.app.util.PathLevel;
import org.itzstonlex.recon.util.InputUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

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

    public void addHeader(String key, String value) {
        exchange.getResponseHeaders().add(key, value);
    }

    public String getHeader(String key) {
        return exchange.getResponseHeaders().getFirst(key);
    }

    public List<String> getHeadersList(String key) {
        return exchange.getResponseHeaders().get(key);
    }

    public int size() {
        return responseBody.size();
    }

    public void reset() {
        responseBody.reset();
    }

    public void write(byte[] bytes) {
        try {
            responseBody.write(bytes);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void write(String text) {
        write(text.getBytes());
    }

    public void write(ByteStream.Input input) {
        write(input.array());
    }

    public void write(InputStream inputStream) {
        write( InputUtils.toByteArray(inputStream) );
    }

    public void write(URL contentResource) {
        try {
            write(contentResource.openStream());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void write(File content) {
        try {
            write(new FileInputStream(content));
        }
        catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void write(Path contentPath) {
        write(contentPath.toFile());
    }

    public void write(Class<?> classLoader, PathLevel pathLevel, String contentPath) {
        InputStream inputStream = HttpContentUtils.getInputStream(classLoader, pathLevel, contentPath);
        write( inputStream );
    }

    public void sendResponseMessage(int errorCode, String responseText) {
        write(responseText);
        sendResponse(errorCode);
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
