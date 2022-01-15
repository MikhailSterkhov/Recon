package org.itzstonlex.recon.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class HttpParameters {

    public static HttpParameters create(String baseURL) {
        return new HttpParameters(baseURL);
    }

    private final String baseURL;
    private final Map<String, String> parameters;

    private HttpParameters(String baseURL) {
        this.baseURL = baseURL;
        this.parameters = new HashMap<>();
    }

    public HttpParameters addParameter(String key, Object value) {
        parameters.put(key, value.toString());
        return this;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
            return value;
        }
    }

    public String appendParameters() {
        return (baseURL + (baseURL.contains("?") ? "" : "?")) + parameters.entrySet()
                .stream()

                .map(entry -> entry.getKey() + "=" + (entry.getValue() != null ? encode(entry.getValue()) : "null"))
                .collect(Collectors.joining("&"));
    }

}
