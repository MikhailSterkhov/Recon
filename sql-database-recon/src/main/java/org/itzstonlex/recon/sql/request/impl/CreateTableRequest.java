package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.table.TableDecorator;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CreateTableRequest extends ReconSqlRequest<IndexedField> {

    private final String table;

    private boolean checkExists;
    private boolean temporary;

    public CreateTableRequest(String table) {
        this.table = table;
    }

    public CreateTableRequest checkExists(boolean checkExists) {
        this.checkExists = checkExists;
        return this;
    }

    public CreateTableRequest temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    @Override
    public String getRequestCommand() {
        return ("CREATE" + (temporary ? " TEMPORARY " : " ") + "TABLE");
    }

    @Override
    protected void append(StringBuilder requestBuilder, LinkedList<IndexedField> fieldsList) {
        if (checkExists) {
            requestBuilder.append("IF NOT EXISTS ");
        }

        requestBuilder.append("`").append(table).append("`");

        requestBuilder.append(String.join(", ", fieldsList.stream()
                .map(IndexedField::toString)
                .collect(Collectors.toCollection(LinkedList::new))));

        requestBuilder.append(")");
    }

    private void addCachedTable(ReconSqlConnection connection) {
        connection.getLoadedTablesMap().put(table.toLowerCase(), new TableDecorator(connection, table));
    }

    @Override
    public void updateSync(ReconSqlConnection connection) {
        addCachedTable(connection);

        super.updateSync(connection);
    }

    @Override
    public void updateAsync(ReconSqlConnection connection) {
        addCachedTable(connection);

        super.updateAsync(connection);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponseSync(ReconSqlConnection connection) {
        addCachedTable(connection);

        return super.getResponseSync(connection);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponseAsync(ReconSqlConnection connection) {
        addCachedTable(connection);

        return super.getResponseAsync(connection);
    }
}
