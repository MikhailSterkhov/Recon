package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.app.*;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;
import org.itzstonlex.recon.http.app.util.PathLevel;

import java.net.HttpURLConnection;

public class HttpApplicationTest {

    public static void main(String[] args) {
        HttpApplication http = new HttpApplication();
        http.setDebugging(true);

        http.addContext(new IndexContext());
        http.addContext(new GreetingContext());

        http.addAttachmentLink("/greeting.css", PathLevel.CLASSPATH, "/greeting.css");

        http.bindLocal(8080);
    }

    @HttpContextPath
    private static class IndexContext extends HttpContextHandler {

        @Override
        public void handleExchange(String context, HttpRequestHandler request, HttpResponseHandler response) {

            // Response
            response.write("<title>HttpRecon</title>");

            response.write("<p>Recon Github: https://github.com/ItzStonlex/Recon</p>");
            response.write("<a href=\"/greeting\">Greeting</a>");

            response.sendResponse(HttpURLConnection.HTTP_OK);
        }
    }

    @HttpContextPath(context = "/greeting")
    public static class GreetingContext extends HttpContextHandler {

        @Override
        public void handleExchange(String context, HttpRequestHandler request, HttpResponseHandler response) {
            response.sendResponse(HttpURLConnection.HTTP_OK);
        }
    }

}
