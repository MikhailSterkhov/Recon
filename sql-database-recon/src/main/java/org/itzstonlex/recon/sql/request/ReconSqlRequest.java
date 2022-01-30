package org.itzstonlex.recon.sql.request;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.request.field.ReconSqlRequestField;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public abstract class ReconSqlRequest<RequestField extends ReconSqlRequestField> {

    private final LinkedList<RequestField> fieldsList = new LinkedList<>();

    public ReconSqlRequest<RequestField> push(RequestField field) {
        fieldsList.add(field);
        return this;
    }

    public void updateSync(ReconSqlConnection connection) {
        connection.getExecution().update(true, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    public void updateAsync(ReconSqlConnection connection) {
        connection.getExecution().update(false, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    public CompletableFuture<ReconSqlResponse> getResponseSync(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(true, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    public CompletableFuture<ReconSqlResponse> getResponseAsync(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(false, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    protected abstract String getRequestCommand();

    protected abstract void append(StringBuilder requestBuilder, LinkedList<RequestField> fieldsList);

    @Override
    public String toString() {
        StringBuilder requestBuilder = new StringBuilder(getRequestCommand())
                .append(" ");

        append(requestBuilder, fieldsList);
        return requestBuilder.toString();
    }
}
