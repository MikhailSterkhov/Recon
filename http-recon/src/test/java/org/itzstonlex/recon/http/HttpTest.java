package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.util.HttpParameters;

public class HttpTest {

    public static final String URL
            = "https://gitlab.65apps.com/65gb/static/raw/master/testTask.json";

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient();
        HttpResponse httpResponse = httpClient.executeGet(URL);

        System.out.println("status-code: " + httpResponse.getStatusCode());

        Throwable error = httpResponse.getError();
        if (error != null) {
            error.printStackTrace();
        }

        // OK Response handle.
        System.out.println("callback: " + httpResponse.getCallback());


        String someURL = HttpParameters.create("http://SOME-URL/")
                .addParameter("example_access_token", "example_01928374fhdj29jcmfsdl9ehl")
                .addParameter("author", "ItzStonlex")
                .appendParameters();
    }

}
