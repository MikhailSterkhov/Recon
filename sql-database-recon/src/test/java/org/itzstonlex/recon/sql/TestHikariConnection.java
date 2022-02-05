package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.HikariDatabaseConnection;
import org.itzstonlex.recon.sql.event.ReconSqlEventListenerAdapter;
import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.sql.Timestamp;

public class TestHikariConnection {

    public static final String EMPTY_RESPONSE_DEBUG = ("Hikari Response is empty (0)!");

    public static final String H2_DRIVER_CLASSNAME  = ("org.h2.Driver");
    public static final String H2_DRIVER_URL        = ("jdbc:h2:mem:test");

    public static void main(String[] args) {
        HikariDatabaseConnection connection = ReconSql.getInstance().createHikariConnection(
                H2_DRIVER_CLASSNAME, H2_DRIVER_URL, "root", ""
        );

        connection.setEventHandler(new HikariEventHandler(connection));
        connection.connect();

        // Get users table
        ReconSqlTable usersTable = connection.createOrGetTable("users", create -> {

            create.push(IndexedField.createPrimaryNotNull(ReconSqlFieldType.INT, "Id")
                    .index(IndexedField.IndexType.AUTO_INCREMENT));

            create.push(IndexedField.createNotNull(ReconSqlFieldType.VAR_CHAR, "FirstName"));
            create.push(IndexedField.createNotNull(ReconSqlFieldType.VAR_CHAR, "LastName"));

            create.push(IndexedField.create(ReconSqlFieldType.TIMESTAMP, "Birthday"));
        });

        // Remove all values in table.
        usersTable.clear();

        // Insert new field to users table.
        usersTable.createRequest()
                .insert()

                .push(ValuedField.create("FirstName", "Misha"))
                .push(ValuedField.create("LastName", "Leyn"))

                .push(ValuedField.create("Birthday", new Timestamp(System.currentTimeMillis())))
                .updateSync(connection);

        // Getting a first table response field.
        usersTable.selectAll().thenAccept(response -> {

            if (!response.next()) {
                connection.getLogger().info(EMPTY_RESPONSE_DEBUG);
            }
            else {

                int identifier = response.getInt("Id");

                String firstName = response.getString("FirstName");
                String lastName = response.getString("LastName");

                long birthdayAsMillis = response.getTimestamp("Birthday").getTime();

                connection.getLogger().info("Hikari Response find!");
                connection.getLogger().info(" ID: " + identifier);
                connection.getLogger().info(" First name: " + firstName);
                connection.getLogger().info(" Last name: " + lastName);
                connection.getLogger().info(" Birthday Time (ms): " + birthdayAsMillis);
            }
        });
    }

    public static class HikariEventHandler extends ReconSqlEventListenerAdapter {

        private final HikariDatabaseConnection connection;

        public HikariEventHandler(HikariDatabaseConnection connection) {
            this.connection = connection;
        }

        @Override
        public void onConnected(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info("Success connected to " + connection.getDriverUrl());
        }

        @Override
        public void onReconnect(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info("Try reconnect to " + connection.getDriverUrl());
        }

        @Override
        public void onDisconnect(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info(connection.getDriverUrl() + " was disconnected");
        }

        @Override
        public void onExecute(ReconSqlConnection reconSqlConnection, String sql) {
            connection.getLogger().info("Request sent: " + sql);
        }
    }
}
