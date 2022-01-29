package org.itzstonlex.recon.sql.event;

import org.itzstonlex.recon.sql.ReconSqlConnection;

public abstract class ReconSqlEventListenerAdapter implements ReconSqlEventListener {

    @Override
    public void onConnected(ReconSqlConnection connection) {
        // override me.
    }

    @Override
    public void onReconnect(ReconSqlConnection connection) {
        // override me.
    }

    @Override
    public void onDisconnect(ReconSqlConnection connection) {
        // override me.
    }

    @Override
    public void onExecute(ReconSqlConnection connection, String sql) {
        // override me.
    }
}
