package org.itzstonlex.recon.sql.objects;

import org.itzstonlex.recon.sql.request.ReconSqlResponse;

import java.util.concurrent.CompletableFuture;

public interface SqlObjectWorker {

    boolean exists(SqlObjectDescription<?> description);

    void create(SqlObjectDescription<?> description);

    void delete(SqlObjectDescription<?> description);

    void update(SqlObjectDescription<?> description);

    void execute(SqlObjectDescription<?> description, String sql);

    CompletableFuture<ReconSqlResponse> executeWithResponse(SqlObjectDescription<?> description, String sql);

    <V> SqlObjectDescription<V> injectObject(V instance);
}
