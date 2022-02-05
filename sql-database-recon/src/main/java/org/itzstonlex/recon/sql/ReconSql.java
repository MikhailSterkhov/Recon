package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.HikariDatabaseConnection;
import org.itzstonlex.recon.sql.connection.MysqlDatabaseConnection;
import org.itzstonlex.recon.sql.connection.SqliteDatabaseConnection;
import org.itzstonlex.recon.sql.exception.ReconSqlException;
import org.itzstonlex.recon.sql.objects.SqlObjectWorker;
import org.itzstonlex.recon.sql.objects.worker.SqlObjectWorkerImpl;
import org.itzstonlex.recon.sql.request.ReconSqlRequestFactory;
import org.itzstonlex.recon.sql.util.SQLExceptionHandler;

import java.io.File;
import java.sql.SQLException;

public final class ReconSql {
    
    private static final ReconSql instance = new ReconSql();

    public static ReconSql getInstance() {
        return instance;
    }

    public SqlObjectWorker newObjectWorker(ReconSqlConnection connection) {
        return new SqlObjectWorkerImpl(connection);
    }

    public ReconSqlCredentials createCredentials(String host, String username, String password, String scheme) {
        return new ReconSqlCredentials(host, username, password, scheme);
    }

    public ReconSqlCredentials createCredentials(int port, String host, String username, String password, String scheme) {
        return new ReconSqlCredentials(port, host, username, password, scheme);
    }

    public MysqlDatabaseConnection createMysqlConnection(ReconSqlCredentials credentials) {
        return new MysqlDatabaseConnection(credentials);
    }

    public HikariDatabaseConnection createHikariConnection(String driverClassname, String driverUrl, String username, String password) {
        return new HikariDatabaseConnection(driverClassname, driverUrl, username, password);
    }

    public HikariDatabaseConnection createHikariConnection(String driverClassname, ReconSqlCredentials credentials) {
        return new HikariDatabaseConnection(driverClassname, credentials);
    }

    public HikariDatabaseConnection createHikariConnection(ReconSqlCredentials credentials) {
        return new HikariDatabaseConnection(credentials);
    }

    public SqliteDatabaseConnection createSqliteConnection(File sqliteFile) {
        return new SqliteDatabaseConnection(sqliteFile);
    }

    public ReconSqlRequestFactory createRequest(String databaseTable) {
        return new ReconSqlRequestFactory(databaseTable);
    }

    public ReconSqlRequestFactory createRequest(ReconSqlTable table) {
        return createRequest(table.getName());
    }

    public void checkArgument(boolean expression) {
        if (!expression) {
            throw new ReconSqlException();
        }
    }

    public void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new ReconSqlException(errorMessage);
        }
    }

    public void checkArgument(boolean expression, String errorMessage, Object... replacement) {
        if (!expression) {
            throw new ReconSqlException(errorMessage, replacement);
        }
    }

    public void handleExceptions(SQLExceptionHandler sqlHandler) {
        try {
            sqlHandler.execute();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

}
