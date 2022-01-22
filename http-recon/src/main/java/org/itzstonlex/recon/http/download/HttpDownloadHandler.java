package org.itzstonlex.recon.http.download;

public interface HttpDownloadHandler {

    void onDownload(HttpDownloadService.HttpDownloadContext downloadContext);
}
