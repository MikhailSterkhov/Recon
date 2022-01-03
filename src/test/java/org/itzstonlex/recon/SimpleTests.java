package org.itzstonlex.recon;

import org.itzstonlex.recon.util.NumberUtils;
import org.itzstonlex.recon.util.TimeUtils;

public class SimpleTests {

    public static void main(String[] args) {
        System.out.println(NumberUtils.onlyDecimal(56.789342));
        System.out.println(TimeUtils.format(TimeUtils.SEQUENCE_TIME_PATTERN));
    }

}
