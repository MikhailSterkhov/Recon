package org.itzstonlex.recon.sql.exception;

public class ReconSqlException
        extends RuntimeException {

    public ReconSqlException() {
        super();
    }

    public ReconSqlException(String message) {
        super(message);
    }

    public ReconSqlException(String message, Object... replacement) {
        super(String.format(message, replacement));
    }

}
