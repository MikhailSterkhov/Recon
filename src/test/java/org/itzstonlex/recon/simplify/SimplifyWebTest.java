package org.itzstonlex.recon.simplify;

import org.itzstonlex.recon.util.ReconSimplify;

import java.net.HttpURLConnection;

public class SimplifyWebTest {

    public static final String URL = "https://gitlab.65apps.com/65gb/static/raw/master/testTask.json";

    public static void main(String[] args) {
        ReconSimplify.WEB.executeAsync(URL, "GET", (responseCode, text) -> {

            System.out.println("responseCode = " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                System.out.println(text);
            }
        });
    }

}
