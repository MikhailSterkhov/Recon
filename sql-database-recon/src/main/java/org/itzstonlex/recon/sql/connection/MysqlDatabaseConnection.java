package org.itzstonlex.recon.sql.connection;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.sql.ReconSqlCredentials;
import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.event.ReconSqlEventListener;
import org.itzstonlex.recon.sql.execute.ReconSqlConnectionExecutor;
import org.itzstonlex.recon.sql.table.TableDecorator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MysqlDatabaseConnection implements ReconSqlConnection {

    private final ReconSqlCredentials credentials;

    private final Map<String, ReconSqlTable> loadedTablesMap = new HashMap<>();

    private final ReconLog logger = new ReconLog("ReconMySQL");

    private final ExecutorService threadExecutor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconMySQL-%s")
    );

    private Connection connection;

    private ReconSqlExecutable executable;
    private ReconSqlEventListener eventHandler;

    public MysqlDatabaseConnection(ReconSqlCredentials credentials) {
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
        return loadedTablesMap.get(tableName.toLowerCase());
    }

    @Override
    public ReconSqlEventListener getEventHandler() {
        return eventHandler;
    }

    @Override
    public Map<String, ReconSqlTable> getLoadedTablesMap() {
        return loadedTablesMap;
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
        MysqlDataSource mysqlDataSource = new MysqlDataSource();

        mysqlDataSource.setServerName(credentials.getHost());
        mysqlDataSource.setPort(credentials.getPort());
        mysqlDataSource.setUser(credentials.getUsername());
        mysqlDataSource.setPassword(credentials.getPassword());
        mysqlDataSource.setDatabaseName(credentials.getScheme());

        try {
            mysqlDataSource.setAutoReconnect(true);
            mysqlDataSource.setCharacterEncoding("UTF-8");

            connection = mysqlDataSource.getConnection();
            executable = new ReconSqlConnectionExecutor(this);

            // Load database tables.
            Objects.requireNonNull(executable.getResponse(true, "SHOW TABLES;"))
                    .thenAccept(response -> {

                        while (response.next()) {

                            String table = response.getString(1);
                            loadedTablesMap.put(table.toLowerCase(), new TableDecorator(this, table));
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

            if (eventHandler != null) {
                eventHandler.onReconnect(this);
            }

            connection = null;
            connect();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
