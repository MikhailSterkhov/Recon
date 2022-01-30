package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public final class SelectRequest extends ReconSqlRequest<ValuedField> {

    private final String table;

    private int limit = -1;
    private String[] selectedRows = {"*"};

    public SelectRequest(String table) {
        this.table = table;
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
    protected void append(StringBuilder requestBuilder, LinkedList<ValuedField> fieldsList) {

        if (Arrays.asList(selectedRows).contains("*")) {
            requestBuilder.append("*");

        } else {

            requestBuilder.append(String.join(",", Arrays.stream(selectedRows).map(field -> "`" + field + "`").collect(Collectors.toSet())));
        }

        requestBuilder.append(" FROM `").append(table).append("`");

        if (!fieldsList.isEmpty()) {

            requestBuilder.append(" WHERE ");
            requestBuilder.append(String.join(" AND ", fieldsList.stream().map(field -> "`" + field.name() + "`=?").collect(Collectors.toSet())));
        }

        if (limit >= 0) {

            requestBuilder.append(" LIMIT ");
            requestBuilder.append(limit);
        }
    }
}
