package org.itzstonlex.recon.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class PrimitiveByteUtils {

    private static final ByteBuffer SHORT_BUFFER = ByteBuffer.allocate(Short.BYTES);
    private static final ByteBuffer INT_BUFFER = ByteBuffer.allocate(Integer.BYTES);
    private static final ByteBuffer FLOAT_BUFFER = ByteBuffer.allocate(Float.BYTES);
    private static final ByteBuffer DOUBLE_BUFFER = ByteBuffer.allocate(Double.BYTES);
    private static final ByteBuffer LONG_BUFFER = ByteBuffer.allocate(Long.BYTES);
    private static final ByteBuffer CHAR_BUFFER = ByteBuffer.allocate(Character.BYTES);

    public static byte[] toByteArray(short value) {
        SHORT_BUFFER.putShort(value);

        byte[] array = SHORT_BUFFER.array();
        SHORT_BUFFER.clear();

        return array;
    }

    public static byte[] toByteArray(int value) {
        INT_BUFFER.putInt(value);

        byte[] array = INT_BUFFER.array();
        INT_BUFFER.clear();

        return array;
    }

    public static byte[] toByteArray(float value) {
        FLOAT_BUFFER.putFloat(value);

        byte[] array = FLOAT_BUFFER.array();
        FLOAT_BUFFER.clear();

        return array;
    }

    public static byte[] toByteArray(double value) {
        DOUBLE_BUFFER.putDouble(value);

        byte[] array = DOUBLE_BUFFER.array();
        DOUBLE_BUFFER.clear();

        return array;
    }

    public static byte[] toByteArray(long value) {
        LONG_BUFFER.putLong(value);

        byte[] array = LONG_BUFFER.array();
        LONG_BUFFER.clear();

        return array;
    }

    public static byte[] toByteArray(char value) {
        CHAR_BUFFER.putChar(value);

        byte[] array = CHAR_BUFFER.array();
        CHAR_BUFFER.clear();

        return array;
    }

    public static short readShort(byte[] array) {
        SHORT_BUFFER.put(array, 0, array.length);
        SHORT_BUFFER.flip();

        return SHORT_BUFFER.getShort();
    }

    public static int readInt(byte[] array) {
        INT_BUFFER.put(array, 0, array.length);
        INT_BUFFER.flip();

        return INT_BUFFER.getInt();
    }

    public static float readFloat(byte[] array) {
        FLOAT_BUFFER.put(array, 0, array.length);
        FLOAT_BUFFER.flip();

        return FLOAT_BUFFER.getFloat();
    }

    public static double readDouble(byte[] array) {
        DOUBLE_BUFFER.put(array, 0, array.length);
        DOUBLE_BUFFER.flip();

        return DOUBLE_BUFFER.getDouble();
    }

    public static long readLong(byte[] array) {
        LONG_BUFFER.put(array, 0, array.length);
        LONG_BUFFER.flip();

        return LONG_BUFFER.getLong();
    }

    public static char readChar(byte[] array) {
        CHAR_BUFFER.put(array, 0, array.length);
        CHAR_BUFFER.flip();

        return CHAR_BUFFER.getChar();
    }

    public static String readString(byte[] bytes, Charset charset) {
        return new String(bytes, 0, bytes.length, charset);
    }

    public static String readString(byte[] bytes) {
        return readString(bytes, Charset.defaultCharset());
    }

}
