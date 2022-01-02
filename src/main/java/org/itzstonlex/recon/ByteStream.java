package org.itzstonlex.recon;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public interface ByteStream {

    Input newInput(byte[] bytes);

    Output transform(Input input);

    Output transform(byte[] bytes);

    Output newOutput();


    interface Input {

        int size();

        byte[] read(int length);

        byte readByte();

        int readInt();

        float readFloat();

        double readDouble();

        boolean readBoolean();

        String readString(int length, Charset charset);

        String readString(int length);

        String readString(Charset charset);

        String readString();

        <R extends ByteSerializable<?>> R readObject(Supplier<R> instance);
    }

    interface Output {

        void write(byte[] bytes);

        void writeByte(byte value);

        void writeInt(int value);

        void writeFloat(float value);

        void writeDouble(double value);

        void writeBoolean(boolean flag);

        void writeString(String value, Charset charset);

        void writeString(String value);

        void writeObject(ByteSerializable<?> value);

        byte[] toByteArray();
    }

}
