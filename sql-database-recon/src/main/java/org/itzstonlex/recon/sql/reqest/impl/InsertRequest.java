package org.itzstonlex.recon.sql.reqest.impl;

import org.itzstonlex.recon.sql.reqest.ReconSqlRequest;
import org.itzstonlex.recon.sql.reqest.field.impl.ValuedRequestField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class InsertRequest extends ReconSqlRequest<ValuedRequestField> {

    private final String databaseTable;
    private boolean ignore;

    public InsertRequest(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    public InsertRequest ignore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    @Override
    protected String getRequestCommand() {
        return "INSERT";
    }

    @Override
    protected void append(StringBuilder queryBuilder, LinkedList<ValuedRequestField> queryRows) {
        if (ignore) queryBuilder.append("IGNORE ");

        queryBuilder.append("INTO ");
        queryBuilder.append("`");
        queryBuilder.append(databaseTable);
        queryBuilder.append("` ");
        queryBuilder.append("(");

        // Build rows.
        queryBuilder.append(queryRows.stream().map(valueQueryRow -> "`" + valueQueryRow.name() + "`").collect(Collectors.joining(", ")));

        // Build rows values.
        queryBuilder.append(") VALUES (");
        queryBuilder.append(queryRows.stream().map(valueQueryRow -> "?").collect(Collectors.joining(", ")));
        queryBuilder.append(")");
    }

}
