package org.itzstonlex.recon.sql.connection;

import org.itzstonlex.recon.factory.ReconThreadFactory;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlExecutable;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.event.ReconSqlEventListener;
import org.itzstonlex.recon.sql.execute.SqliteWrappedExecutor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SqliteDatabaseConnection implements ReconSqlConnection {

    private final File storage;

    private final Map<String, ReconSqlTable> loadedTablesMap = new ConcurrentHashMap<>();

    private final ReconLog logger = new ReconLog("ReconSqlite");

    private final ExecutorService threadExecutor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconSqlite-%s")
    );

    private Connection connection;

    private ReconSqlExecutable executable;
    private ReconSqlEventListener eventHandler;

    public SqliteDatabaseConnection(File storage) {
        this.storage = storage;
    }

    public File getFileStorage() {
        return storage;
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
            connection = DriverManager.getConnection("jdbc:sqlite:" + storage.getAbsolutePath());
            executable = new SqliteWrappedExecutor(this);

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
