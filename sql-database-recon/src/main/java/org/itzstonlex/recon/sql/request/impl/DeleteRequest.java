package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedRequestField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class DeleteRequest extends ReconSqlRequest<ValuedRequestField> {

    private final String databaseTable;

    public DeleteRequest(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    @Override
    protected String getRequestCommand() {
        return "DELETE FROM";
    }

    @Override
    protected void append(StringBuilder queryBuilder, LinkedList<ValuedRequestField> queryRows) {

        queryBuilder.append("`");
        queryBuilder.append(databaseTable);
        queryBuilder.append("`");

        if (!queryRows.isEmpty()) {

            queryBuilder.append(" WHERE ");
            queryBuilder.append(String.join(" AND ", queryRows.stream().map(row -> "`" + row.name() + "`=?").collect(Collectors.toSet())));
        }
    }

}
