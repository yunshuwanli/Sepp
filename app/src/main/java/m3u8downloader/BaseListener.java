package m3u8downloader;

public interface BaseListener {
    void onError(Throwable th);

    void onStart();
}
