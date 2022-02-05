package org.itzstonlex.recon.sql;

import org.itzstonlex.recon.sql.connection.HikariDatabaseConnection;
import org.itzstonlex.recon.sql.objects.SqlObjectDescription;
import org.itzstonlex.recon.sql.objects.SqlObjectWorker;
import org.itzstonlex.recon.sql.objects.annotation.FieldSql;
import org.itzstonlex.recon.sql.objects.annotation.InjectionSql;

public class TestObjectWorker {

    public static final String H2_DRIVER_CLASSNAME  = ("org.h2.Driver");
    public static final String H2_DRIVER_URL        = ("jdbc:h2:mem:test");

    public static void main(String[] args) {
        HikariDatabaseConnection connection = ReconSql.getInstance().createHikariConnection(
                H2_DRIVER_CLASSNAME, H2_DRIVER_URL, "root", ""
        );

        connection.connect();

        User user = new User("Misha Leyn", 18);

        SqlObjectWorker worker = ReconSql.getInstance().newObjectWorker(connection);
        SqlObjectDescription sqlUser = worker.createDescription(user);

        if (sqlUser != null) {
            if (!worker.exists(sqlUser)) {
                worker.create(sqlUser);
            }

            int userID = worker.executeWithResponse(sqlUser, "SELECT * FROM ${rtable} WHERE `name`=${name}")
                    .thenApply(response -> response.next() ? response.getInt("id") : 0).join();

            System.out.println("User ID: " + userID);
        }
        else {
            System.out.println("Cannot a find annotation @InjectionSql in User class.");
        }

        connection.getTable("users").selectAll(response -> {

            while (response.next()) {
                System.out.println("-------------------------------------------");
                System.out.println("ID: " + response.getInt("id") + " | Name: " + response.getString("name") + " | Age: " + response.getInt("age"));
            }
        });
    }

    @InjectionSql(table = "users")
    public static class User {

        @FieldSql(name = "name")
        private final String username;

        @FieldSql
        private int age;

        private User(String name, int age) {
            this.username = name;
            this.age = age;
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
}
