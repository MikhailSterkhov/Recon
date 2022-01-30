package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class InsertRequest extends ReconSqlRequest<ValuedField> {

    private final String table;
    private boolean ignore;

    public InsertRequest(String table) {
        this.table = table;
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
    protected void append(StringBuilder requestBuilder, LinkedList<ValuedField> fieldsList) {
        if (ignore) {
            requestBuilder.append("IGNORE ");
        }

        requestBuilder.append("INTO `").append(table).append("`(");

        // Add request fields keys.
        requestBuilder.append(fieldsList.stream().map(field -> "`" + field.name() + "`").collect(Collectors.joining(", ")));

        // Add request fields values.
        requestBuilder.append(") VALUES (").append(fieldsList.stream().map(field -> "?").collect(Collectors.joining(", ")))
                .append(")");
    }

}
