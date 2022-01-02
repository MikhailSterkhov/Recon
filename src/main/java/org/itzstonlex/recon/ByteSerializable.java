package org.itzstonlex.recon;

public interface ByteSerializable<Type> {

    Type write(ByteStream.Output buffer);

    Type read(ByteStream.Input buffer);
}
