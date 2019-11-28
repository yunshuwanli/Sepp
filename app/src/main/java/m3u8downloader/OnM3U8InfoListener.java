package m3u8downloader;

import com.m3u8downloader.bean.M3U8;

public interface OnM3U8InfoListener extends BaseListener {
    void onError(Throwable th);

    void onStart();

    void onSuccess(M3U8 m3u8);
}
