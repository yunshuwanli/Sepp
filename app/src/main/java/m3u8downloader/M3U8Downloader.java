package m3u8downloader;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.m3u8downloader.bean.M3U8;
import com.m3u8downloader.bean.M3U8Task;
import com.m3u8downloader.utils.M3U8Log;
import com.m3u8downloader.utils.MUtils;
import java.io.File;
import java.util.List;

public class M3U8Downloader {
    private M3U8Task currentM3U8Task;
    private long currentTime;
    private DownloadQueue downLoadQueue;
    private M3U8DownloadTask m3U8DownLoadTask;
    private OnM3U8DownloadListener onM3U8DownloadListener;
    private OnM3U8DownloadListener onM3U8DownloadProgressListener;
    private OnTaskDownloadListener onTaskDownloadListener;

    private static class SingletonHolder {
        static M3U8Downloader instance = new M3U8Downloader();

        private SingletonHolder() {
        }
    }

    /* synthetic */ M3U8Downloader(AnonymousClass1 anonymousClass1) {
        this();
    }

    private M3U8Downloader() {
        this.onTaskDownloadListener = new OnTaskDownloadListener() {
            private float downloadProgress;
            private long lastLength;

            public void onStartDownload(int i, int i2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onStartDownload: ");
                stringBuilder.append(i);
                stringBuilder.append("|");
                stringBuilder.append(i2);
                M3U8Log.d(stringBuilder.toString());
                M3U8Downloader.this.currentM3U8Task.setState(2);
                this.downloadProgress = (((float) i2) * 1.0f) / ((float) i);
                M3U8Downloader.this.onM3U8DownloadProgressListener;
            }

            public void onDownloading(long j, long j2, int i, int i2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onDownloading: ");
                stringBuilder.append(j);
                stringBuilder.append("|");
                stringBuilder.append(j2);
                stringBuilder.append("|");
                stringBuilder.append(i);
                stringBuilder.append("|");
                stringBuilder.append(i2);
                M3U8Log.d(stringBuilder.toString());
                this.downloadProgress = (((float) i2) * 1.0f) / ((float) i);
                if (M3U8Downloader.this.onM3U8DownloadListener != null) {
                    M3U8Downloader.this.onM3U8DownloadListener.onDownloadItem(M3U8Downloader.this.currentM3U8Task, j2, i, i2);
                }
                if (M3U8Downloader.this.onM3U8DownloadProgressListener != null) {
                    M3U8Downloader.this.onM3U8DownloadProgressListener.onUpdateProgress(M3U8Downloader.this.currentM3U8Task, i, i2);
                }
            }

            public void onSuccess(M3U8 m3u8) {
                M3U8Downloader.this.m3U8DownLoadTask.stop();
                M3U8Downloader.this.currentM3U8Task.setM3U8(m3u8);
                M3U8Downloader.this.currentM3U8Task.setState(3);
                if (M3U8Downloader.this.onM3U8DownloadListener != null) {
                    M3U8Downloader.this.onM3U8DownloadListener.onDownloadSuccess(M3U8Downloader.this.currentM3U8Task);
                }
                if (M3U8Downloader.this.onM3U8DownloadProgressListener != null) {
                    M3U8Downloader.this.onM3U8DownloadProgressListener.onDownloadSuccess(M3U8Downloader.this.currentM3U8Task);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("m3u8 Downloader onSuccess: ");
                stringBuilder.append(m3u8);
                M3U8Log.d(stringBuilder.toString());
                M3U8Downloader.this.downloadNextTask();
            }

            public void onProgress(long j) {
                if (j - this.lastLength > 0) {
                    M3U8Downloader.this.currentM3U8Task.setProgress(this.downloadProgress);
                    M3U8Downloader.this.currentM3U8Task.setSpeed(j - this.lastLength);
                    if (M3U8Downloader.this.onM3U8DownloadListener != null) {
                        M3U8Downloader.this.onM3U8DownloadListener.onDownloadProgress(M3U8Downloader.this.currentM3U8Task);
                    }
                    this.lastLength = j;
                }
            }

            public void onStart() {
                M3U8Downloader.this.currentM3U8Task.setState(1);
                if (M3U8Downloader.this.onM3U8DownloadListener != null) {
                    M3U8Downloader.this.onM3U8DownloadListener.onDownloadPrepare(M3U8Downloader.this.currentM3U8Task);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onDownloadPrepare: ");
                stringBuilder.append(M3U8Downloader.this.currentM3U8Task.getUrl());
                M3U8Log.d(stringBuilder.toString());
            }

            public void onError(Throwable th) {
                if (th.getMessage() == null || !th.getMessage().contains("ENOSPC")) {
                    M3U8Downloader.this.currentM3U8Task.setState(4);
                } else {
                    M3U8Downloader.this.currentM3U8Task.setState(6);
                }
                if (M3U8Downloader.this.onM3U8DownloadListener != null) {
                    M3U8Downloader.this.onM3U8DownloadListener.onDownloadError(M3U8Downloader.this.currentM3U8Task, th);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onError: ");
                stringBuilder.append(th.getMessage());
                M3U8Log.e(stringBuilder.toString());
                M3U8Downloader.this.downloadNextTask();
            }
        };
        this.downLoadQueue = new DownloadQueue();
        this.m3U8DownLoadTask = new M3U8DownloadTask();
    }

    public static M3U8Downloader getInstance() {
        return SingletonHolder.instance;
    }

    private boolean isQuicklyClick() {
        boolean z;
        if (System.currentTimeMillis() - this.currentTime <= 100) {
            z = true;
            M3U8Log.d("is too quickly click!");
        } else {
            z = false;
        }
        this.currentTime = System.currentTimeMillis();
        return z;
    }

    private void downloadNextTask() {
        startDownloadTask(this.downLoadQueue.poll());
    }

    private void pendingTask(M3U8Task m3U8Task) {
        m3U8Task.setState(-1);
        OnM3U8DownloadListener onM3U8DownloadListener = this.onM3U8DownloadListener;
        if (onM3U8DownloadListener != null) {
            onM3U8DownloadListener.onDownloadPending(m3U8Task);
        }
    }

    public void download(String str, String str2, String str3, int i) {
        if (!TextUtils.isEmpty(str) && !isQuicklyClick()) {
            M3U8Task m3U8Task = new M3U8Task(str);
            m3U8Task.setIcon(str3);
            m3U8Task.setName(str2);
            m3U8Task.setSecs(i);
            if (this.downLoadQueue.contains(m3U8Task)) {
                M3U8Task task = this.downLoadQueue.getTask(str);
                if (task.getState() == 5 || task.getState() == 4) {
                    startDownloadTask(task);
                } else {
                    pause(str);
                }
            } else {
                this.downLoadQueue.offer(m3U8Task);
                startDownloadTask(m3U8Task);
            }
        }
    }

    public void pause(String str) {
        if (!TextUtils.isEmpty(str)) {
            M3U8Task task = this.downLoadQueue.getTask(str);
            if (task != null) {
                task.setState(5);
                OnM3U8DownloadListener onM3U8DownloadListener = this.onM3U8DownloadListener;
                if (onM3U8DownloadListener != null) {
                    onM3U8DownloadListener.onDownloadPause(task);
                }
                if (str.equals(this.currentM3U8Task.getUrl())) {
                    this.m3U8DownLoadTask.stop();
                    downloadNextTask();
                } else {
                    this.downLoadQueue.remove(task);
                }
            }
        }
    }

    public void pause(List<String> list) {
        if (list != null && list.size() != 0) {
            Object obj = null;
            for (String str : list) {
                if (this.downLoadQueue.contains(new M3U8Task(str))) {
                    M3U8Task task = this.downLoadQueue.getTask(str);
                    if (task != null) {
                        task.setState(5);
                        OnM3U8DownloadListener onM3U8DownloadListener = this.onM3U8DownloadListener;
                        if (onM3U8DownloadListener != null) {
                            onM3U8DownloadListener.onDownloadPause(task);
                        }
                        if (task.equals(this.currentM3U8Task)) {
                            this.m3U8DownLoadTask.stop();
                            obj = 1;
                        }
                        this.downLoadQueue.remove(task);
                    }
                }
            }
            if (obj != null) {
                startDownloadTask(this.downLoadQueue.peek());
            }
        }
    }

    public boolean checkM3U8IsExist(String str) {
        try {
            return this.m3U8DownLoadTask.getM3u8File(str).exists();
        } catch (Exception e) {
            M3U8Log.e(e.getMessage());
            return false;
        }
    }

    public String getM3U8Path(String str) {
        return this.m3U8DownLoadTask.getM3u8File(str).getPath();
    }

    public boolean isRunning() {
        return this.m3U8DownLoadTask.isRunning();
    }

    public boolean isCurrentTask(String str) {
        return (TextUtils.isEmpty(str) || this.downLoadQueue.peek() == null || !this.downLoadQueue.peek().getUrl().equals(str)) ? false : true;
    }

    public void setOnM3U8DownloadListener(OnM3U8DownloadListener onM3U8DownloadListener) {
        this.onM3U8DownloadListener = onM3U8DownloadListener;
    }

    public void setOnM3U8DownloadProgressListener(OnM3U8DownloadListener onM3U8DownloadListener) {
        this.onM3U8DownloadProgressListener = onM3U8DownloadListener;
    }

    public void setEncryptKey(String str) {
        this.m3U8DownLoadTask.setEncryptKey(str);
    }

    public String getEncryptKey() {
        return this.m3U8DownLoadTask.getEncryptKey();
    }

    private void startDownloadTask(M3U8Task m3U8Task) {
        if (m3U8Task != null) {
            pendingTask(m3U8Task);
            StringBuilder stringBuilder;
            if (!this.downLoadQueue.isHead(m3U8Task)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("start download task, but task is running: ");
                stringBuilder.append(m3U8Task.getUrl());
                M3U8Log.d(stringBuilder.toString());
            } else if (m3U8Task.getState() == 5) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("start download task, but task has pause: ");
                stringBuilder.append(m3U8Task.getUrl());
                M3U8Log.d(stringBuilder.toString());
            } else {
                try {
                    this.currentM3U8Task = m3U8Task;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("====== start downloading ===== ");
                    stringBuilder.append(m3U8Task.getUrl());
                    M3U8Log.d(stringBuilder.toString());
                    this.m3U8DownLoadTask.download(m3U8Task.getUrl(), this.onTaskDownloadListener);
                } catch (Exception e) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("startDownloadTask Error:");
                    stringBuilder.append(e.getMessage());
                    M3U8Log.e(stringBuilder.toString());
                }
            }
        }
    }

    public void cancel(String str) {
        pause(str);
    }

    public void cancel(List<String> list) {
        pause((List) list);
    }

    public void cancelAndDelete(final String str, @Nullable final OnDeleteTaskListener onDeleteTaskListener) {
        pause(str);
        this.downLoadQueue.remove(this.downLoadQueue.getTask(str));
        if (onDeleteTaskListener != null) {
            onDeleteTaskListener.onStart();
            onDeleteTaskListener.onStartDelete(str);
        }
        new Thread(new Runnable() {
            public void run() {
                boolean clearDir = MUtils.clearDir(new File(MUtils.getSaveFileDir(str)));
                OnDeleteTaskListener onDeleteTaskListener = onDeleteTaskListener;
                if (onDeleteTaskListener == null) {
                    return;
                }
                if (clearDir) {
                    onDeleteTaskListener.onSuccess(str);
                } else {
                    onDeleteTaskListener.onFail();
                }
            }
        }).start();
    }

    public void cancelAndDelete(final List<String> list, @Nullable final OnDeleteTaskListener onDeleteTaskListener) {
        pause((List) list);
        if (onDeleteTaskListener != null) {
            onDeleteTaskListener.onStart();
        }
        new Thread(new Runnable() {
            public void run() {
                Object obj = 1;
                for (String saveFileDir : list) {
                    obj = (obj == null || !MUtils.clearDir(new File(MUtils.getSaveFileDir(saveFileDir)))) ? null : 1;
                }
                OnDeleteTaskListener onDeleteTaskListener = onDeleteTaskListener;
                if (onDeleteTaskListener == null) {
                    return;
                }
                if (obj != null) {
                    onDeleteTaskListener.onSuccess(null);
                } else {
                    onDeleteTaskListener.onFail();
                }
            }
        }).start();
    }
}
