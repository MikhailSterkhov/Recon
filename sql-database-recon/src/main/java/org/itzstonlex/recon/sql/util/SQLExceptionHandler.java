package org.itzstonlex.recon.sql.util;

import java.sql.SQLException;

public interface SQLExceptionHandler {

    void execute() throws SQLException;
}
