package org.itzstonlex.recon.simplify;

import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.util.ReconSimplify;

import java.util.ArrayList;
import java.util.Arrays;

public class SimplifyOutputBuilderTest {

    public static void main(String[] args) {
        ByteStream.Output output = ReconSimplify.newOutputBufBuilder()
                .writeString("ItzStonlex")
                .writeDouble(18d)
                .writeStringList(Arrays.asList("Java", "PHP", "Python", "Kotlin"))
                .build();

        // Debug
        System.out.println( output.size() );
        System.out.println( Arrays.toString(output.array()) );

        ByteStream.Input input = ReconSimplify.BYTE_BUF.convert(output);

        // Debug
        System.out.println("Name: " + input.readString());
        System.out.println("Age: " + input.readDouble());
        System.out.println("Languages: " + input.readStringList().toString());
    }

}
