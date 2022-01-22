package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.ftp.HttpFTPClient;

public class HttpFTPTest {

    public static final HttpFTPClient FTP_CLIENT = new HttpFTPClient();

    public static final String FTP_FILEPATH = ("/home/user/app");

    public static final String FTP_HOST     = System.getProperty("$_FTP_HOST");
    public static final String FTP_PORT     = System.getProperty("$_FTP_PORT");
    public static final String FTP_USER     = System.getProperty("$_FTP_USERNAME");
    public static final String FTP_PASS     = System.getProperty("$_FTP_PASSWORD");


    public static void main(String[] args) {
        boolean isConnected = FTP_CLIENT.connect(FTP_HOST, Integer.parseInt(FTP_PORT), FTP_USER, FTP_PASS);

        if (isConnected) {
            System.out.println("Success connected to " + FTP_CLIENT.getInetSocketAddress());

            testDownload();
            testUpload();

        } else {

            System.out.println("Could not connect to " + FTP_CLIENT.getInetSocketAddress(false));
        }
    }

    public static void testDownload() {
        boolean downloadStatus = FTP_CLIENT.downloadFile(FTP_FILEPATH + "/launcher.jar", "/target-compiled");

        System.out.println("Download status: " + (downloadStatus ? "[Success]" : "[Error]"));
    }

    public static void testUpload() {
        boolean uploadStatus = FTP_CLIENT.uploadFile("/target-compiled", FTP_FILEPATH);

        System.out.println("Upload status: " + (uploadStatus ? "[Success]" : "[Error]"));
    }

}
