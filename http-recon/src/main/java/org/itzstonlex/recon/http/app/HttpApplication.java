package org.itzstonlex.recon.http.app;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.RemoteConnection;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.http.app.util.ContextInitUtils;
import org.itzstonlex.recon.http.app.util.PathLevel;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.option.ChannelOption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class HttpApplication implements RemoteConnection {

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    private int backlog = 50;

    private boolean ssl;
    private boolean debugging;

    private HttpsConfigurator httpsConfigurator;
    private final ReconLog logger = new ReconLog("HttpApplication");

    private final List<HttpContextHandler> contextPathsMap
            = new ArrayList<>();

    private final ExecutorService executor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconHttpApp-%s")
    );

    private HttpChannel channel;
    private final Set<ChannelOption> optionSet = new HashSet<>();

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

    public final void addContext(HttpContextHandler context) {
        contextPathsMap.add(context);
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

    private String getContextPath(HttpContextHandler httpContextHandler) {
        HttpContextPath contextPath = httpContextHandler.getClass().getAnnotation(HttpContextPath.class);

        if (contextPath == null) {
            return null;
        }

        return contextPath.value();
    }

    private HttpServer initHttpServer(InetSocketAddress address)
    throws IOException {

        HttpServer httpServer = isUsedSSL() ? HttpsServer.create(address, backlog) : HttpServer.create(address, backlog);
        httpServer.setExecutor(executor);

        if (isUsedSSL() && httpsConfigurator != null) {
            ((HttpsServer) httpServer).setHttpsConfigurator(httpsConfigurator);
        }

        contextPathsMap.forEach(httpContextHandler -> {

            // Getting a context path.
            String contextPath = getContextPath(httpContextHandler);
            if (contextPath == null) {

                printDebugError("[HttpContext] Context path for '%s' is`nt found", httpContextHandler.getClass());
                throw new HttpApplicationException("Context path for '%s' cannot be null", httpContextHandler.getClass());
            }

            // Initialize context instance.
            ContextInitUtils.initContextInstance(httpContextHandler);

            // Registering the context.
            HttpContext httpContext = httpServer.createContext(contextPath, httpContextHandler);
            httpContext.setAuthenticator(httpContextHandler.getAuthenticator());

            printDebugInfo("[HttpContext] Context path='%s' is registered.", contextPath);
        });

        return httpServer;
    }

    private HttpServer cachedHttpServer;
    public final HttpChannel bind(String host, int port) {
        InetSocketAddress address = cachedAddress = new InetSocketAddress(host, port);
        HttpChannel httpChannel = new HttpChannel(this);

        try {
            printDebugInfo("[HttpServer] Start init process...");

            HttpServer httpServer = initHttpServer(address);
            httpServer.start();

            printDebugInfo("[HttpServer] Success started on [address=%s].", address);

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
            cachedHttpServer.stop(0);
        }
    }
}
