package m3u8downloader;

public interface OnDeleteTaskListener extends BaseListener {
    void onFail();

    void onStart();

    void onStartDelete(String str);

    void onSuccess(String str);
}
