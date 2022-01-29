package org.itzstonlex.recon.sql.execute;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.reqest.ReconSqlResponse;
import org.itzstonlex.recon.sql.reqest.ReconSqlRequestStatement;
import org.itzstonlex.recon.sql.util.ThrowableSupplier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class ReconSqlConnectionExecutor implements ReconSqlExecutable {

    private final ReconSqlConnection databaseConnection;

    public ReconSqlConnectionExecutor(ReconSqlConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Connection getConnection() {
        return databaseConnection.getConnection();
    }

    @Override
    public void update(boolean sync, String sql, Object... values) {
        Runnable command = () -> {
            databaseConnection.reconnect();

            try (ReconSqlRequestStatement queryStatement = new ReconSqlRequestStatement(sync, getConnection(), sql, values)) {
                queryStatement.executeUpdate();

                if (databaseConnection.getEventHandler() != null) {
                    databaseConnection.getEventHandler().onExecute(databaseConnection, sql);
                }
            }

            catch (SQLException exception) {
                exception.printStackTrace();
            }
        };

        if (!sync) {
            databaseConnection.getThreadExecutor().submit(command);
            return;
        }

        command.run();
    }

    @Override
    public CompletableFuture<ReconSqlResponse> getResponse(boolean sync, String sql, Object... values) {
        databaseConnection.reconnect();

        try (ReconSqlRequestStatement queryStatement = new ReconSqlRequestStatement(false, getConnection(), sql, values)) {
            if (databaseConnection.getEventHandler() != null) {
                databaseConnection.getEventHandler().onExecute(databaseConnection, sql);
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
