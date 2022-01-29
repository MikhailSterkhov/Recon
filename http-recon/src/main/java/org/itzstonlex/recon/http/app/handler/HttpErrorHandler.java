package org.itzstonlex.recon.http.app.handler;

public interface HttpErrorHandler {

    void handleError(int responseCode, HttpResponseHandler response, HttpRequestHandler request);
}
