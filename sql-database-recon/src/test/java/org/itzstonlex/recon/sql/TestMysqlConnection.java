package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.MysqlDatabaseConnection;
import org.itzstonlex.recon.sql.event.ReconSqlEventListenerAdapter;
import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

import java.sql.Timestamp;

public class TestMysqlConnection {

    public static final String EMPTY_RESPONSE_DEBUG = "Mysql Response is empty (0)!";

    public static final ReconSqlCredentials DATABASE_CREDENTIALS = ReconSql.getInstance().createCredentials(
            3306, "localhost", "root", "", "test"
    );

    public static void main(String[] args) {
        MysqlDatabaseConnection connection = ReconSql.getInstance().createMysqlConnection(DATABASE_CREDENTIALS);
        connection.setEventHandler(new MysqlEventHandler(connection));

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
        usersTable.selectAll(response -> {

            if (!response.next()) {
                connection.getLogger().info(EMPTY_RESPONSE_DEBUG);
            }
            else {

                int identifier = response.getInt("Id");

                String firstName = response.getString("FirstName");
                String lastName = response.getString("LastName");

                long birthdayAsMillis = response.getTimestamp("Birthday").getTime();

                connection.getLogger().info("Mysql Response find!");
                connection.getLogger().info(" ID: " + identifier);
                connection.getLogger().info(" First name: " + firstName);
                connection.getLogger().info(" Last name: " + lastName);
                connection.getLogger().info(" Birthday Time (ms): " + birthdayAsMillis);
            }
        });

        // Delete a table from database.
        usersTable.drop();
    }

    public static class MysqlEventHandler extends ReconSqlEventListenerAdapter {

        private final MysqlDatabaseConnection connection;

        public MysqlEventHandler(MysqlDatabaseConnection connection) {
            this.connection = connection;
        }

        @Override
        public void onConnected(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info("Success connected to " + connection.getCredentials());
        }

        @Override
        public void onReconnect(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info("Try reconnect to " + connection.getCredentials());
        }

        @Override
        public void onDisconnect(ReconSqlConnection reconSqlConnection) {
            connection.getLogger().info(connection.getCredentials() + " was disconnected");
        }

        @Override
        public void onExecute(ReconSqlConnection reconSqlConnection, String sql) {
            connection.getLogger().info("Request sent: " + sql);
        }
    }
}
