package org.itzstonlex.recon.sql.request;

import org.itzstonlex.recon.sql.request.impl.*;

public final class ReconSqlRequestFactory {

    private final String tableName;

    public ReconSqlRequestFactory(String tableName) {
        this.tableName = tableName;
    }

    public AlterAddRequest alterAdd() {
        return new AlterAddRequest(tableName);
    }

    public AlterDropRequest alterDelete() {
        return new AlterDropRequest(tableName);
    }

    public AlterModifyRequest alterModify() {
        return new AlterModifyRequest(tableName);
    }

    public CreateTableRequest createTable() {
        return new CreateTableRequest(tableName);
    }

    public DropTableRequest deleteTable() {
        return new DropTableRequest(tableName);
    }

    public InsertRequest insert() {
        return new InsertRequest(tableName);
    }

    public SelectRequest select() {
        return new SelectRequest(tableName);
    }

    public DeleteRequest delete() {
        return new DeleteRequest(tableName);
    }

    public UpdateRequest update() {
        return new UpdateRequest(tableName);
    }

}
