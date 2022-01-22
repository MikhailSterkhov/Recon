package org.itzstonlex.recon.http.util;

import org.itzstonlex.recon.http.client.HttpRequestConfig;

import java.net.InetSocketAddress;
import java.net.URI;

public final class HttpUtils {

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

    public static String trim(String url) {
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

        int port = scheme == null ? 80 : uri.getPort();

        if (port < 0) {

            switch (uri.getScheme()) {
                case "http": {
                    port = 80;
                    break;
                }

                case "https": {
                    port = 443;
                    break;
                }
            }
        }

        return port == 80 ? "http" : "https";
    }

    public static boolean hasSSL(String url) {
        return getProtocol(url).equalsIgnoreCase("https");
    }

    public static String naturallyAddress(int port, String url) {
        InetSocketAddress address = new InetSocketAddress(url, port);
        return address.getAddress().getHostAddress();
    }

    public static InetSocketAddress getInetAddress(boolean normalize, String url) {
        url = trim(url);

        int port = hasSSL(url) ? 443 : 80;
        String host = normalize ? naturallyAddress(port, url) : url;

        return new InetSocketAddress(host, port);
    }

    public static InetSocketAddress getInetAddress(String url) {
        return getInetAddress(false, url);
    }

}
