package org.itzstonlex.recon.http.app;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;
import org.itzstonlex.recon.util.ReconSimplify;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpContextHandler implements HttpHandler {

    public static final String ERROR_403_FORBIDDEN      = ("Error 404 (Forbidden)");
    public static final String ERROR_404_NOT_FILE_FOUND = ("Error 404 (File '%s' not found)");

    private final HttpApplication application;

    private Authenticator authenticator;
    private ByteStream.Input contentStream;

    private final Map<String, ByteStream.Input> attachmentsStreamsMap;

    public HttpContextHandler(HttpApplication application) {
        this.application = application;
        this.attachmentsStreamsMap = new HashMap<>();
    }

    public final void addAttachmentStream(String filePath, InputStream stylesheetStream) {
        attachmentsStreamsMap.put(filePath, ReconSimplify.BYTE_BUF.input(stylesheetStream));
    }

    public final Authenticator getAuthenticator() {
        return authenticator;
    }

    public final void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public final HttpApplication getApplication() {
        return application;
    }

    public final void setContentStream(InputStream contentStream) {
        this.contentStream = ReconSimplify.BYTE_BUF.input(contentStream);
    }

    public void handleAuthentication(Authenticator.Result result) {
        // override me.
    }

    public void handleRequest(HttpRequestHandler httpRequestHandler) {
        // override me
    }

    public void handleResponse(HttpResponseHandler httpResponseHandler) {
        // override me
    }

    private byte[] initContentExchange(HttpExchange exchange) {
        String requestAttachmentPath = exchange.getRequestURI().getPath();

        if (requestAttachmentPath != null && requestAttachmentPath.contains(".")) {
            return getAttachmentBytes(requestAttachmentPath);
        }

        return contentStream != null ? contentStream.array() : new byte[0];
    }

    private byte[] getAttachmentBytes(String filePath) {
        ByteStream.Input buffer = attachmentsStreamsMap.get(filePath);

        if (buffer == null) {
            return null;
        }

        return buffer.array();
    }

    @Override
    public final void handle(HttpExchange exchange)
    throws IOException, NullPointerException {

        // Send content
        String requestPath = exchange.getRequestURI().getPath();
        byte[] responseBytes = initContentExchange(exchange);

        if (responseBytes == null) {
            String response = String.format(ERROR_404_NOT_FILE_FOUND, requestPath);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
            exchange.getResponseBody().write(response.getBytes());
        }
        else {

            // Handle authentication.
            if (authenticator != null) {

                Authenticator.Result authenticationResult = authenticator.authenticate(exchange);
                handleAuthentication(authenticationResult);

                if (!(authenticationResult instanceof Authenticator.Success)) {

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, ERROR_403_FORBIDDEN.length());
                    exchange.getResponseBody().write(ERROR_403_FORBIDDEN.getBytes());

                    return;
                }
            }

            // Handle http response exchange.
            handleResponse(HttpResponseHandler.fromExchange(application, exchange));
            exchange.getResponseBody().write(responseBytes);

            // Handle http request exchange.
            if (!requestPath.contains(".")) {
                handleRequest(HttpRequestHandler.fromExchange(application, exchange));
            }
        }

        // Flush & closing exchange streams.
        exchange.getRequestBody().close();

        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }

}
