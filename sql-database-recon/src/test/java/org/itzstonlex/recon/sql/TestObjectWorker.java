package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.HikariDatabaseConnection;
import org.itzstonlex.recon.sql.objects.SqlObjectDescription;
import org.itzstonlex.recon.sql.objects.SqlObjectWorker;
import org.itzstonlex.recon.sql.objects.annotation.FieldSql;
import org.itzstonlex.recon.sql.objects.annotation.InjectionSql;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.request.field.impl.ValuedField;

public class TestObjectWorker {

    @SuppressWarnings("unused")
    @InjectionSql(table = "users")
    public static class User {

        @FieldSql // For objects storage tests.
        private final User currentUser;

        @FieldSql(name = "name", indexes = IndexedField.IndexType.NOT_NULL)
        private final String username;

        @FieldSql(indexes = IndexedField.IndexType.NOT_NULL)
        private int age;

        private User(String name, int age) {
            this.username = name;
            this.age = age;

            this.currentUser = this;
        }

        public String getName() {
            return username;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static final String H2_DRIVER_CLASSNAME  = ("org.h2.Driver");
    public static final String H2_DRIVER_URL        = ("jdbc:h2:mem:test");

    public static void main(String[] args) {
        HikariDatabaseConnection connection = ReconSql.getInstance().createHikariConnection(
                H2_DRIVER_CLASSNAME, H2_DRIVER_URL, "root", ""
        );

        connection.connect();

        // Create object to request's parsing.
        User user = new User("Misha Leyn", 18);

        // Create & Inject sql-object services.
        SqlObjectWorker worker = ReconSql.getInstance().newObjectWorker(connection);
        SqlObjectDescription<User> sqlUser = worker.injectObject(user);

        // Insert user object to database and getting a user id.
        if (sqlUser != null) {
            if (!worker.contains(sqlUser)) {
                worker.insert(sqlUser);
            }

            int userID = worker.getID(sqlUser);
            System.out.println("User ID: " + userID);
        }
        else {
            System.out.println("Cannot a find annotation @InjectionSql in User class.");
        }

        // Check `users` table content.
        connection.getTable("users").selectAll().thenAccept(response -> {
            System.out.println("-------------------------------------------");

            while (response.next()) {
                int userID = response.getInt("id");
                int userAge = response.getInt("age");

                String username = response.getString("name");

                System.out.println("ID: " + userID + " | Name: " + username + " | Age: " + userAge);
            }
        });

        // Try to change user-object a fields values.
        user.setAge(25);

        if (sqlUser != null) {
            sqlUser.reinject(worker);

            // Execute UPDATE request to database-server.
            worker.update(sqlUser);
        }

        // Check `users` table content.
        connection.getTable("users").selectWhere(ValuedField.create("name", user.username))
                        .thenAccept(response -> {

            if (!response.next()) {
                return;
            }

            int userID = response.getInt("id");
            int userAge = response.getInt("age");

            String username = response.getString("name");

            System.out.println("-------------------------------------------");
            System.out.println("ID: " + userID + " | Name: " + username + " | Age: " + userAge);
            System.out.println("User: " + response.getJsonObject("currentUser", User.class).getName());
        });

        System.out.println("-------------------------------------------");
        System.out.println("Table size: " + connection.getTable("users").requestCount());
    }

}
