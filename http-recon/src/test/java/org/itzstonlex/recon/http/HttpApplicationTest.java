package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.app.*;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;

import java.net.HttpURLConnection;

public class HttpApplicationTest {

    public static void main(String[] args) {
        HttpApplication httpApplication = new HttpApplication();
        httpApplication.setDebugging(true);

        httpApplication.addContext(new IndexContext(httpApplication));
        httpApplication.addContext(new GreetingContext(httpApplication));

        httpApplication.bindLocal(8080);
    }

    @HttpContextPath("/")
    @HttpContextContent(filePath = "/index.html")
    private static class IndexContext extends HttpContextHandler {

        public IndexContext(HttpApplication httpApplication) {
            super(httpApplication);
        }

        @Override
        public void handleResponse(HttpResponseHandler httpResponseHandler) {
            httpResponseHandler.write("<p>Recon Github: https://github.com/ItzStonlex/Recon</p>");
            httpResponseHandler.write("<a href=\"/greeting\">Greeting</a>");

            httpResponseHandler.sendResponse(HttpURLConnection.HTTP_OK);
        }

        @Override
        public void handleRequest(HttpRequestHandler httpRequestHandler) {
            HttpApplication httpApplication = httpRequestHandler.getHttpApplication();
            httpApplication.logger().info("[Request] uri=" + httpRequestHandler.getURI() + ", boby=" + httpRequestHandler.getBody());
        }
    }

    @HttpContextPath("/greeting")
    @HttpContextContent(filePath = "/greeting.html")
    @HttpContextAttachment(filePath = "/greeting.css")
    public static class GreetingContext extends HttpContextHandler {

        public GreetingContext(HttpApplication httpApplication) {
            super(httpApplication);
        }

        @Override
        public void handleRequest(HttpRequestHandler httpRequestHandler) {
            HttpApplication httpApplication = httpRequestHandler.getHttpApplication();
            httpApplication.logger().info("[Request] uri=" + httpRequestHandler.getURI() + ", boby=" + httpRequestHandler.getBody());
        }

        @Override
        public void handleResponse(HttpResponseHandler httpResponseHandler) {
            httpResponseHandler.sendResponse(HttpURLConnection.HTTP_OK);
        }
    }

}
