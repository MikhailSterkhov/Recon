package org.itzstonlex.recon.simplify;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.util.ReconSimplify;

import java.util.Arrays;

public class SimplifyOutputBuilderTest {

    public static void main(String[] args) {
        ByteStream.Output buffer = ReconSimplify.newOutputBufBuilder()
                .writeString("ItzStonlex")
                .writeDouble(18d)
                .writeCollection(Arrays.asList("Java", "PHP", "Python", "Kotlin"), (s, output) -> output.writeString(s))
                .build();

        // Debug
        System.out.println( buffer.size() );
        System.out.println( Arrays.toString(buffer.array()) );
    }

}
