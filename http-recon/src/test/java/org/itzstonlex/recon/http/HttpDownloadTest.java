package org.itzstonlex.recon.http;

import org.itzstonlex.recon.http.download.HttpDownloadService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpDownloadTest {

    public static final HttpDownloadService HTTP_DOWNLOAD_SERVICE = HttpDownloadService.create();

    public static final String BUNGEECORD_REPOSITORY    = "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar";
    public static final String COMPILED_TARGET_NAME     = "compiled-bungeecord.jar";

    public static final String DOWNLOAD_PROCESS_FORMAT = "Download: [%s/%s KB]\n";

    private static Path createTargetPath() throws IOException {
        Path target = new File("").toPath();

        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        target = target.resolve(COMPILED_TARGET_NAME);
        Files.deleteIfExists(target);

        return target;
    }

    private static void startDownload(Path target) {
        boolean isSuccess = HTTP_DOWNLOAD_SERVICE.downloadSync(BUNGEECORD_REPOSITORY, target,
                (maxKilobytes, currentKilobytes) -> System.out.printf(DOWNLOAD_PROCESS_FORMAT, currentKilobytes, maxKilobytes));

        System.out.println("Download Complete Status: " + (isSuccess ? "[Success]" : "[Error]"));
        System.out.println(target.toAbsolutePath().toString());
    }


    public static void main(String[] args) throws IOException {

        // Getting download target.
        Path target = createTargetPath();

        // Start sync download.
        startDownload(target);
    }

}
