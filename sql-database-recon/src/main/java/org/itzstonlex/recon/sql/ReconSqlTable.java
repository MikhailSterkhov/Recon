package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.request.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;

import java.util.function.Consumer;

public interface ReconSqlTable {

    ReconSqlConnection getConnection();
    String getName();

    default ReconSqlRequestFactory createRequest() {
        return new ReconSqlRequestFactory(getName());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы .
     *
     * @param responseHandler - Обработчик запроса
     */
    default void selectAll(Consumer<ReconSqlResponse> responseHandler) {
        this.createRequest()
                .select()
                .getResponseSync(getConnection())
                .thenAccept(responseHandler);
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы .
     *
     * @param responseHandler - Обработчик запроса
     */
    default void selectAll(int limit, Consumer<ReconSqlResponse> responseHandler) {
        this.createRequest()
                .select()
                .limit(limit)
                .getResponseSync(getConnection())
                .thenAccept(responseHandler);
    }

    /**
     * Полная очистка таблицы от всех
     * данных в ней
     */
    default void clear() {
        this.createRequest()
                .delete()
                .updateSync(getConnection());
    }

    /**
     * Удаление таблицы из базы
     */
    default void drop() {
        this.createRequest()
                .deleteTable()
                .updateSync(getConnection());
    }

}
