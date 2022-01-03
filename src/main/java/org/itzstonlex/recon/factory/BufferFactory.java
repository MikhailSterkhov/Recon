package org.itzstonlex.recon.factory;

import org.itzstonlex.recon.ByteSerializable;
import org.itzstonlex.recon.ByteStream;
import org.itzstonlex.recon.error.BufferReadError;
import org.itzstonlex.recon.util.NumberUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Supplier;

public final class BufferFactory {

    public static final ByteStream BYTESTREAM
            = new PooledBuffer();

    public static ByteStream.Input createPooledInput(byte[] bytes) {
        return BYTESTREAM.newInput(bytes);
    }

    public static ByteStream.Output transformOutput(byte[] bytes) {
        return BYTESTREAM.transform(bytes);
    }

    public static ByteStream.Output transformOutput(ByteStream.Input input) {
        return BYTESTREAM.transform(input);
    }

    public static ByteStream.Output createPooledOutput() {
        return BYTESTREAM.newOutput();
    }


    private static class PooledOutput
            implements ByteStream.Output {

        private byte[] storage;

        public PooledOutput() {
            this.storage = new byte[0];
        }

        public PooledOutput(byte[] impl) {
            this.storage = impl;
        }

        @Override
        public void write(byte[] bytes) {
            int stSize = storage.length;
            storage = Arrays.copyOf(storage, stSize + bytes.length);

            int index = 0;
            for (byte value : bytes) {
                storage[stSize + index] = value;

                index++;
            }
        }

        @Override
        public void writeByte(byte value) {
            write(new byte[]{value});
        }

        @Override
        public void writeInt(int value) {
            while ((value & -128) != 0) {
                writeByte((byte)(value & 127 | 128));
                value >>>= 7;
            }

            writeByte((byte)value);
        }

        @Override
        public void writeFloat(float value) {
            writeDouble(value);
        }

        @Override
        public void writeDouble(double value) {
            writeInt((int) value);
            writeInt(NumberUtils.onlyDecimal(value));
        }

        @Override
        public void writeBoolean(boolean flag) {
            writeByte((byte)(flag ? 1 : 0));
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
        public void writeObject(ByteSerializable<?> value) {
            value.write(this);
        }

        @Override
        public void reset() {
            this.storage = new byte[0];
        }

        @Override
        public byte[] toByteArray() {
            return storage;
        }
    }

    private static class PooledInput
            implements ByteStream.Input {

        private byte[] storage;

        public PooledInput(byte[] impl) {
            this.storage = impl;
        }

        @Override
        public int size() {
            return storage.length;
        }

        @Override
        public byte[] read(int length) {
            byte[] readableBytes = Arrays.copyOf(storage, length);
            this.storage = Arrays.copyOfRange(storage, length, storage.length);

            return readableBytes;
        }

        @Override
        public byte readByte() {
            return read(1)[0];
        }

        @Override
        public int readInt() {
            int result = 0;
            int numRead = 0;

            byte read;

            do {
                read = readByte();
                result |= (read & 127) << numRead++ * 7;

                if (numRead > 5) {
                    throw new BufferReadError("VarInt is too big");
                }

            } while ((read & 128) == 128);
            return result;
        }

        @Override
        public float readFloat() {
            return (float) readDouble();
        }

        @Override
        public double readDouble() {
            return readInt() + Double.parseDouble("0." + readInt());
        }

        @Override
        public boolean readBoolean() {
            return readByte() == 1;
        }

        @Override
        public String readString(int length, Charset charset) {
            int size = readInt();
            byte[] array = read(size);

            if (size > length) {
                throw new BufferReadError("String value length must be <= %d", length);
            }

            return new String(array, 0, size, charset);
        }

        @Override
        public String readString(int length) {
            return readString(length, StandardCharsets.UTF_8);
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
        public <R extends ByteSerializable<?>> R readObject(Supplier<R> supplier) {
            R instance = supplier.get();
            instance.read(this);

            return instance;
        }

        @Override
        public void reset() {
            this.storage = new byte[0];
        }
    }

    private static class PooledBuffer
            implements ByteStream {

        @Override
        public Input newInput(byte[] bytes) {
            return new PooledInput(bytes);
        }

        @Override
        public Output transform(Input input) {
            return new PooledOutput(input.read(input.size()));
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
