package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public final class DropTableRequest extends ReconSqlRequest<IndexedField> {

    private final String table;

    public DropTableRequest(String table) {
        this.table = table;
    }

    @Override
    public String getRequestCommand() {
        return "DROP TABLE";
    }

    @Override
    protected void append(StringBuilder requestBuilder, LinkedList<IndexedField> fieldsList) {
        requestBuilder.append("`").append(table).append("`");
    }

    private void removeCachedTable(ReconSqlConnection connection) {
        connection.getLoadedTablesMap().remove(table.toLowerCase());
    }

    @Override
    public void updateSync(ReconSqlConnection connection) {
        removeCachedTable(connection);

        super.updateSync(connection);
    }

    @Override
    public void updateAsync(ReconSqlConnection connection) {
        removeCachedTable(connection);

        super.updateAsync(connection);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponseSync(ReconSqlConnection connection) {
        removeCachedTable(connection);

        return super.getResponseSync(connection);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponseAsync(ReconSqlConnection connection) {
        removeCachedTable(connection);

        return super.getResponseAsync(connection);
    }

}
