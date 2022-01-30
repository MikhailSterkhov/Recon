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

    public CompletableFuture<ReconSqlResponse> getSyncResponse(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(true, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    public CompletableFuture<ReconSqlResponse> getAsyncResponse(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(false, this.toString(), fieldsList.stream().map(RequestField::value).toArray());
    }

    protected abstract String getRequestCommand();

    protected abstract void append(StringBuilder queryBuilder, LinkedList<RequestField> queryRows);

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(getRequestCommand());
        stringBuilder.append(" ");

        append(stringBuilder, fieldsList);

        return stringBuilder.toString();
    }
}
