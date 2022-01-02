package org.itzstonlex.recon.util;

public final class NumberUtils {

    public static int onlyDecimal(double value) {
        String doubleAsString = String.valueOf(value);

        int indexOfDecimal = doubleAsString.indexOf(".");
        return Integer.parseInt(doubleAsString.substring(indexOfDecimal + 1));
    }

}
