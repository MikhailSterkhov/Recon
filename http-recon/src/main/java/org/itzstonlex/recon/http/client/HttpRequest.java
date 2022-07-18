package org.itzstonlex.recon.http.client;

import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.http.util.HttpParameters;
import org.itzstonlex.recon.http.util.HttpUtils;
import org.itzstonlex.recon.util.InputUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class HttpRequest {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(
            ReconThreadFactory.asBuilder().setName("HTTP-Worker #%s").setDaemon(true));

    public enum Type {
        GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH
    }

    private final HttpClient client;

    private final Type type;
    private String url;

    private final HttpParameters httpParameters;

    private final ByteArrayOutputStream content;

    HttpRequest(HttpClient client, Type type, String url) {
        this.client = client;
        this.type = type;
        this.url = url;

        this.httpParameters = HttpParameters.create(url);
        this.content = new ByteArrayOutputStream();
    }

    HttpRequest(HttpClient client, String url) {
        this(client, Type.GET, url);
    }

    public String getUrl() {
        return url;
    }

    public Type getType() {
        return type;
    }

    public OutputStream getContentOutput() {
        return content;
    }

    public byte[] getContentAsByteArray() {
        return content.toByteArray();
    }

    public HttpRequest appendProtocolToUrl() {
        url = HttpUtils.getProtocol(url) + "://" + HttpUtils.trimProtocol(url);
        return this;
    }

    public HttpRequest modifyUrlParams(Consumer<HttpParameters> parametersConsumer) {
        if (parametersConsumer != null) {
            parametersConsumer.accept(httpParameters);

            this.url = httpParameters.appendParameters();
        }

        return this;
    }

    public HttpRequest addParamToUrl(String key, Object value) {
        return modifyUrlParams(params -> params.addParameter(key, value));
    }

    public HttpRequest setContent(byte[] contentAsBytes) {
        try {
            content.write(contentAsBytes);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return this;
    }

    public HttpRequest setContent(String contentMessage, Charset charset) {
        return setContent(contentMessage.getBytes(charset));
    }

    public HttpRequest setContent(String contentMessage) {
        return setContent(contentMessage, StandardCharsets.UTF_8);
    }

    public HttpRequest setContentByFile(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            setContent(InputUtils.toByteArray(inputStream));
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return this;
    }

    public HttpRequest setContentJson(Object object) {
        return setContent(HttpClient.GSON.toJson(object));
    }

    private HttpResponse execute() {
        return client.execute(this);
    }

    public final CompletableFuture<HttpResponse> executeSync() {
        return CompletableFuture.completedFuture(execute());
    }

    public final CompletableFuture<HttpResponse> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, THREAD_POOL);
    }
}
