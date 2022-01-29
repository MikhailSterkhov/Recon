package org.itzstonlex.recon.sql;

public final class ReconSqlCredentials {

    private int port;

    private String host;

    private String username;

    private String password;

    private String scheme;

    ReconSqlCredentials(int port, String host, String username, String password, String scheme) {
        this.port = port;
        this.host = host;
        this.username = username;
        this.password = password;
        this.scheme = scheme;
    }

    ReconSqlCredentials(String host, String username, String password, String scheme) {
        this(3306, host, username, password, scheme);
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getScheme() {
        return scheme;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String toString() {
        return String.format("%s@%s (%s)", host, username, scheme);
    }
}
