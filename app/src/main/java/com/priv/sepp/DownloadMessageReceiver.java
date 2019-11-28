package com.priv.sepp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import m3u8downloader.M3U8Downloader;
import m3u8downloader.OnM3U8DownloadListener;
import m3u8downloader.bean.M3U8Task;

public class DownloadMessageReceiver extends BroadcastReceiver {
    private static final String TAG = "DownloadMessageReceiver";
    private OnM3U8DownloadListener onM3U8DownloadListener = new OnM3U8DownloadListener() {
        public void onDownloadItem(M3U8Task m3U8Task, long j, int i, int i2) {
            super.onDownloadItem(m3U8Task, j, i, i2);
        }

        public void onDownloadSuccess(M3U8Task m3U8Task) {
            super.onDownloadSuccess(m3U8Task);
            String str = DownloadMessageReceiver.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onDownloadSuccess: filesize=");
            stringBuilder.append(m3U8Task.getFormatTotalSize());
            Log.d(str, stringBuilder.toString());
            new DownloadDBHelper(MyApplication.getContext(), "CacheDB", null, 1).updateMediaSize(m3U8Task.getUrl(), m3U8Task.getFormatTotalSize());
        }

        public void onDownloadPause(M3U8Task m3U8Task) {
            super.onDownloadPause(m3U8Task);
        }

        public void onDownloadPending(M3U8Task m3U8Task) {
            super.onDownloadPending(m3U8Task);
        }

        public void onDownloadProgress(M3U8Task m3U8Task) {
            super.onDownloadProgress(m3U8Task);
        }

        public void onDownloadPrepare(M3U8Task m3U8Task) {
            super.onDownloadPrepare(m3U8Task);
        }

        public void onDownloadError(M3U8Task m3U8Task, Throwable th) {
            super.onDownloadError(m3U8Task, th);
        }

        public void onUpdateProgress(M3U8Task m3U8Task, int i, int i2) {
            super.onUpdateProgress(m3U8Task, i, i2);
            String str = DownloadMessageReceiver.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("onUpdateProgress: curTs=");
            stringBuilder.append(i2);
            stringBuilder.append(" , totalTs=");
            stringBuilder.append(i);
            Log.d(str, stringBuilder.toString());
            new DownloadDBHelper(MyApplication.getContext(), "CacheDB", null, 1).updateMedia(m3U8Task.getUrl(), i, i2);
        }
    };

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String string = extras.getString("url");
        boolean z = extras.getBoolean("downloadNow");
        String string2 = extras.getString("name");
        String string3 = extras.getString("icon");
        boolean z2 = extras.getBoolean("newdownload");
        int i = extras.getInt("secs");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("接收到下载通知Url:");
        stringBuilder.append(string);
        Log.d(str, stringBuilder.toString());
        M3U8Downloader.getInstance().setOnM3U8DownloadProgressListener(this.onM3U8DownloadListener);
        int insertMedia = new DownloadDBHelper(MyApplication.getContext(), "CacheDB", null, 1).insertMedia(string2, string, 0, string3, "", i);
        if (insertMedia > 0) {
            if (insertMedia == 2) {
                if (z2) {
                    Toast.makeText(MyApplication.getContext(), "此影片已在下载列表中，无需重新添加。", Toast.LENGTH_SHORT).show();
                }
            } else if (z2) {
                Toast.makeText(MyApplication.getContext(), "此影片已添加到下载队列中。", Toast.LENGTH_SHORT).show();
            }
            if (z) {
                M3U8Downloader.getInstance().download(string, string2, string3, i);
                return;
            }
            return;
        }
        Toast.makeText(MyApplication.getContext(), "此影片已下载完成，无需重新下载。", 0).show();
    }
}
