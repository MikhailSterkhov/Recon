package org.itzstonlex.recon.sql.execute;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public final class SqliteExecutor implements ReconSqlExecutable {

    private final ReconSqlConnectionExecutor defaultExecutor;

    public SqliteExecutor(ReconSqlConnection databaseConnection) {
        this.defaultExecutor = new ReconSqlConnectionExecutor(databaseConnection);
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
        return defaultExecutor.getConnection();
    }

    @Override
    public void update(boolean sync, String sql, Object... values) {
        defaultExecutor.update(sync, this.remakeSqliteRequest(sql), values);
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponse(boolean sync, String sql, Object... values) {
        return defaultExecutor.getResponse(sync, this.remakeSqliteRequest(sql), values);
    }

}
