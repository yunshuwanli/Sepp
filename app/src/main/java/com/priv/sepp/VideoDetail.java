package com.priv.sepp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import m3u8downloader.M3U8Downloader;
import m3u8downloader.M3U8DownloaderConfig;

public class VideoDetail extends Activity {
    private String encryptKey = "63F06F99D823D33AAB89A0A93DECFEE0";

    class startDownloadListener implements OnClickListener {
        startDownloadListener() {
        }

        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setAction("kankan.download.m3u8");
            Bundle bundle = new Bundle();
            bundle.putString("url", "https://www3.laqddc.com/hls/2018/04/07/BQ2cqpyZ/playlist.m3u8");
            bundle.putBoolean("downloadNow", false);
            bundle.putString("name", "测试影片1");
            bundle.putString("icon", "http://r1.ykimg.com/050C00005B728474ADBAC3391F08F85F?x-oss-process=image/resize,w_290/interlace,1/quality,Q_100/sharpen,100");
            bundle.putBoolean("newdownload", true);
            bundle.putInt("secs", 70);
            intent.putExtras(bundle);
            VideoDetail.this.sendBroadcast(intent);
            intent = new Intent();
            intent.setAction("kankan.download.m3u8");
            bundle = new Bundle();
            bundle.putString("url", "http://hcjs2ra2rytd8v8np1q.exp.bcevod.com/mda-hegtjx8n5e8jt9zv/mda-hegtjx8n5e8jt9zv.m3u8");
            bundle.putBoolean("downloadNow", false);
            bundle.putBoolean("newdownload", true);
            bundle.putString("name", "测试影片2");
            bundle.putInt("secs", 70);
            bundle.putString("icon", "http://ykimg.alicdn.com/develop/image/2019-02-12/f288871cbf8c5f7ffffcc700df7d9f98.jpg?x-oss-process=image/resize,w_290/interlace,1/quality,Q_100/sharpen,100");
            intent.putExtras(bundle);
            VideoDetail.this.sendBroadcast(intent);
        }
    }

    class startDownloadManagerListener implements OnClickListener {
        startDownloadManagerListener() {
        }

        public void onClick(View view) {
            VideoDetail.this.startDownloadManagerActivity();
        }
    }

    class startServiceListener implements OnClickListener {
        startServiceListener() {
        }

        public void onClick(View view) {
            VideoDetail.this.startDownloadService();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_detail);
        findViewById(R.id.startBtn).setOnClickListener(new startServiceListener());
        findViewById(R.id.downloadBtn).setOnClickListener(new startDownloadListener());
        findViewById(R.id.downloadManagerBtn).setOnClickListener(new startDownloadManagerListener());
    }

    private void startDownloadService() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StorageUtils.getCacheDirectory(this).getPath());
        stringBuilder.append("/m3u8Downloader");
        M3U8DownloaderConfig.build(getApplicationContext()).setSaveDir(stringBuilder.toString()).setDebugMode(true);
        M3U8Downloader.getInstance().setEncryptKey(this.encryptKey);
        Intent intent = new Intent();
        intent.setClass(this, DownloadService.class);
        startService(intent);
    }

    private void startDownloadManagerActivity() {
        startActivity(new Intent(this, DownloadManager.class));
    }
}
