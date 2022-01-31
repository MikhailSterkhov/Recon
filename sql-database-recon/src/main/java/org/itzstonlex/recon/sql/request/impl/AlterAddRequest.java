package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;

import java.util.LinkedList;
import java.util.stream.Collectors;

public final class AlterAddRequest extends ReconSqlRequest<IndexedField> {

    private final String table;

    public AlterAddRequest(String table) {
        this.table = table;
    }

    @Override
    protected String getRequestCommand() {
        return "ALTER TABLE";
    }

    @Override
    protected void append(StringBuilder requestBuilder, LinkedList<IndexedField> fieldsList) {
        requestBuilder.append("`").append(table).append("`");

        if (!fieldsList.isEmpty()) {

            requestBuilder.append(" ADD COLUMN ");
            requestBuilder.append(String.join(", ", fieldsList.stream().map(IndexedField::toString).collect(Collectors.toSet())));
        }
    }
}
