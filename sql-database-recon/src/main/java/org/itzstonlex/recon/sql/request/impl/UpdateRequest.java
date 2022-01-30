package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedRequestField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class UpdateRequest extends ReconSqlRequest<ValuedRequestField> {

    private final String databaseTable;
    private final LinkedList<ValuedRequestField> whereRowsList = new LinkedList<>();

    public UpdateRequest(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    @Override
    protected String getRequestCommand() {
        return "UPDATE";
    }

    public UpdateRequest where(ValuedRequestField valuedRequestField) {
        whereRowsList.add(valuedRequestField);
        return this;
    }

    @Override
    protected void append(StringBuilder queryBuilder, LinkedList<ValuedRequestField> queryRows) {
        queryBuilder.append("`");
        queryBuilder.append(databaseTable);
        queryBuilder.append("`");

        if (!queryRows.isEmpty()) {

            queryBuilder.append(" SET ");
            queryBuilder.append(String.join(" AND ", queryRows.stream().map(row -> "`" + row.name() + "`=?").collect(Collectors.toSet())));
        }

        if (!whereRowsList.isEmpty()) {

            queryBuilder.append(" WHERE ");
            queryBuilder.append(String.join(" AND ", whereRowsList.stream().map(row -> "`" + row.name() + "`=?").collect(Collectors.toSet())));
        }

        queryRows.addAll(whereRowsList);
    }
}
