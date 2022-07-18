package org.itzstonlex.recon.http.client;

import com.google.gson.Gson;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.util.InputUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class HttpClient {

    static final Gson GSON = new Gson();

    private final HttpClientConfiguration config = new HttpClientConfiguration();

    private final ReconLog logger = new ReconLog("HttpClient");

    public ReconLog logger() {
        return logger;
    }

    public HttpClientConfiguration config() {
        return config;
    }

    public HttpRequest newRequest(String url) {
        return new HttpRequest(this, url);
    }

    public HttpRequest newRequest(HttpRequest.Type type, String url) {
        return new HttpRequest(this, type, url);
    }

    HttpResponse execute(HttpRequest request) {
        if (request == null) {
            throw new NullPointerException("requestConfig");
        }

        String url;
        if ((url = request.getUrl()) == null) {
            throw new NullPointerException("url");
        }

        try {
            HttpURLConnection http = ((HttpURLConnection) new URL(url).openConnection());

            http.setConnectTimeout(config.getConnectTimeout());
            http.setReadTimeout(config.getReadTimeout());
            http.setRequestMethod(request.getType().toString());

            config.getHeaders().forEach(http::setRequestProperty);

            if (request.getContentAsByteArray().length > 0) {
                http.setDoOutput(true);

                try (OutputStream outputStream = http.getOutputStream()) {

                    outputStream.write(request.getContentAsByteArray());
                    outputStream.flush();
                }
            }

            // Response handle.
            http.setDoInput(true);

            try (InputStream inputStream = http.getInputStream()) {

                byte[] callbackArray = InputUtils.toByteArray(inputStream);
                String callback = new String(callbackArray, 0, callbackArray.length, StandardCharsets.UTF_8);

                HttpResponse httpResponse = HttpResponse.create(http.getContentLength(), http.getResponseCode(), callback, null);

                // Shutdown http connection.
                http.disconnect();
                return httpResponse;
            }
        }
        catch (Exception exception) {
            return HttpResponse.create(0, HttpURLConnection.HTTP_BAD_REQUEST, null, exception);
        }
    }

}
