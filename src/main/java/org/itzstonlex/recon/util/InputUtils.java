package org.itzstonlex.recon.util;

import java.io.InputStream;

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

}
