package org.itzstonlex.recon.util;

public final class StringRandom {

    private static final String REGEX_NUMERIC       = "0123456789";
    private static final String REGEX_ALPHABETIC    = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String REGEX_ALPHANUMERIC  = (REGEX_NUMERIC + REGEX_ALPHABETIC);

    public static String random(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) ((int) (Math.random() * Short.MAX_VALUE)));
        }

        return stringBuilder.toString();
    }

    public static String random(int length, String regex) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            stringBuilder.append(regex.charAt((int) (Math.random() * regex.length())));
        }

        return stringBuilder.toString();
    }

    public static String randomNumeric(int length) {
        return random(length, REGEX_NUMERIC);
    }

    public static String randomAlphabetic(int length) {
        return random(length, REGEX_ALPHABETIC);
    }

    public static String randomAlphanumeric(int length) {
        return random(length, REGEX_ALPHANUMERIC);
    }
}
