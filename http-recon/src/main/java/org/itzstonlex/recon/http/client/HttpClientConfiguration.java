package org.itzstonlex.recon.http.client;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpClientConfiguration {
    private final Map<String, String> headers = new ConcurrentHashMap<>();

    private int connectTimeout = 3000, readTimeout = 5000;

    HttpClientConfiguration() {
    }

    public HttpClientConfiguration setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpClientConfiguration setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpClientConfiguration setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }
    
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

}
