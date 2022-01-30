package org.itzstonlex.recon.sql.execute;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.ReconSqlRequestStatement;
import org.itzstonlex.recon.sql.util.ThrowableSupplier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class ReconSqlConnectionExecutor implements ReconSqlExecutable {

    private final ReconSqlConnection connection;

    public ReconSqlConnectionExecutor(ReconSqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection.getConnection();
    }

    @Override
    public void update(boolean sync, String sql, Object... values) {
        Runnable command = () -> {
            connection.reconnect();

            try (ReconSqlRequestStatement queryStatement = new ReconSqlRequestStatement(sync, getConnection(), sql, values)) {
                queryStatement.executeUpdate();

                if (connection.getEventHandler() != null) {
                    connection.getEventHandler().onExecute(connection, sql);
                }
            }

            catch (SQLException exception) {
                exception.printStackTrace();
            }
        };

        if (!sync) {
            connection.getThreadExecutor().submit(command);
            return;
        }

        command.run();
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponse(boolean sync, String sql, Object... values) {
        connection.reconnect();

        try (ReconSqlRequestStatement queryStatement = new ReconSqlRequestStatement(false, getConnection(), sql, values)) {
            if (connection.getEventHandler() != null) {
                connection.getEventHandler().onExecute(connection, sql);
            }

            return sync
                    ? CompletableFuture.completedFuture(queryStatement.executeQuery())
                    : CompletableFuture.supplyAsync((ThrowableSupplier<ReconSqlResponse>) queryStatement::executeQuery);
        }

        catch (SQLException exception) {
            exception.printStackTrace();

            return null;
        }
    }

}
