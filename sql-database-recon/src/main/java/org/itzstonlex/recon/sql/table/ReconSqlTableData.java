package org.itzstonlex.recon.sql.table;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlTable;

public class ReconSqlTableData implements ReconSqlTable {

    private final ReconSqlConnection connectionHandler;
    private final String name;

    public ReconSqlTableData(ReconSqlConnection connectionHandler, String name) {
        this.connectionHandler = connectionHandler;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReconSqlConnection getConnectionHandler() {
        return connectionHandler;
    }

}
