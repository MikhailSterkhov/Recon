package org.itzstonlex.recon.minecraft.exception;

public class PacketException extends RuntimeException {

    public PacketException(Throwable throwable) {
        super(throwable);
    }

    public PacketException(String message) {
        super(message);
    }

    public PacketException(String message, Object... replacements) {
        super(String.format(message, replacements));
    }

}
