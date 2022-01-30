package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class DeleteRequest extends ReconSqlRequest<ValuedField> {

    private final String table;

    public DeleteRequest(String table) {
        this.table = table;
    }

    @Override
    protected String getRequestCommand() {
        return "DELETE FROM";
    }

    @Override
    protected void append(StringBuilder requestBuilder, LinkedList<ValuedField> fieldsList) {
        requestBuilder.append("`").append(table).append("`");

        if (!fieldsList.isEmpty()) {

            requestBuilder.append(" WHERE ");
            requestBuilder.append(String.join(" AND ", fieldsList.stream().map(field -> "`" + field.name() + "`=?").collect(Collectors.toSet())));
        }
    }

}
