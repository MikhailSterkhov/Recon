package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class UpdateRequest extends ReconSqlRequest<ValuedField> {

    private final String table;
    private final LinkedList<ValuedField> whereRowsList = new LinkedList<>();

    public UpdateRequest(String table) {
        this.table = table;
    }

    @Override
    protected String getRequestCommand() {
        return "UPDATE";
    }

    public UpdateRequest where(ValuedField valuedField) {
        whereRowsList.add(valuedField);
        return this;
    }

    @Override
    protected void append(StringBuilder requestBuilder, LinkedList<ValuedField> fieldsList) {
        requestBuilder.append("`").append(table).append("`");

        if (!fieldsList.isEmpty()) {

            requestBuilder.append(" SET ");
            requestBuilder.append(String.join(" AND ", fieldsList.stream().map(field -> "`" + field.name() + "`=?").collect(Collectors.toSet())));
        }

        if (!whereRowsList.isEmpty()) {

            requestBuilder.append(" WHERE ");
            requestBuilder.append(String.join(" AND ", whereRowsList.stream().map(field -> "`" + field.name() + "`=?").collect(Collectors.toSet())));
        }

        fieldsList.addAll(whereRowsList);
    }
}
