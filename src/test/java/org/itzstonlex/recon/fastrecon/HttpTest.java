package org.itzstonlex.recon.fastrecon;

import org.itzstonlex.recon.util.FastRecon;

public class HttpTest {

    public static final String URL
            = "https://gitlab.65apps.com/65gb/static/raw/master/testTask.json";

    public static void main(String[] args) {
        FastRecon.HTTP.fastHttpConnect(URL, (callback, error) -> {

            if (error != null) {
                error.printStackTrace();
                return;
            }

            System.out.println(callback);
        });
    }

}
