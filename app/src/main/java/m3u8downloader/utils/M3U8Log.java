package m3u8downloader.utils;

import android.util.Log;
import com.m3u8downloader.M3U8DownloaderConfig;

public class M3U8Log {
    private static String TAG = "M3U8Log";

    public static void d(String str) {
        if (M3U8DownloaderConfig.isDebugMode()) {
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Thread.currentThread());
            stringBuilder.append(str);
            Log.d(str2, stringBuilder.toString());
        }
    }

    public static void e(String str) {
        if (M3U8DownloaderConfig.isDebugMode()) {
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Thread.currentThread());
            stringBuilder.append(str);
            Log.e(str2, stringBuilder.toString());
        }
    }
}
