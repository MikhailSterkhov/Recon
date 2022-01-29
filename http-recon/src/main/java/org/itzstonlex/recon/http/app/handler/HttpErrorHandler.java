package org.itzstonlex.recon.http.app.handler;

public interface HttpErrorHandler {

    void handle(int errorCode, HttpResponseHandler response, HttpRequestHandler request);
}
