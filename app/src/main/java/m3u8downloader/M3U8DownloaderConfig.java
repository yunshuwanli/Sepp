package m3u8downloader;

import android.content.Context;
import android.os.Environment;
import com.m3u8downloader.utils.SPHelper;
import java.io.File;

public class M3U8DownloaderConfig {
    private static final String TAG_CONN_TIMEOUT = "TAG_CONN_TIMEOUT_M3U8";
    private static final String TAG_DEBUG = "TAG_DEBUG_M3U8";
    private static final String TAG_READ_TIMEOUT = "TAG_READ_TIMEOUT_M3U8";
    private static final String TAG_SAVE_DIR = "TAG_SAVE_DIR_M3U8";
    private static final String TAG_THREAD_COUNT = "TAG_THREAD_COUNT_M3U8";

    public static M3U8DownloaderConfig build(Context context) {
        SPHelper.init(context);
        return new M3U8DownloaderConfig();
    }

    public M3U8DownloaderConfig setSaveDir(String str) {
        SPHelper.putString(TAG_SAVE_DIR, str);
        return this;
    }

    public static String getSaveDir() {
        String str = TAG_SAVE_DIR;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
        stringBuilder.append(File.separator);
        stringBuilder.append("M3u8Downloader");
        return SPHelper.getString(str, stringBuilder.toString());
    }

    public M3U8DownloaderConfig setThreadCount(int i) {
        if (i > 5) {
            i = 5;
        }
        if (i <= 0) {
            i = 1;
        }
        SPHelper.putInt(TAG_THREAD_COUNT, i);
        return this;
    }

    public static int getThreadCount() {
        return SPHelper.getInt(TAG_THREAD_COUNT, 3);
    }

    public M3U8DownloaderConfig setConnTimeout(int i) {
        SPHelper.putInt(TAG_CONN_TIMEOUT, i);
        return this;
    }

    public static int getConnTimeout() {
        return SPHelper.getInt(TAG_CONN_TIMEOUT, 10000);
    }

    public M3U8DownloaderConfig setReadTimeout(int i) {
        SPHelper.putInt(TAG_READ_TIMEOUT, i);
        return this;
    }

    public static int getReadTimeout() {
        return SPHelper.getInt(TAG_READ_TIMEOUT, 1800000);
    }

    public M3U8DownloaderConfig setDebugMode(boolean z) {
        SPHelper.putBoolean(TAG_DEBUG, z);
        return this;
    }

    public static boolean isDebugMode() {
        return SPHelper.getBoolean(TAG_DEBUG, false);
    }
}
