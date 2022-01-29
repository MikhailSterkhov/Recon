package org.itzstonlex.recon.sql.reqest;

import org.itzstonlex.recon.sql.reqest.impl.*;

public final class ReconSqlRequestFactory {

    private final String databaseTable;

    public ReconSqlRequestFactory(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    public CreateTableRequest createTableQuery() {
        return new CreateTableRequest(databaseTable);
    }

    public DropTableRequest dropTableQuery() {
        return new DropTableRequest(databaseTable);
    }

    public InsertRequest insertQuery() {
        return new InsertRequest(databaseTable);
    }

    public SelectRequest selectQuery() {
        return new SelectRequest(databaseTable);
    }

    public DeleteRequest deleteQuery() {
        return new DeleteRequest(databaseTable);
    }

    public UpdateRequest updateQuery() {
        return new UpdateRequest(databaseTable);
    }

}
