package org.itzstonlex.recon.sql.util;

import java.util.function.Consumer;

public interface ThrowableConsumer<R> extends Consumer<R> {

    void implAccept(R value) throws Throwable;

    @Override
    default void accept(R value) {
        try {
            implAccept(value);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
