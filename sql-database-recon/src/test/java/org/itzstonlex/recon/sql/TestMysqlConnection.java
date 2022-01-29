package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.MysqlDatabaseConnection;
import org.itzstonlex.recon.sql.event.ReconSqlEventListenerAdapter;
import org.itzstonlex.recon.sql.reqest.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.reqest.field.impl.IndexedRequestField;
import org.itzstonlex.recon.sql.reqest.field.impl.ValuedRequestField;

import java.sql.Timestamp;

public class TestMysqlConnection {

    public static final String EMPTY_RESPONSE_DEBUG = "Mysql Response is empty (0)!";

    public static final ReconSqlCredentials DATABASE_CREDENTIALS = ReconSql.getInstance().createCredentials(
            3306, "localhost", "root", "", "test"
    );

    public static void main(String[] args) {
        MysqlDatabaseConnection connection = ReconSql.getInstance().createMysqlConnection(DATABASE_CREDENTIALS);
        connection.setEventHandler(new MysqlEventHandler());

        connection.connect();

        // Get users table
        ReconSqlTable usersTable = connection.createOrGetTable("users", create -> {

            create.push(IndexedRequestField.createPrimaryNotNull(ReconSqlFieldType.INT, "Id")
                    .putIndex(IndexedRequestField.IndexType.AUTO_INCREMENT));

            create.push(IndexedRequestField.createNotNull(ReconSqlFieldType.VAR_CHAR, "FirstName"));
            create.push(IndexedRequestField.createNotNull(ReconSqlFieldType.VAR_CHAR, "LastName"));

            create.push(IndexedRequestField.create(ReconSqlFieldType.TIMESTAMP, "Birthday"));
        });

        // Remove all values in table.
        usersTable.clear();

        // Insert new field to users table.
        usersTable.insert(insert -> {

            insert.push(ValuedRequestField.create("FirstName", "Misha"));
            insert.push(ValuedRequestField.create("LastName", "Leyn"));

            insert.push(ValuedRequestField.create("Birthday", new Timestamp(System.currentTimeMillis())));
        });

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

        @Override
        public void onConnected(ReconSqlConnection connection) {
            connection.getLogger().info("Success connected to " + connection.getCredentials());
        }

        @Override
        public void onReconnect(ReconSqlConnection connection) {
            connection.getLogger().info("Try reconnect to " + connection.getCredentials());
        }

        @Override
        public void onDisconnect(ReconSqlConnection connection) {
            connection.getLogger().info(connection.getCredentials() + " was disconnected");
        }

        @Override
        public void onExecute(ReconSqlConnection connection, String sql) {
            connection.getLogger().info("Request sent: " + sql);
        }
    }
}
