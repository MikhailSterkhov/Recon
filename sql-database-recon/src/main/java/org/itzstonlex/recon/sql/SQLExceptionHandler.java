package org.itzstonlex.recon.sql;

import java.sql.SQLException;

public interface SQLExceptionHandler {

    void handle() throws SQLException;
}
