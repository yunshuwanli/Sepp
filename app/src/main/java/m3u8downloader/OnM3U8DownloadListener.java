package m3u8downloader;

import m3u8downloader.bean.M3U8Task;

public abstract class OnM3U8DownloadListener {
    public void onDownloadError(M3U8Task m3U8Task, Throwable th) {
    }

    public void onDownloadItem(M3U8Task m3U8Task, long j, int i, int i2) {
    }

    public void onDownloadPause(M3U8Task m3U8Task) {
    }

    public void onDownloadPending(M3U8Task m3U8Task) {
    }

    public void onDownloadPrepare(M3U8Task m3U8Task) {
    }

    public void onDownloadProgress(M3U8Task m3U8Task) {
    }

    public void onDownloadSuccess(M3U8Task m3U8Task) {
    }

    public void onUpdateProgress(M3U8Task m3U8Task, int i, int i2) {
    }
}
