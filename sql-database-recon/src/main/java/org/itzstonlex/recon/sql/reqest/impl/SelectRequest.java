package org.itzstonlex.recon.sql.reqest.impl;

import org.itzstonlex.recon.sql.reqest.ReconSqlRequest;
import org.itzstonlex.recon.sql.reqest.field.impl.ValuedRequestField;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public final class SelectRequest extends ReconSqlRequest<ValuedRequestField> {

    private final String databaseTable;

    private int limit = -1;
    private String[] selectedRows = {"*"};

    public SelectRequest(String databaseTable) {
        this.databaseTable = databaseTable;
    }

    public SelectRequest rows(String... selectedRows) {
        this.selectedRows = selectedRows;
        return this;
    }

    public SelectRequest limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    protected String getRequestCommand() {
        return "SELECT";
    }

    @Override
    protected void append(StringBuilder queryBuilder, LinkedList<ValuedRequestField> queryRows) {

        if (Arrays.asList(selectedRows).contains("*")) {
            queryBuilder.append("*");

        } else {

            queryBuilder.append(String.join(",", Arrays.stream(selectedRows).map(row -> "`" + row + "`").collect(Collectors.toSet())));
        }

        queryBuilder.append(" FROM `");
        queryBuilder.append(databaseTable);
        queryBuilder.append("`");

        if (!queryRows.isEmpty()) {

            queryBuilder.append(" WHERE ");
            queryBuilder.append(String.join(" AND ", queryRows.stream().map(row -> "`" + row.name() + "`=?").collect(Collectors.toSet())));
        }

        if (limit >= 0) {

            queryBuilder.append(" LIMIT ");
            queryBuilder.append(limit);
        }
    }
}
