package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.reqest.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.reqest.ReconSqlResponse;
import org.itzstonlex.recon.sql.reqest.impl.InsertRequest;

import java.util.function.Consumer;

public interface ReconSqlTable {

    ReconSqlConnection getConnectionHandler();
    String getName();

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
        InsertRequest insertRequest = newDatabaseQuery().insertQuery();
        queryHandler.accept(insertRequest);

        insertRequest.updateSync(getConnectionHandler());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы .
     *
     * @param resultHandler - Обработчик запроса
     */
    default void selectAll(Consumer<ReconSqlResponse> resultHandler) {
        newDatabaseQuery().selectQuery().getSyncResponse(getConnectionHandler())
                .thenAccept(resultHandler);
    }

    /**
     * Полная очистка таблицы от всех
     * данных в ней
     */
    default void clear() {
        newDatabaseQuery().deleteQuery().updateSync(getConnectionHandler());
    }

    /**
     * Удаление таблицы из базы
     */
    default void drop() {
        newDatabaseQuery().dropTableQuery().updateSync(getConnectionHandler());

        getConnectionHandler().getDatabaseTables().remove(getName().toLowerCase());
    }

    default ReconSqlRequestFactory newDatabaseQuery() {
        return new ReconSqlRequestFactory(getName());
    }
}
