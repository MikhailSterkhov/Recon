package org.itzstonlex.recon.fastrecon;

import org.itzstonlex.recon.util.FastRecon;

import java.util.Arrays;

public class BufferTest {

    public static void main(String[] args) {
        byte[] bytes = FastRecon.Buffer.initBytes(buffer -> {

            buffer.writeBoolean(true);
            buffer.writeInt(512_000);
            buffer.writeString("github.com");
        });

        System.out.println( Arrays.toString(bytes) );
    }

}
