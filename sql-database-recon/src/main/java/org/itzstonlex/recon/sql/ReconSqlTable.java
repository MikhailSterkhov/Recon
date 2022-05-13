package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.request.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;
import org.itzstonlex.recon.sql.request.impl.SelectRequest;

import java.util.concurrent.CompletableFuture;

public interface ReconSqlTable {

    ReconSqlConnection getConnection();
    String getName();

    default ReconSqlRequestFactory createRequest() {
        return new ReconSqlRequestFactory(getName());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы.
     *
     * @param limit - Лимит количества получаемых данных.
     */
    default CompletableFuture<ReconSqlResponse> selectAll(int limit) {
        return this.createRequest()
                .select()
                .limit(limit)
                .getResponseSync(getConnection());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая все данные из таблицы.
     */
    default CompletableFuture<ReconSqlResponse> selectAll() {
        return this.selectAll(-1);
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая данные по указанным переменным.
     *
     * @param limit       - Лимит количества получаемых данных.
     * @param fieldsWhere - Дополнения к подкоманде WHERE.
     */
    default CompletableFuture<ReconSqlResponse> selectWhere(int limit, ValuedField... fieldsWhere) {
        SelectRequest select = this.createRequest().select()
                .limit(limit);

        for (ValuedField where : fieldsWhere) {
            select.push(where);
        }

        return select.getResponseSync(getConnection());
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая данные по указанным переменным.
     *
     * @param fieldsWhere - Дополнения к подкоманде WHERE.
     */
    default CompletableFuture<ReconSqlResponse> selectWhere(ValuedField... fieldsWhere) {
        return this.selectWhere(-1, fieldsWhere);
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая количество записанных строк по
     * указанным переменным.
     *
     * @param limit       - Лимит количества получаемых данных.
     * @param fieldsWhere - Дополнения к подкоманде WHERE.
     */
    default int requestCount(int limit, ValuedField... fieldsWhere) {
        SelectRequest select = this.createRequest().select()
                .limit(limit);

        for (ValuedField where : fieldsWhere) {
            select.push(where);
        }

        return getConnection().getExecution().getResponse(true, select.toString().replace("\\*", "COUNT(*) AS `rcount`"))
                .thenApply(response -> response.next() ? 0 : response.getInt("rcount"))
                .join();
    }

    /**
     * Выполняет запрос в SQL базу функцией SELECT,
     * получая количество записанных строк по
     * указанным переменным.
     *
     * @param fieldsWhere - Дополнения к подкоманде WHERE.
     */
    default int requestCount(ValuedField... fieldsWhere) {
        return this.requestCount(-1, fieldsWhere);
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
