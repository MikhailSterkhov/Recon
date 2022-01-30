package org.itzstonlex.recon.sql.execute;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public final class SqliteWrappedExecutor implements ReconSqlExecutable {

    private final ReconSqlConnectionExecutor impl;

    public SqliteWrappedExecutor(ReconSqlConnection connection) {
        this.impl = new ReconSqlConnectionExecutor(connection);
    }

    public String remakeSqliteRequest(String request) {

        // Replace auto_increment
        request = request.replace("AUTO_INCREMENT", "AUTOINCREMENT");

        // Replace fields types.
        request = request.replace("INT ", "INTEGER ").replace("INT,", "INTEGER,");

        // Return a remake result.
        return request;
    }

    @Override
    public Connection getConnection() {
        return impl.getConnection();
    }

    @Override
    public void update(boolean sync, String sql, Object... values) {
        impl.update(sync, this.remakeSqliteRequest(sql), values);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponse(boolean sync, String sql, Object... values) {
        return impl.getResponse(sync, this.remakeSqliteRequest(sql), values);
    }

}
