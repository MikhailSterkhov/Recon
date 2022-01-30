package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.request.ReconSqlResponse;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public interface ReconSqlExecutable {

    Connection getConnection();

    CompletableFuture<ReconSqlResponse> getResponse(boolean sync, String sql, Object... values);

    void update(boolean sync, String sql, Object... values);

}
