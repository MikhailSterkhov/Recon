package org.itzstonlex.recon.sql.connection;

import com.clickhouse.jdbc.ClickHouseDriver;
import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlCredentials;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.event.ReconSqlEventListener;
import org.itzstonlex.recon.sql.execute.ReconSqlConnectionExecutor;
import org.itzstonlex.recon.sql.table.TableDecorator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ClickHouseDatabaseConnection implements ReconSqlConnection {

    public static final String JDBC_CLICKHOUSE_URL_FORMAT        = ("jdbc:clickhouse://%s:%s/%s");

    private final Map<String, ReconSqlTable> loadedTablesMap = new ConcurrentHashMap<>();

    private final ReconSqlCredentials credentials;
    private final ReconLog logger = new ReconLog("ReconClickHouse");

    private final ExecutorService threadExecutor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconClickHouse-%s")
    );

    private Connection connection;

    private ReconSqlExecutable executable;
    private ReconSqlEventListener eventHandler;

    public ClickHouseDatabaseConnection(ReconSqlCredentials credentials) {
        this.credentials = credentials;
    }

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
        try {
            Class.forName("com.clickhouse.jdbc.ClickHouseDriver");

            connection = DriverManager.getConnection(String.format(JDBC_CLICKHOUSE_URL_FORMAT, credentials.getHost(), credentials.getPort(), credentials.getScheme()),
                    credentials.getUsername(), credentials.getPassword());

            executable = new ReconSqlConnectionExecutor(this);

            // Load database tables.
            Objects.requireNonNull(executable.getResponse(true, "SHOW TABLES;"))
                    .thenAccept(response -> {

                        response.forEachOrdered(value -> {

                            String table = value.getString(1);
                            loadedTablesMap.put(table.toLowerCase(), new TableDecorator(this, table));
                        });
                    });

            // Handle event.
            if (eventHandler != null) {
                eventHandler.onConnected(this);
            }
        }
        catch (Exception exception) {
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
