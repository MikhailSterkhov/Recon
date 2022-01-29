package org.itzstonlex.recon.sql.event;

import org.itzstonlex.recon.sql.ReconSqlConnection;

public interface ReconSqlEventListener {

    void onConnected(ReconSqlConnection connection);

    void onDisconnect(ReconSqlConnection connection);

    void onReconnect(ReconSqlConnection connection);

    void onExecute(ReconSqlConnection connection, String sql);
}
