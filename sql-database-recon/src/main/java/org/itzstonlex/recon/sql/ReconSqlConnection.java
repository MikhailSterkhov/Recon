package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.sql.event.ReconSqlEventListener;
import org.itzstonlex.recon.sql.request.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.request.impl.CreateTableRequest;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface ReconSqlConnection {

    ReconSqlCredentials getCredentials();

    ReconLog getLogger();

    ExecutorService getThreadExecutor();

    ReconSqlExecutable getExecution();

    Connection getConnection();

    ReconSqlEventListener getEventHandler();

    Map<String, ReconSqlTable> getLoadedTablesMap();

    ReconSqlTable getTable(String tableName);

    void setEventHandler(ReconSqlEventListener eventListener);

    void connect();

    void disconnect();

    void reconnect();

    default ReconSqlRequestFactory createRequest(String databaseTable) {
        return ReconSql.getInstance().createRequest(databaseTable);
    }

    /**
     * Создание таблицы и ее получение из базы SQL
     * при помощи функции CREATE TABLE.
     * <p>
     * P.S.: Выполнять запрос в обработке НЕ НУЖНО!
     * так как он выполняется уже в процессе
     * этого метода
     *
     * @param tableName         - Название таблицы
     * @param requestHandler    - Обработчик запроса
     */
    default ReconSqlTable createOrGetTable(String tableName, Consumer<CreateTableRequest> requestHandler) {
        ReconSqlTable reconSqlTable = getTable(tableName);

        if (reconSqlTable != null) {
            return reconSqlTable;
        }

        CreateTableRequest createTableRequest = this.createRequest(tableName)
                .createTable()
                .checkExists(true);

        if (requestHandler != null) {
            requestHandler.accept(createTableRequest);
        }

        createTableRequest.updateSync(this);

        return getTable(tableName);
    }

}
