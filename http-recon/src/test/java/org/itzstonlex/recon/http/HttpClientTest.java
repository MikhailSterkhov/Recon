package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.client.HttpClient;
import org.itzstonlex.recon.http.client.HttpRequest;

public class HttpClientTest {

    public static final String URL = "google.com";

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        httpClient.config().setHeader("User-Agent", "HttpRecon");

        httpClient.newRequest(HttpRequest.Type.GET, URL)
                .appendProtocolToUrl() // append "http://" or "https://" in start

                .executeAsync()
                .whenComplete((response, error) -> {

                    Throwable inprocessError = response.getInprocessThrowable();

                    System.out.println("content-length: " + response.getContentLength());
                    System.out.println("status-code: " + response.getStatusCode());

                    if (inprocessError != null) {
                        inprocessError.printStackTrace();
                        return;
                    }

                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    // OK Response handle.
                    System.out.println("callback: " + response.getBodyAsString());
                })
                .join();
    }

}
