package org.itzstonlex.recon.sql.util;

import java.util.function.Supplier;

public interface ThrowableSupplier<R> extends Supplier<R> {

    R implGet() throws Throwable;

    @Override
    default R get() {
        try {
            return implGet();
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}
