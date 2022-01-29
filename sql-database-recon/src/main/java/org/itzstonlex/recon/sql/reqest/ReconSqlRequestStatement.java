package org.itzstonlex.recon.sql.reqest;

import org.itzstonlex.recon.sql.ReconSql;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ReconSqlRequestStatement
        implements Closeable {

    private final boolean sync;

    private final PreparedStatement preparedStatement;
    private ReconSqlResponse response;

    /**
     * Инициализация статемента
     */
    public ReconSqlRequestStatement(boolean sync, Connection connection, String sql, Object... values)
    throws SQLException {

        this.sync = sync;
        this.preparedStatement = connection.prepareStatement(sql);

        if (values != null && values.length > 0 && Arrays.stream(values).anyMatch(Objects::nonNull)) {
            for (int i = 0; i < values.length; i++) {

                preparedStatement.setObject(i + 1, values[i]);
            }
        }
    }

    public void executeUpdate() {
        try {
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ReconSqlResponse executeQuery() {
        try {
            return (this.response = new ReconSqlResponse(preparedStatement.executeQuery()));
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        Runnable command = () -> ReconSql.getInstance().handleExceptions(() -> {

            if (response != null && !response.isClosed()) {
                response.close();
            }

            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        });

        if (!sync) {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(1500);

                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                command.run();
            });

            return;
        }

        command.run();
    }

}
