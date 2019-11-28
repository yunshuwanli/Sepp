package com.priv.sepp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.File;

public class DownloadService extends Service {
    protected static final String TAG = "DownloadService::";
    protected static final String fileDownloadPath = "sunrise/download/";
    protected static final String fileRootPath;
    protected static Builder mBuilder;
    protected static NotificationManager mNotifyManager;
    protected static final int notifiID = 0;
    protected File downloaddir;
    protected File downloadfile;
    protected File downloadfiletemp;
    protected int fileCache;
    protected String fileName = "";
    protected String fileNametemp = "";
    protected int fileSize;
    private DownloadMessageReceiver receiver;
    protected String urlStr = "";

    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory());
        stringBuilder.append(File.separator);
        fileRootPath = stringBuilder.toString();
    }

    public void onCreate() {
        super.onCreate();
        this.receiver = new DownloadMessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("kankan.download.m3u8");
        registerReceiver(this.receiver, intentFilter);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, i, i2);
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    protected void DownloadFile(String str) {
        Log.e(TAG, "DownloadFile");
    }

    public void installApp(Context context, String str) {
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
