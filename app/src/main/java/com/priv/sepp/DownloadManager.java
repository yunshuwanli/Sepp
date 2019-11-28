package com.priv.sepp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.priv.sepp.widget.SwipeLayoutManager;

import java.io.File;
import java.util.ArrayList;

import m3u8downloader.M3U8Downloader;
import m3u8downloader.M3U8DownloaderConfig;
import m3u8downloader.OnDeleteTaskListener;
import m3u8downloader.OnM3U8DownloadListener;
import m3u8downloader.bean.M3U8Task;
import m3u8downloader.utils.AES128Utils;
import m3u8downloader.utils.M3U8Log;
import m3u8downloader.utils.MUtils;

public class DownloadManager extends AppCompatActivity {
    private static final String DB_NAME = "CacheDB";
    private static final String[] PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private VideoListAdapter adapter;
    private DownloadDBHelper dbHelper;
    private String dirPath;
    private String encryptKey = "63F06F99D823D33AAB89A0A93DECFEE0";
    private VideoListAdapter.OnDeleteListener onDeleteListener = new VideoListAdapter.OnDeleteListener() {
        public void onDelete(int i) {
            M3U8Task m3U8Task = (M3U8Task) DownloadManager.this.tasklist.get(i);
            Log.e("OnDeleteListener", m3U8Task.getName());
            DownloadManager.this.tasklist.remove(i);
            DownloadManager.this.notifyChanged(m3U8Task);
            M3U8Downloader.getInstance().cancelAndDelete(m3U8Task.getUrl(), DownloadManager.this.onDeleteTaskListener);
        }
    };
    private OnDeleteTaskListener onDeleteTaskListener = new OnDeleteTaskListener() {
        public void onError(Throwable th) {
        }

        public void onFail() {
        }

        public void onStart() {
        }

        public void onStartDelete(String str) {
        }

        public void onSuccess(String str) {
            Log.e("OnDeleteListenerSuccess", str);
            new DownloadDBHelper(MyApplication.getContext(), DownloadManager.DB_NAME, null, 1).deleteMedia(str);
        }
    };
    private OnM3U8DownloadListener onM3U8DownloadListener = new OnM3U8DownloadListener() {
        public void onDownloadItem(M3U8Task m3U8Task, long j, int i, int i2) {
            super.onDownloadItem(m3U8Task, j, i, i2);
        }

        public void onDownloadSuccess(M3U8Task m3U8Task) {
            super.onDownloadSuccess(m3U8Task);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onDownloadPause(M3U8Task m3U8Task) {
            super.onDownloadPause(m3U8Task);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onDownloadPending(M3U8Task m3U8Task) {
            super.onDownloadPending(m3U8Task);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onDownloadProgress(M3U8Task m3U8Task) {
            super.onDownloadProgress(m3U8Task);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onDownloadPrepare(M3U8Task m3U8Task) {
            super.onDownloadPrepare(m3U8Task);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onDownloadError(M3U8Task m3U8Task, Throwable th) {
            super.onDownloadError(m3U8Task, th);
            DownloadManager.this.notifyChanged(m3U8Task);
        }

        public void onUpdateProgress(M3U8Task m3U8Task, int i, int i2) {
            super.onUpdateProgress(m3U8Task, i, i2);
        }
    };
    M3U8Task[] taskList = new M3U8Task[5];
    private ArrayList<M3U8Task> tasklist;

    private void requestAppPermissions() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_download_manager);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("我的下载");
        requestAppPermissions();
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("AES BASE64 Random Key:");
            stringBuilder.append(AES128Utils.getAESKey());
            M3U8Log.d(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dbHelper = new DownloadDBHelper(this, DB_NAME, null, 1);
        initView();
    }

    private void initView() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StorageUtils.getCacheDirectory(this).getPath());
        stringBuilder.append("/m3u8Downloader");
        this.dirPath = stringBuilder.toString();
        M3U8DownloaderConfig.build(getApplicationContext()).setSaveDir(this.dirPath).setDebugMode(true);
        M3U8Downloader.getInstance().setOnM3U8DownloadListener(this.onM3U8DownloadListener);
        M3U8Downloader.getInstance().setEncryptKey(this.encryptKey);
        initData();
        this.adapter = new VideoListAdapter((Context) this, (int) R.layout.list_item, this.tasklist);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 1) {
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            }
        });
        listView.setAdapter(this.adapter);
        listView.setEmptyView(findViewById(R.id.emptyview));
        this.adapter.setOnDeleteListener(this.onDeleteListener);
        this.adapter.setOnItemIconClickListener(new VideoListAdapter.OnItemIconClickListener() {
            public void onItemIconClick(int i) {
                M3U8Task m3U8Task = (M3U8Task) DownloadManager.this.tasklist.get(i);
                String url = m3U8Task.getUrl();
                String name = m3U8Task.getName();
                String icon = m3U8Task.getIcon();
                int secs = m3U8Task.getSecs();
                if (M3U8Downloader.getInstance().checkM3U8IsExist(url)) {
                    Intent intent = new Intent(DownloadManager.this, FullScreenActivity.class);
                    intent.putExtra("M3U8_URL", M3U8Downloader.getInstance().getM3U8Path(url));
                    intent.putExtra("online", false);
                    intent.putExtra("videoname", m3U8Task.getName());
                    DownloadManager.this.startActivity(intent);
                    return;
                }
                Intent intent2 = new Intent();
                intent2.setAction("kankan.download.m3u8");
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putBoolean("downloadNow", true);
                bundle.putBoolean("newdownload", false);
                bundle.putString("name", name);
                bundle.putString("icon", icon);
                bundle.putInt("secs", secs);
                intent2.putExtras(bundle);
                DownloadManager.this.sendBroadcast(intent2);
            }
        });
        findViewById(R.id.clear_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MUtils.clearDir(new File(DownloadManager.this.dirPath));
                DownloadManager.this.adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.startservice_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                DownloadManager.this.startDownloadService();
            }
        });
    }

    private void initData() {
        this.tasklist = new DownloadDBHelper(MyApplication.getContext(), DB_NAME, null, 1).queryAllMedia();
        if (this.tasklist.size() > 0) {
            this.taskList = new M3U8Task[this.tasklist.size()];
            for (int i = 0; i < this.tasklist.size(); i++) {
                M3U8Task m3U8Task = (M3U8Task) this.tasklist.get(i);
                this.taskList[i] = m3U8Task;
                if (!(m3U8Task == null || m3U8Task.getCurTs() == m3U8Task.getTotalTs() || m3U8Task.getTotalTs() <= 0)) {
                    if (M3U8Downloader.getInstance().isCurrentTask(m3U8Task.getUrl())) {
                        m3U8Task.setState(2);
                    } else {
                        m3U8Task.setState(5);
                    }
                    m3U8Task.setProgress((((float) m3U8Task.getCurTs()) * 1.0f) / ((float) m3U8Task.getTotalTs()));
                }
            }
        }
    }

    private void notifyChanged(final M3U8Task m3U8Task) {
        runOnUiThread(new Runnable() {
            public void run() {
                DownloadManager.this.adapter.notifyChanged(DownloadManager.this.tasklist, m3U8Task);
            }
        });
    }

    private void startDownloadService() {
        Intent intent = new Intent();
        intent.setClass(this, DownloadService.class);
        startService(intent);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }
}
