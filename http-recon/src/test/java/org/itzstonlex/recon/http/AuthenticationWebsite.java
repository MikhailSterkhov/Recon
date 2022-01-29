package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.app.HttpApplication;
import org.itzstonlex.recon.http.app.HttpContextHandler;
import org.itzstonlex.recon.http.app.HttpContextPath;
import org.itzstonlex.recon.http.app.handler.HttpErrorHandler;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;
import org.itzstonlex.recon.http.app.util.PathLevel;

import java.net.HttpURLConnection;

public class AuthenticationWebsite {

    public static void main(String[] args) {
        HttpApplication http = new HttpApplication();
        http.setDebugging(true);

        // Add errors handler.
        http.setErrorHandler(new ErrorHandler());

        // Add main context.
        http.addContext(new MainContext());

        // Add attachments from <link>`s
        http.addAttachmentLink("/login.css", PathLevel.CLASSPATH, "/authentication/login.css");
        http.addAttachmentLink("/img/attachment.png", PathLevel.CLASSPATH, "/authentication/img/attachment.png");

        // Bind a http application.
        http.bindLocal(8080);
    }

    // HTTP Errors handler.
    public static class ErrorHandler implements HttpErrorHandler {

        @Override
        public void handleError(int responseCode, HttpResponseHandler response, HttpRequestHandler request) {
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {

                response.write(AuthenticationWebsite.class, PathLevel.CLASSPATH, "/authentication/404.html");
                response.sendResponse(HttpURLConnection.HTTP_OK);
            }
        }
    }

    /**
     * HTTP Context /auth path handler.
     *
     * @context     - HTTP main content of current context.
     *
     * @baseDir     - Base and main content directory.
     * @contentPath - Path to main content without base directory.
     */
    @HttpContextPath(context = "/auth", baseDir = "/authentication", contentPath = "/login.html")
    public static class MainContext extends HttpContextHandler {

        @Override
        public void handleExchange(String context, HttpRequestHandler request, HttpResponseHandler response) {
            response.sendResponse(HttpURLConnection.HTTP_OK);
        }
    }

}
