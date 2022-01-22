package org.itzstonlex.recon.http.download;

import java.io.IOException;
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

    private ScheduledFuture<?> scheduleFutureTask(long contentLength, Path target, HttpDownloadHandler downloadHandler) {
        return executor.scheduleAtFixedRate(new Runnable() {

            private final HttpDownloadContext context = new HttpDownloadContext(contentLength / 1024);
            private long previousLength;

            @Override
            public void run() {
                context.setCurrentKilobytes(target.toFile().length() / 1024);

                if (Files.exists(target)) {

                    if (context.currentKilobytes != previousLength) {
                        previousLength = context.currentKilobytes;

                        if (downloadHandler != null) {
                            downloadHandler.onDownload(context);
                        }
                    }

                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private boolean download0(HttpURLConnection http, Path target, HttpDownloadHandler downloadHandler) {
        ScheduledFuture<?> futureTask = this.scheduleFutureTask(http.getContentLengthLong(), target, downloadHandler);

        // Copy file bytes to target path.
        try (InputStream inputStream = http.getInputStream()) {
            Files.copy(inputStream, target);

            return true;
        }
        catch (IOException exception) {
            return false;
        }
        finally {

            // Close http connection.
            http.disconnect();

            // Cancel download process.
            futureTask.cancel(true);
        }
    }

    public boolean downloadSync(String url, Path target, HttpDownloadHandler downloadHandler) {
        try {
            // Cleanup file.
            if (Files.exists(target)) {
                Files.deleteIfExists(target);
            }

            // Connect to url content.
            HttpURLConnection http = ((HttpURLConnection) new URL(url).openConnection());
            if (http.getContentLength() <= 0) {
                return false;
            }

            // Start download process.
            return download0(http, target, downloadHandler);
        }
        catch (IOException exception) {
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

    // Http file download Context.
    public static class HttpDownloadContext {
        private final long maxKilobytes;

        private long currentKilobytes;
        private long speed;

        private HttpDownloadContext(long maxKilobytes) {
            this.maxKilobytes = maxKilobytes;
        }

        public long getCurrentKilobytes() {
            return currentKilobytes;
        }

        public long getMaxKilobytes() {
            return maxKilobytes;
        }

        public long getSpeed() {
            return speed;
        }

        void setCurrentKilobytes(long currentKilobytes) {
            this.speed = currentKilobytes - this.currentKilobytes;
            this.currentKilobytes = currentKilobytes;
        }
    }

}
