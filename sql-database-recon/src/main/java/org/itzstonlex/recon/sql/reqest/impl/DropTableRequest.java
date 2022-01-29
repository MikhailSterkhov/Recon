package org.itzstonlex.recon.sql.reqest.impl;

import org.itzstonlex.recon.sql.reqest.ReconSqlRequest;
import org.itzstonlex.recon.sql.reqest.field.impl.IndexedRequestField;

import java.util.LinkedList;

public final class DropTableRequest extends ReconSqlRequest<IndexedRequestField> {

    private final String databaseTable;

    public DropTableRequest(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    @Override
    public String getRequestCommand() {
        return "DROP TABLE";
    }

    @Override
    protected void append(StringBuilder queryBuilder, LinkedList<IndexedRequestField> queryRows) {
        queryBuilder.append("`");
        queryBuilder.append(databaseTable);
        queryBuilder.append("`");
    }

}
