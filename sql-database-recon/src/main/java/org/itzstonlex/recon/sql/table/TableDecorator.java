package org.itzstonlex.recon.sql.table;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlTable;

public class TableDecorator implements ReconSqlTable {

    private final ReconSqlConnection connection;
    private final String name;

    public TableDecorator(ReconSqlConnection connection, String name) {
        this.connection = connection;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReconSqlConnection getConnection() {
        return connection;
    }

}
