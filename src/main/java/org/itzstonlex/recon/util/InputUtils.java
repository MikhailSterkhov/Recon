package org.itzstonlex.recon.util;

import org.itzstonlex.recon.error.SocketThreadError;
import org.itzstonlex.recon.factory.ContextFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public final class InputUtils {

    public static boolean isEmpty(InputStream inputStream) {
        try {
            return inputStream.available() <= 0;
        }
        catch (Exception exception) {
            return true;
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        try {
            byte[] result = new byte[inputStream.available()];
            inputStream.read(result);

            return result;
        }
        catch (Exception exception) {
            return new byte[0];
        }
    }

    public static boolean isClosed(Socket socket) {
        try {
            if (!socket.getKeepAlive()) {

                socket.setKeepAlive(true);
                socket.getInputStream().read();
            }
        }
        catch (IOException exception) {
            return true;
        }

        return false;
    }

}
