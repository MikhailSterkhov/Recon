package org.itzstonlex.recon.http.download;

public interface HttpDownloadHandler {

    void onDownload(long maxKilobytes, long currentKilobytes);
}
