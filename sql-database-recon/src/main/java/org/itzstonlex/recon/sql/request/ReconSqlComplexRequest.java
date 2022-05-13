package org.itzstonlex.recon.sql.request;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.request.field.ReconSqlRequestField;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ReconSqlComplexRequest {

    public static ReconSqlComplexRequest newComplex() {
        return new ReconSqlComplexRequest();
    }

    private final List<ReconSqlRequest<?>> complexRequests = new LinkedList<>();

    private ReconSqlComplexRequest() {
    }

    public ReconSqlComplexRequest pushComplex(ReconSqlRequest<?> request) {
        complexRequests.add(request);
        return this;
    }

    private String makeComplexRequest() {
        StringBuilder complexBuilder = new StringBuilder();

        for (ReconSqlRequest<?> request : complexRequests) {
            complexBuilder.append(request.toString()).append("\n");
        }

        return complexBuilder.toString();
    }

    private Object[] makeComplexArguments() {
        List<Object> arguments = new ArrayList<>();

        for (ReconSqlRequest<?> request : complexRequests) {
            arguments.add(request.getFieldsList().stream().map(ReconSqlRequestField::value).toArray());
        }

        return arguments.toArray();
    }

    public void updateSync(ReconSqlConnection connection) {
        connection.getExecution().update(true, this.makeComplexRequest(), this.makeComplexArguments());
    }

    public void updateAsync(ReconSqlConnection connection) {
        connection.getExecution().update(false, this.makeComplexRequest(), this.makeComplexArguments());
    }

    public CompletableFuture<ReconSqlResponse> getResponseSync(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(true, this.makeComplexRequest(), this.makeComplexArguments());
    }

    public CompletableFuture<ReconSqlResponse> getResponseAsync(ReconSqlConnection connection) {
        return connection.getExecution().getResponse(false, this.makeComplexRequest(), this.makeComplexArguments());
    }
}
