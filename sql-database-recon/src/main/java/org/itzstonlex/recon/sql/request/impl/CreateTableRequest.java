package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.impl.IndexedRequestField;
import org.itzstonlex.recon.sql.table.ReconSqlTableData;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CreateTableRequest extends ReconSqlRequest<IndexedRequestField> {

    private final String databaseTable;

    private boolean checkExists;
    private boolean temporary;

    public CreateTableRequest(String databaseTable) {
        this.databaseTable = databaseTable;
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
    protected void append(StringBuilder queryBuilder, LinkedList<IndexedRequestField> queryRows) {
        if (checkExists) {
            queryBuilder.append("IF NOT EXISTS ");
        }

        queryBuilder.append("`");
        queryBuilder.append(databaseTable);
        queryBuilder.append("` (");

        queryBuilder.append(String.join(", ", queryRows.stream()
                .map(IndexedRequestField::toString)
                .collect(Collectors.toCollection(LinkedList::new))));

        queryBuilder.append(")");
    }

    @Override
    public void updateSync(ReconSqlConnection connectionHandler) {
        addConnectionTable(connectionHandler);

        super.updateSync(connectionHandler);
    }

    @Override
    public void updateAsync(ReconSqlConnection connectionHandler) {
        addConnectionTable(connectionHandler);

        super.updateAsync(connectionHandler);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getSyncResponse(ReconSqlConnection connectionHandler) {
        addConnectionTable(connectionHandler);

        return super.getSyncResponse(connectionHandler);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getAsyncResponse(ReconSqlConnection connectionHandler) {
        addConnectionTable(connectionHandler);

        return super.getAsyncResponse(connectionHandler);
    }

    private void addConnectionTable(ReconSqlConnection connectionHandler) {
        connectionHandler.getDatabaseTables().put(databaseTable.toLowerCase(), new ReconSqlTableData(connectionHandler, databaseTable));
    }

}
