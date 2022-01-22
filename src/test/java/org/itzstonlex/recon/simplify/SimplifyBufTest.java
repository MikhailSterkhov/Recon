package org.itzstonlex.recon.simplify;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.util.ReconSimplify;

import java.util.Arrays;

public class SimplifyBufTest {

    public static void main(String[] args) {
        // Output
        ByteStream.Output output = ReconSimplify.BYTE_BUF.output();

        output.writeByte((byte) 32);
        output.writeLong(14_324_667);

        // Input
        ByteStream.Input input = ReconSimplify.BYTE_BUF.convert(output);

        // Debug
        System.out.println( input.size() );
        System.out.println( Arrays.toString(input.array()) );
    }
}
