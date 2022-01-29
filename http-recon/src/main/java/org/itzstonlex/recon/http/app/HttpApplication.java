package org.itzstonlex.recon.http.app;

import com.sun.net.httpserver.*;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.http.app.handler.HttpErrorHandler;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;
import org.itzstonlex.recon.http.app.util.HttpContentUtils;
import org.itzstonlex.recon.http.app.util.PathLevel;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.option.ChannelOption;
import org.itzstonlex.recon.util.ReconSimplify;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class HttpApplication implements RemoteConnection, HttpHandler {

    private static final String DEBUG_WRITE_200 = "[ReconHTTP] 200 (OK): Write content for \"%s\"";
    private static final String DEBUG_ERROR_404 = "[ReconHTTP] 404 (Not Found): Content for \"%s\" is`nt found";
    private static final String DEBUG_ERROR_403 = "[ReconHTTP] 403 (Forbidden): Failed to authenticate";

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    // Flushing & closing HTTP Exchange streams.
    private static final BiConsumer<HttpResponseHandler, HttpExchange> onCloseHandler = (response, exchange) -> {

        try {
            exchange.getRequestBody().close();

            if (response.size() > 0) {
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    };

    private int backlog = 50;

    private boolean ssl;
    private boolean debugging;

    private HttpsConfigurator httpsConfigurator;
    private HttpErrorHandler httpErrorHandler;

    private final ReconLog logger = new ReconLog("HttpApplication");

    private final List<HttpContextHandler> contextsList = new ArrayList<>();
    private final Map<String, ByteStream.Input> attachmentLinksMap = new HashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconHttpApp-%s")
    );

    private HttpChannel channel;
    private final Set<ChannelOption> optionSet = new HashSet<>();

    public void setErrorHandler(HttpErrorHandler httpErrorHandler) {
        this.httpErrorHandler = httpErrorHandler;
    }

    private InetSocketAddress cachedAddress;
    public final InetSocketAddress getAddress() {
        return cachedAddress;
    }

    public final boolean isUsedSSL() {
        return ssl;
    }

    public final void setUseSSL(boolean ssl, HttpsConfigurator httpsConfigurator) {
        this.ssl = ssl;
        this.httpsConfigurator = httpsConfigurator;
    }

    public final int getBacklog() {
        return backlog;
    }

    public final void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public final boolean isDebugging() {
        return debugging;
    }

    public final void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public void addAttachmentLink(String linkPath, PathLevel pathLevel, String filePath) {
        printDebugInfo("[ReconHTTP] Attachment link \"%s\" is registered.", linkPath);
        InputStream inputStream = HttpContentUtils.getInputStream(HttpApplication.class, pathLevel, filePath);

        attachmentLinksMap.put(linkPath, ReconSimplify.BYTE_BUF.input(inputStream));
    }

    public final List<HttpContextHandler> getContextsList() {
        return contextsList;
    }

    public final void addContext(HttpContextHandler context) {
        contextsList.add(context);
    }

    public final HttpContextHandler getContext(String path) {
        for (HttpContextHandler context : getContextsList()) {

            if (context.getPath().equals(path)) {
                return context;
            }
        }

        return null;
    }

    public final void printDebug(Level level, String message, Object... replacement) {
        if (isDebugging()) {
            logger.log(level, String.format(message, replacement));
        }
    }

    public final void printDebugInfo(String message, Object... replacement) {
        this.printDebug(Level.INFO, message, replacement);
    }

    public final void printDebugError(String message, Object... replacement) {
        this.printDebug(Level.SEVERE, message, replacement);
    }

    public final void printDebugWarn(String message, Object... replacement) {
        this.printDebug(Level.WARNING, message, replacement);
    }

    private HttpServer initHttpServer(InetSocketAddress address)
    throws IOException {

        HttpServer httpServer = isUsedSSL() ? HttpsServer.create(address, backlog) : HttpServer.create(address, backlog);
        httpServer.setExecutor(executor);

        if (isUsedSSL() && httpsConfigurator != null) {
            ((HttpsServer) httpServer).setHttpsConfigurator(httpsConfigurator);
        }

        httpServer.createContext("/", this);

        contextsList.forEach(httpContextHandler -> {

            // Getting a context path.
            HttpContextPath contextPath = httpContextHandler.getClass().getAnnotation(HttpContextPath.class);
            if (contextPath == null) {

                printDebugError("[ReconHTTP] Context path for \"%s\" is`nt found", httpContextHandler.getClass());
                throw new HttpApplicationException("Context path for \"%s\" cannot be null", httpContextHandler.getClass());
            }

            // Initialize context instance.
            HttpContentUtils.initContextContent(contextPath, httpContextHandler);

            if (httpContextHandler.getContentBuffer() != null) {
                attachmentLinksMap.put(contextPath.contentPath(), httpContextHandler.getContentBuffer());
            }

            // Print debugs.
            printDebugInfo("[ReconHTTP] Context \"%s\" is registered.", contextPath.context());
        });

        return httpServer;
    }

    private HttpServer cachedHttpServer;
    public final HttpChannel bind(String host, int port) {
        InetSocketAddress address = cachedAddress = new InetSocketAddress(host, port);
        HttpChannel httpChannel = new HttpChannel(this);

        try {
            HttpServer httpServer = initHttpServer(address);
            httpServer.start();

            printDebugInfo("[ReconHTTP] Success started on [address=\"%s\"].", address);

            cachedHttpServer = httpServer;
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return this.channel = httpChannel;
    }

    public final HttpChannel bind(String host) {
        return bind(host, (ssl ? HTTP_PORT : HTTPS_PORT));
    }

    public final HttpChannel bindLocal(int port) {
        return bind("localhost", port);
    }

    public final HttpChannel bindLocal() {
        return bindLocal(ssl ? HTTP_PORT : HTTPS_PORT);
    }

    @Override
    public final ExecutorService getThread() {
        return executor;
    }

    @Override
    public final ReconLog logger() {
        return logger;
    }

    @Override
    public final RemoteChannel channel() {
        return channel;
    }

    @Override
    public ChannelOption[] options() {
        return optionSet.toArray(new ChannelOption[0]);
    }

    @Override
    public void setOption(ChannelOption channelOption) {
        optionSet.add(channelOption);
    }

    @Override
    public final void shutdown() {
        if (channel != null && !channel.isClosed()) {
            channel.close();
        }

        if (cachedHttpServer != null) {
            
            printDebugInfo("[ReconHTTP] Handle shutdown process");
            cachedHttpServer.stop(0);
        }
    }

    @Override
    protected void finalize() {
        shutdown();
    }

    @Override
    public final void handle(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();

        HttpRequestHandler httpRequestHandler = HttpRequestHandler.fromExchange(this, exchange);
        HttpResponseHandler httpResponseHandler = HttpResponseHandler.fromExchange(this, exchange);

        // Send HTTP Content Attachments.
        if (requestPath.contains(".")) {

            if (attachmentLinksMap.containsKey(requestPath)) {
                printDebugInfo(DEBUG_WRITE_200, requestPath);

                httpResponseHandler.write(attachmentLinksMap.get(requestPath).array());
                httpResponseHandler.sendResponse(HttpURLConnection.HTTP_OK);
            }
            else {
                printDebugWarn(DEBUG_ERROR_404, requestPath);

                if (httpErrorHandler != null) {
                    httpErrorHandler.handleError(HttpURLConnection.HTTP_NOT_FOUND, httpResponseHandler, httpRequestHandler);
                }
                else {
                    httpResponseHandler.sendResponseMessage(HttpURLConnection.HTTP_NOT_FOUND, String.format("Error 404: Content \"%s\" not found!", requestPath));
                }
            }

            onCloseHandler.accept(httpResponseHandler, exchange);
            return;
        }

        // Check HTTP Content Body nullable state.
        HttpContextHandler context = getContext(requestPath);
        if (context == null) {

            printDebugWarn(DEBUG_ERROR_404, requestPath);

            if (httpErrorHandler != null) {
                httpErrorHandler.handleError(HttpURLConnection.HTTP_NOT_FOUND, httpResponseHandler, httpRequestHandler);
            }
            else {
                httpResponseHandler.sendResponseMessage(HttpURLConnection.HTTP_NOT_FOUND, String.format("Error 404: Content \"%s\" not found!", requestPath));
            }

            onCloseHandler.accept(httpResponseHandler, exchange);
            return;
        }

        // Send HTTP Content Body
        if (context.getContentBuffer() != null) {
            printDebugInfo(DEBUG_WRITE_200, requestPath);

            httpResponseHandler.write(context.getContentBuffer().array());
            httpResponseHandler.sendResponse(HttpURLConnection.HTTP_OK);

            onCloseHandler.accept(httpResponseHandler, exchange);
            return;
        }

        // Handle Content Authentication.
        Authenticator authenticator = context.getAuthenticator();

        if (authenticator != null) {
            Authenticator.Result authenticationResult = authenticator.authenticate(exchange);

            printDebugInfo("[ReconHTTP] Authentication Result: \"%s\"", authenticationResult.getClass().getName().toUpperCase());
            context.handleAuthentication(authenticationResult);

            if (!(authenticationResult instanceof Authenticator.Success)) {
                printDebugWarn(DEBUG_ERROR_403);

                if (httpErrorHandler != null) {
                    httpErrorHandler.handleError(HttpURLConnection.HTTP_FORBIDDEN, httpResponseHandler, httpRequestHandler);
                }
                else {
                    httpResponseHandler.sendResponseMessage(HttpURLConnection.HTTP_FORBIDDEN, "Error 403: Forbidden");
                }

                onCloseHandler.accept(httpResponseHandler, exchange);
                return;
            }
        }

        // Handle context HTTP request/response exchanges.
        context.handleExchange(context.getPath(), httpRequestHandler, httpResponseHandler);
        onCloseHandler.accept(httpResponseHandler, exchange);
    }

}
