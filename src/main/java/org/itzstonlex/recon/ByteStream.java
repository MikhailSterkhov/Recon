package org.itzstonlex.recon;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ByteStream {

    Input newInput(byte[] bytes);

    Output transform(Input input);

    Output transform(byte[] bytes);

    Output newOutput();


    interface Buffer {

        int size();

        void flush();

        byte array(int index);

        byte[] array();
    }

    interface Input extends Buffer {

        byte[] readBytes(int length);

        byte readByte();

        int readInt();

        int readVarInt();

        long readLong();

        float readFloat();

        double readDouble();

        boolean readBoolean();

        char readChar();

        String readStringLE(int length, Charset charset);

        String readStringLE(int length);

        String readStringLE();

        String readString(int max, Charset charset);

        String readString(int max);

        String readString(Charset charset);

        String readString();

        List<String> readStringList();

        int[] readIntArray();

        long[] readLongArray();

        float[] readFloatArray();

        double[] readDoubleArray();

        boolean[] readBooleanArray();

        <R, C extends Collection<R>> C readCollection(Supplier<C> collectionInstance, Function<Input, R> reader);

        <R> R[] readArray(Function<Input, R> reader);

        <R extends ByteSerializable<?>> R readObject(Supplier<R> instance);

        Input copy();
    }

    interface Output extends Buffer {

        void write(byte[] bytes);

        void writeByte(byte value);

        void writeInt(int value);

        void writeVarInt(int value);

        void writeLong(long value);

        void writeFloat(float value);

        void writeDouble(double value);

        void writeBoolean(boolean value);

        void writeChar(char value);

        void writeString(String value, Charset charset);

        void writeString(String value);

        void writeStringList(List<String> value);

        void writeIntArray(int... value);

        void writeLongArray(long... value);

        void writeFloatArray(float... value);

        void writeDoubleArray(double... value);

        void writeBooleanArray(boolean... value);

        <R> void writeCollection(Collection<R> collection, BiConsumer<R, Output> writer);

        <R> void writeArray(R[] array, BiConsumer<R, Output> writer);

        void writeObject(ByteSerializable<?> value);

        Output copy();
    }

}
