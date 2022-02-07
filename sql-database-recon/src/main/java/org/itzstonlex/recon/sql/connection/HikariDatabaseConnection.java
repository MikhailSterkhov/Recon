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
import org.itzstonlex.recon.sql.table.TableDecorator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HikariDatabaseConnection implements ReconSqlConnection {

    public static final String JDBC_MYSQL_DRIVER_CLASSNAME  = ("com.mysql.jdbc.Driver");
    public static final String JDBC_MYSQL_URL_FORMAT        = ("jdbc:mysql://%s:%s/%s");

    private final String driverClassname,
            driverUrl,
            username,
            password;

    private final Map<String, ReconSqlTable> loadedTablesMap = new HashMap<>();

    private final ReconLog logger = new ReconLog("ReconMySQL");

    private final ExecutorService threadExecutor = Executors.newCachedThreadPool(
            ReconThreadFactory.asInstance("ReconMySQL-%s")
    );

    private Connection connection;

    private ReconSqlExecutable executable;
    private ReconSqlEventListener eventHandler;

    public HikariDatabaseConnection(String driverClassname, String driverUrl, String username, String password) {
        this.driverClassname = driverClassname;
        this.driverUrl = driverUrl;

        this.username = username;
        this.password = password;
    }

    public HikariDatabaseConnection(String driverClassname, ReconSqlCredentials credentials) {
        this(driverClassname, String.format(JDBC_MYSQL_URL_FORMAT, credentials.getHost(), credentials.getPort(), credentials.getScheme()),
                credentials.getUsername(), credentials.getPassword());
    }

    public HikariDatabaseConnection(ReconSqlCredentials credentials) {
        this(JDBC_MYSQL_DRIVER_CLASSNAME, credentials);
    }

    public String getDriverUrl() {
        return driverUrl;
    }

    public String getDriverClassname() {
        return driverClassname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(driverClassname);
        hikariConfig.setJdbcUrl(driverUrl);

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("cachePrepStmts" , "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize" , "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");

        try {
            connection = new HikariDataSource(hikariConfig).getConnection();
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
