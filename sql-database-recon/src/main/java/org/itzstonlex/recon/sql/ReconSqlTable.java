package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.request.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.impl.InsertRequest;

import java.util.function.Consumer;

public interface ReconSqlTable {

    ReconSqlConnection getConnection();
    String getName();

    default ReconSqlRequestFactory createRequest() {
        return new ReconSqlRequestFactory(getName());
    }

    /**
     * Выполняет запрос в SQL базу функцией INSERT.
     * <p>
     * P.S.: Выполнять запрос в обработке НЕ НУЖНО!
     * так как он выполняется уже в процессе
     * этого метода
     *
     * @param queryHandler - Обработчик запроса
     */
    default void insert(Consumer<InsertRequest> queryHandler) {
        InsertRequest insertRequest = this.createRequest().insert();
        queryHandler.accept(insertRequest);

        insertRequest.updateSync(getConnection());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы .
     *
     * @param resultHandler - Обработчик запроса
     */
    default void selectAll(Consumer<ReconSqlResponse> resultHandler) {
        this.createRequest().select()
                .getResponseSync(getConnection())
                .thenAccept(resultHandler);
    }

    /**
     * Полная очистка таблицы от всех
     * данных в ней
     */
    default void clear() {
        this.createRequest().delete().updateSync(getConnection());
    }

    /**
     * Удаление таблицы из базы
     */
    default void drop() {
        this.createRequest().deleteTable().updateSync(getConnection());
    }

}
