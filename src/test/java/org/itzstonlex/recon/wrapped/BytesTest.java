package org.itzstonlex.recon.wrapped;

import org.itzstonlex.recon.util.PrimitiveByteUtils;

import java.util.Arrays;

public class BytesTest {

    public static void main(String[] args) {
        byte[] array;

        // Int
        int i = 126_567;

        System.out.println(Arrays.toString(array = PrimitiveByteUtils.toByteArray(i)));
        System.out.println(PrimitiveByteUtils.readInt(array));

        // Long
        long l = 0xfff;

        System.out.println(Arrays.toString(array = PrimitiveByteUtils.toByteArray(l)));
        System.out.println(PrimitiveByteUtils.readLong(array));
    }
}
