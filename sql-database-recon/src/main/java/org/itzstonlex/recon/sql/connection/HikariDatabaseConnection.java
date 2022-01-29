package org.itzstonlex.recon.sql.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.sql.ReconSqlCredentials;
import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.event.ReconSqlEventListener;
import org.itzstonlex.recon.sql.execute.ReconSqlConnectionExecutor;
import org.itzstonlex.recon.sql.table.ReconSqlTableData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HikariDatabaseConnection implements ReconSqlConnection {

    private final ReconSqlCredentials credentials;

    private final Map<String, ReconSqlTable> databaseTables = new HashMap<>();

    private final ReconLog logger = new ReconLog("ReconMySQL");

    private final ExecutorService threadExecutor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconMySQL-%s")
    );

    private Connection connection;

    private ReconSqlExecutable executable;
    private ReconSqlEventListener eventHandler;

    public HikariDatabaseConnection(ReconSqlCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public ReconSqlCredentials getCredentials() {
        return credentials;
    }

    @Override
    public ReconLog getLogger() {
        return logger;
    }

    @Override
    public ExecutorService getThreadExecutor() {
        return threadExecutor;
    }

    @Override
    public void setEventHandler(ReconSqlEventListener eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public ReconSqlTable getTable(String tableName) {
        return databaseTables.get(tableName.toLowerCase());
    }

    @Override
    public ReconSqlEventListener getEventHandler() {
        return eventHandler;
    }

    @Override
    public Map<String, ReconSqlTable> getDatabaseTables() {
        return databaseTables;
    }

    @Override
    public ReconSqlExecutable getExecution() {
        return executable;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s",
                credentials.getHost(), credentials.getPort(), credentials.getScheme()));

        hikariConfig.setUsername(credentials.getUsername());
        hikariConfig.setPassword(credentials.getPassword());

        hikariConfig.addDataSourceProperty("cachePrepStmts" , "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize" , "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");

        try {
            connection = new HikariDataSource(hikariConfig).getConnection();
            executable = new ReconSqlConnectionExecutor(this);

            // Load database tables.
            Objects.requireNonNull(executable.getResponse(true, "SHOW TABLES;"))
                    .thenAccept(result -> {

                        while (result.next()) {

                            String table = result.getString(1);
                            databaseTables.put(table.toLowerCase(), new ReconSqlTableData(this, table));
                        }
                    });

            // Handle event.
            if (eventHandler != null) {
                eventHandler.onConnected(this);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (eventHandler != null) {
            eventHandler.onDisconnect(this);
        }

        threadExecutor.submit(() -> {
            try {
                connection.close();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void reconnect() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(1000)) {
                return;
            }

            connection = null;
            connect();

            if (eventHandler != null) {
                eventHandler.onReconnect(this);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
