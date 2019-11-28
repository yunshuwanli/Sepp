package m3u8downloader;

import com.m3u8downloader.bean.M3U8;

interface OnTaskDownloadListener extends BaseListener {
    void onDownloading(long j, long j2, int i, int i2);

    void onError(Throwable th);

    void onProgress(long j);

    void onStart();

    void onStartDownload(int i, int i2);

    void onSuccess(M3U8 m3u8);
}
