package org.itzstonlex.recon.init;

import org.itzstonlex.recon.ByteSerializable;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.exception.BufferReadException;
import org.itzstonlex.recon.factory.BufferFactory;
import org.itzstonlex.recon.util.PrimitiveByteUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BufferInitializer {

    public static class PooledOutput implements ByteStream.Output {
        private byte[] buffer;

        public PooledOutput() {
            this.buffer = new byte[0];
        }

        public PooledOutput(byte[] impl) {
            this.buffer = impl;
        }

        @Override
        public void write(byte[] bytes) {
            int stSize = buffer.length;
            buffer = Arrays.copyOf(buffer, stSize + bytes.length);

            int index = 0;
            for (byte value : bytes) {
                buffer[stSize + index] = value;

                index++;
            }
        }

        @Override
        public void writeByte(byte value) {
            write(new byte[]{value});
        }

        @Override
        public void writeInt(int value) {
            write( PrimitiveByteUtils.toByteArray(value) );
        }

        @Override
        public void writeVarInt(int value) {
            while ((value & -128) != 0) {
                writeByte((byte)(value & 127 | 128));
                value >>>= 7;
            }

            writeByte((byte)value);
        }

        @Override
        public void writeLong(long value) {
            write( PrimitiveByteUtils.toByteArray(value) );
        }

        @Override
        public void writeFloat(float value) {
            write( PrimitiveByteUtils.toByteArray(value) );
        }

        @Override
        public void writeDouble(double value) {
            write( PrimitiveByteUtils.toByteArray(value) );
        }

        @Override
        public void writeBoolean(boolean flag) {
            writeByte((byte)(flag ? 1 : 0));
        }

        @Override
        public void writeChar(char value) {
            write( PrimitiveByteUtils.toByteArray(value) );
        }

        @Override
        public void writeString(String value, Charset charset) {
            byte[] bytes = value.getBytes(charset);

            writeInt(bytes.length);
            write(bytes);
        }

        @Override
        public void writeString(String value) {
            writeString(value, StandardCharsets.UTF_8);
        }

        @Override
        public void writeStringList(List<String> value) {
            writeCollection(value, (string, buffer) -> buffer.writeString(string));
        }

        @Override
        public void writeIntArray(int... value) {
            writeInt(value.length);

            for (int var : value) {
                writeInt(var);
            }
        }

        @Override
        public void writeLongArray(long... value) {
            writeInt(value.length);

            for (long var : value) {
                writeLong(var);
            }
        }

        @Override
        public void writeFloatArray(float... value) {
            writeInt(value.length);

            for (float var : value) {
                writeFloat(var);
            }
        }

        @Override
        public void writeDoubleArray(double... value) {
            writeInt(value.length);

            for (double var : value) {
                writeDouble(var);
            }
        }

        @Override
        public void writeBooleanArray(boolean... value) {
            writeInt(value.length);

            for (boolean var : value) {
                writeBoolean(var);
            }
        }

        @Override
        public <R> void writeCollection(Collection<R> collection, BiConsumer<R, ByteStream.Output> writer) {
            writeInt(collection.size());

            for (R element : collection) {
                writer.accept(element, this);
            }
        }

        @Override
        public <R> void writeArray(R[] array, BiConsumer<R, ByteStream.Output> writer) {
            writeInt(array.length);

            for (R element : array) {
                writer.accept(element, this);
            }
        }

        @Override
        public void writeObject(ByteSerializable<?> value) {
            value.write(this);
        }

        @Override
        public ByteStream.Output copy() {
            return BufferFactory.transformOutput(buffer);
        }

        @Override
        public int size() {
            return buffer.length;
        }

        @Override
        public void flush() {
            this.buffer = new byte[0];
        }

        @Override
        public byte array(int index) {
            return buffer[index];
        }

        @Override
        public byte[] array() {
            return buffer;
        }
    }

    public static class PooledInput implements ByteStream.Input {
        private byte[] buffer;
        private int pointer = 0;

        public PooledInput(byte[] impl) {
            this.buffer = impl;
        }

        @Override
        public int size() {
            return buffer.length;
        }

        @Override
        public byte[] readBytes(int length) {
            if (length < 0) {
                throw new BufferReadException("read length must be >= 0");
            }

            byte[] read = Arrays.copyOfRange(buffer, pointer, pointer + length);
            pointer += length;

            return read;
        }

        @Override
        public byte array(int index) {
            return buffer[index];
        }

        @Override
        public byte[] array() {
            return buffer;
        }

        @Override
        public byte readByte() {
            return readBytes(1)[0];
        }

        @Override
        public int readInt() {
            return PrimitiveByteUtils.readInt( readBytes(Integer.BYTES) );
        }

        @Override
        public int readVarInt() {
            int result = 0;
            int numRead = 0;

            byte read;

            do {
                read = readByte();
                result |= (long) (read & 127) << numRead++ * 7;

                if (numRead > 5) {
                    throw new BufferReadException("VarInt is too big");
                }

            } while ((read & 128) == 128);
            return result;
        }

        @Override
        public long readLong() {
            return PrimitiveByteUtils.readLong( readBytes(Long.BYTES) );
        }

        @Override
        public float readFloat() {
            return PrimitiveByteUtils.readFloat( readBytes(Float.BYTES) );
        }

        @Override
        public double readDouble() {
            return PrimitiveByteUtils.readDouble( readBytes(Double.BYTES) );
        }

        @Override
        public char readChar() {
            return PrimitiveByteUtils.readChar( readBytes(Character.BYTES) );
        }

        @Override
        public String readStringLE(int length, Charset charset) {
            byte[] array = readBytes(length);
            return new String(array, 0, array.length, charset);
        }

        @Override
        public String readStringLE(int length) {
            return readStringLE(length, StandardCharsets.UTF_8);
        }

        @Override
        public String readStringLE() {
            return readStringLE(size(), StandardCharsets.UTF_8);
        }

        @Override
        public boolean readBoolean() {
            return readByte() == 1;
        }

        @Override
        public String readString(int max, Charset charset) {
            int size = readVarInt();
            if (size > max) {
                throw new BufferReadException("String value length must be <= %d", max);
            }

            return readStringLE(size, charset);
        }

        @Override
        public String readString(int max) {
            return readString(max, StandardCharsets.UTF_8);
        }

        @Override
        public String readString(Charset charset) {
            return readString(Short.MAX_VALUE, charset);
        }

        @Override
        public String readString() {
            return readString(StandardCharsets.UTF_8);
        }

        @Override
        public List<String> readStringList() {
            return readCollection(ArrayList::new, ByteStream.Input::readString);
        }

        @Override
        public int[] readIntArray() {
            int size = readInt();
            int[] array = new int[size];

            for (int i = 0; i < size; i++) {
                array[i] = readInt();
            }

            return array;
        }

        @Override
        public long[] readLongArray() {
            int size = readInt();
            long[] array = new long[size];

            for (int i = 0; i < size; i++) {
                array[i] = readLong();
            }

            return array;
        }

        @Override
        public float[] readFloatArray() {
            int size = readInt();
            float[] array = new float[size];

            for (int i = 0; i < size; i++) {
                array[i] = readFloat();
            }

            return array;
        }

        @Override
        public double[] readDoubleArray() {
            int size = readInt();
            double[] array = new double[size];

            for (int i = 0; i < size; i++) {
                array[i] = readDouble();
            }

            return array;
        }

        @Override
        public boolean[] readBooleanArray() {
            int size = readInt();
            boolean[] array = new boolean[size];

            for (int i = 0; i < size; i++) {
                array[i] = readBoolean();
            }

            return array;
        }

        @Override
        public <R, C extends Collection<R>> C readCollection(Supplier<C> collectionInstance,
                                                             Function<ByteStream.Input, R> reader) {
            C collection = collectionInstance.get();
            int size = readInt();

            for (int i = 0; i < size; i++) {
                collection.add( reader.apply(this) );
            }

            return collection;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> R[] readArray(Function<ByteStream.Input, R> reader) {
            int size = readInt();
            R[] array = (R[]) new Object[size];

            for (int i = 0; i < size; i++) {
                array[i] = reader.apply(this);
            }

            return array;
        }

        @Override
        public <R extends ByteSerializable<?>> R readObject(Supplier<R> supplier) {
            R instance = supplier.get();
            instance.read(this);

            return instance;
        }

        @Override
        public ByteStream.Input copy() {
            return BufferFactory.createPooledInput(buffer);
        }

        @Override
        public void flush() {
            this.buffer = new byte[0];
        }
    }

    public static class PooledBuffer
            implements ByteStream {

        @Override
        public Input newInput(byte[] bytes) {
            return new PooledInput(bytes);
        }

        @Override
        public Output transform(Input input) {
            return new PooledOutput(input.readBytes(input.size()));
        }

        @Override
        public Output transform(byte[] bytes) {
            return new PooledOutput(bytes);
        }

        @Override
        public Output newOutput() {
            return new PooledOutput();
        }
    }

}
