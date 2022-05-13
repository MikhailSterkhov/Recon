package org.itzstonlex.recon.sql.request.impl;

import org.itzstonlex.recon.sql.request.ReconSqlRequest;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public final class SelectRequest extends ReconSqlRequest<ValuedField> {

    private final String table;

    private int limit = -1;
    private String[] selectedRows = {"*"};

    private final Set<ValuedField> likeThenSet = new HashSet<>();
    private final Set<ValuedField> moreThenSet = new HashSet<>();
    private final Set<ValuedField> lessThenSet = new HashSet<>();

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

    public SelectRequest pushLikeThen(ValuedField field) {
        likeThenSet.add(field);

        super.push(field);
        return this;
    }

    public SelectRequest pushMoreThen(ValuedField field) {
        moreThenSet.add(field);

        super.push(field);
        return this;
    }

    public SelectRequest pushLessThen(ValuedField field) {
        lessThenSet.add(field);

        super.push(field);
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
            requestBuilder.append(String.join(" AND ", fieldsList.stream().map(field -> {

                String sign = ("=");

                if (likeThenSet.contains(field)) {
                    sign = (" LIKE ");
                }
                else if (moreThenSet.contains(field)) {
                    sign = ">";
                }
                else if (lessThenSet.contains(field)) {
                    sign = "<";
                }

                return "`" + field.name() + "`" + sign + "?";
            }).collect(Collectors.toSet())));
        }

        if (limit >= 0) {

            requestBuilder.append(" LIMIT ");
            requestBuilder.append(limit);
        }
    }
}
