package org.itzstonlex.recon.http.app;

import com.sun.net.httpserver.Authenticator;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.http.app.handler.HttpRequestHandler;
import org.itzstonlex.recon.http.app.handler.HttpResponseHandler;
import org.itzstonlex.recon.util.ReconSimplify;

import java.io.IOException;
import java.io.InputStream;

public abstract class HttpContextHandler {

    // HTTP Context variables.
    private String path;

    private Authenticator authenticator;
    private ByteStream.Input contentBuffer;

    public String getPath() {
        return path;
    }

    public ByteStream.Input getContentBuffer() {
        return contentBuffer;
    }

    public final Authenticator getAuthenticator() {
        return authenticator;
    }

    public final void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public final void setContentStream(String path, InputStream content) {
        this.path = path;
        this.contentBuffer = content != null ? ReconSimplify.BYTE_BUF.input(content) : null;

        try {
            if (content != null) {
                content.close();
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void handleAuthentication(Authenticator.Result result) {
        // override me.
    }

    public void handleExchange(String path, HttpRequestHandler request, HttpResponseHandler response) {
        // override me.
    }

}
