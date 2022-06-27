package org.itzstonlex.recon.http.util;

import org.itzstonlex.recon.http.client.HttpRequestConfig;

import java.net.InetSocketAddress;
import java.net.URI;

public final class HttpUtils {

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    public static final String REQUEST_GET      = "GET";
    public static final String REQUEST_HEAD     = "HEAD";
    public static final String REQUEST_POST     = "POST";
    public static final String REQUEST_PUT      = "PUT";
    public static final String REQUEST_DELETE   = "DELETE";
    public static final String REQUEST_CONNECT  = "CONNECT";
    public static final String REQUEST_OPTIONS  = "OPTIONS";
    public static final String REQUEST_TRACE    = "TRACE";
    public static final String REQUEST_PATCH    = "PATCH";

    public static HttpRequestConfig createRequestConfig(String method) {
        return new HttpRequestConfig(method);
    }

    public static String trimProtocol(String url) {
        url = url.trim().replace("https://", "")
                .replace("http://", "");

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    public static String getProtocol(String url) {
        URI uri = URI.create(url);
        String scheme = uri.getScheme();

        int port = scheme == null ? HTTP_PORT : uri.getPort();

        if (port < 0) {

            switch (uri.getScheme()) {
                case "http": {
                    port = HTTP_PORT;
                    break;
                }

                case "https": {
                    port = HTTPS_PORT;
                    break;
                }
            }
        }

        return (port == HTTP_PORT ? "http" : "https");
    }

    public static boolean hasSSL(String url) {
        return getProtocol(url).equalsIgnoreCase("https");
    }

    public static String naturallyAddress(int port, String url) {
        InetSocketAddress address = new InetSocketAddress(url, port);
        return address.getAddress().getHostAddress();
    }

    public static String naturallyAddress(String url) {
        return naturallyAddress(HTTP_PORT, url);
    }

    public static InetSocketAddress getInetAddress(boolean normalize, String url) {
        url = trimProtocol(url);

        int port = hasSSL(url) ? HTTPS_PORT : HTTP_PORT;
        String host = normalize ? naturallyAddress(port, url) : url;

        return new InetSocketAddress(host, port);
    }

    public static InetSocketAddress getInetAddress(String url) {
        return getInetAddress(false, url);
    }

}
