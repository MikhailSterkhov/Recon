package org.itzstonlex.recon.http.download;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class HttpDownloadService {

    public static HttpDownloadService create() {
        return new HttpDownloadService();
    }

    private final ScheduledExecutorService executor;

    private HttpDownloadService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    private boolean download0(HttpURLConnection urlConnection, Path target, HttpDownloadHandler downloadHandler) {
        try (InputStream inputStream = urlConnection.getInputStream()) {

            ScheduledFuture<?> futureTask = executor.scheduleAtFixedRate(new Runnable() {
                private long previousLength;

                @Override
                public void run() {
                    if (Files.exists(target)) {

                        long current = (target.toFile().length() / 1024);
                        long max = (urlConnection.getContentLengthLong() / 1024);

                        if (current != previousLength) {
                            if (downloadHandler != null) {
                                downloadHandler.onDownload(max, current);
                            }

                            previousLength = current;
                        }
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);

            // Copy file bytes to target path.
            Files.copy(inputStream, target);

            // Cancel download process.
            futureTask.cancel(true);
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public boolean downloadSync(String url, Path target, HttpDownloadHandler downloadHandler) {
        try {
            HttpURLConnection connection = ((HttpURLConnection) new URL(url).openConnection());

            if (connection.getContentLength() <= 0) {
                return false;
            }

            return download0(connection, target, downloadHandler);
        }
        catch (Exception exception) {
            return false;
        }
    }

    public void downloadAsync(String url, Path target, HttpDownloadHandler downloadHandler) {
        executor.submit(() -> downloadSync(url, target, downloadHandler));
    }

    public boolean downloadSync(String url, Path target) {
        return downloadSync(url, target, null);
    }

    public void downloadAsync(String url, Path target) {
        downloadAsync(url, target, null);
    }

}
