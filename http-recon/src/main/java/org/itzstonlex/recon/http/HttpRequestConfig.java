package org.itzstonlex.recon.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestConfig {

    private final String method;
    private final Map<String, String> requestProperty;

    private int connectTimeout = 5000,
            readTimeout = 5000;

    public HttpRequestConfig(String method) {
        this.method = method;
        this.requestProperty = new ConcurrentHashMap<>();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setRequestProperty(String key, String value) {
        requestProperty.put(key, value);
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getProperties() {
        return requestProperty;
    }

}
