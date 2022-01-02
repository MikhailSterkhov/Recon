package org.itzstonlex.recon;

import org.itzstonlex.recon.factory.BufferFactory;

public class BufferTest {

    public static void main(String[] args) {
        ByteStream.Output buffer = BufferFactory.createPooledOutput();

        write( buffer );
        read( BufferFactory.createPooledInput(buffer.toByteArray()) );
    }

    public static void write(ByteStream.Output buffer) {
        buffer.writeBoolean(true);
        buffer.writeInt(2022);
        buffer.writeDouble(13.502);
        buffer.writeString("ItzStonlex");
    }

    public static void read(ByteStream.Input buffer) {
        boolean flag = buffer.readBoolean();
        int i = buffer.readInt();
        double d = buffer.readDouble();
        String s = buffer.readString();

        handle(flag, i, d, s);
    }

    public static void handle(boolean flag, int i, double d, String s) {
        System.out.println(flag);
        System.out.println(i);
        System.out.println(d);
        System.out.println(s);
    }

}
