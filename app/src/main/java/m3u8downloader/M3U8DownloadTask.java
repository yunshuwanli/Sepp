package m3u8downloader;

import android.os.Handler.Callback;
import android.os.Message;
import com.m3u8downloader.bean.M3U8;
import com.m3u8downloader.bean.M3U8Ts;
import com.m3u8downloader.utils.M3U8Log;
import com.m3u8downloader.utils.MUtils;
import java.io.File;
import java.io.InterruptedIOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class M3U8DownloadTask {
    private static final int WHAT_ON_ERROR = 1001;
    private static final int WHAT_ON_PROGRESS = 1002;
    private static final int WHAT_ON_START_DOWNLOAD = 1004;
    private static final int WHAT_ON_SUCCESS = 1003;
    private int connTimeout;
    private long curLength;
    private volatile int curTs;
    private M3U8 currentM3U8;
    private String encryptKey;
    private ExecutorService executor;
    private boolean isRunning;
    private volatile boolean isStartDownload;
    private volatile long itemFileSize;
    private String m3u8FileName;
    private WeakHandler mHandler;
    private Timer netSpeedTimer;
    private OnTaskDownloadListener onTaskDownloadListener;
    private int readTimeout;
    private String saveDir;
    private int threadCount;
    private volatile long totalFileSize;
    private volatile int totalTs;

    public M3U8DownloadTask() {
        this.encryptKey = null;
        this.m3u8FileName = "local.m3u8";
        this.curTs = 0;
        this.totalTs = 0;
        this.itemFileSize = 0;
        this.totalFileSize = 0;
        this.isStartDownload = true;
        this.curLength = 0;
        this.isRunning = false;
        this.threadCount = 3;
        this.readTimeout = 1800000;
        this.connTimeout = 10000;
        this.mHandler = new WeakHandler(new Callback() {
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 1001:
                        M3U8DownloadTask.this.onTaskDownloadListener.onError((Throwable) message.obj);
                        break;
                    case 1002:
                        M3U8DownloadTask.this.onTaskDownloadListener.onDownloading(M3U8DownloadTask.this.totalFileSize, M3U8DownloadTask.this.itemFileSize, M3U8DownloadTask.this.totalTs, M3U8DownloadTask.this.curTs);
                        break;
                    case 1003:
                        if (M3U8DownloadTask.this.netSpeedTimer != null) {
                            M3U8DownloadTask.this.netSpeedTimer.cancel();
                        }
                        M3U8DownloadTask.this.onTaskDownloadListener.onSuccess(M3U8DownloadTask.this.currentM3U8);
                        break;
                    case 1004:
                        M3U8DownloadTask.this.onTaskDownloadListener.onStartDownload(M3U8DownloadTask.this.totalTs, M3U8DownloadTask.this.curTs);
                        break;
                }
                return true;
            }
        });
        this.connTimeout = M3U8DownloaderConfig.getConnTimeout();
        this.readTimeout = M3U8DownloaderConfig.getReadTimeout();
        this.threadCount = M3U8DownloaderConfig.getThreadCount();
    }

    public void download(String str, OnTaskDownloadListener onTaskDownloadListener) {
        this.saveDir = MUtils.getSaveFileDir(str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("start download ,SaveDir: ");
        stringBuilder.append(this.saveDir);
        M3U8Log.d(stringBuilder.toString());
        this.onTaskDownloadListener = onTaskDownloadListener;
        if (isRunning()) {
            handlerError(new Throwable("Task running"));
        } else {
            getM3U8Info(str);
        }
    }

    public void setEncryptKey(String str) {
        this.encryptKey = str;
    }

    public String getEncryptKey() {
        return this.encryptKey;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    private void getM3U8Info(String str) {
        M3U8InfoManger.getInstance().getM3U8Info(str, new OnM3U8InfoListener() {
            public void onSuccess(final M3U8 m3u8) {
                M3U8DownloadTask.this.currentM3U8 = m3u8;
                new Thread() {
                    public void run() {
                        try {
                            M3U8DownloadTask.this.startDownload(m3u8);
                            if (M3U8DownloadTask.this.executor != null) {
                                M3U8DownloadTask.this.executor.shutdown();
                            }
                            while (M3U8DownloadTask.this.executor != null && !M3U8DownloadTask.this.executor.isTerminated()) {
                                Thread.sleep(100);
                            }
                            if (M3U8DownloadTask.this.isRunning) {
                                M3U8DownloadTask.this.currentM3U8.setM3u8FilePath(MUtils.createLocalM3U8(new File(M3U8DownloadTask.this.saveDir), M3U8DownloadTask.this.m3u8FileName, M3U8DownloadTask.this.currentM3U8).getPath());
                                M3U8DownloadTask.this.currentM3U8.setDirFilePath(M3U8DownloadTask.this.saveDir);
                                M3U8DownloadTask.this.currentM3U8.getFileSize();
                                M3U8DownloadTask.this.mHandler.sendEmptyMessage(1003);
                                M3U8DownloadTask.this.isRunning = false;
                            }
                        } catch (InterruptedIOException unused) {
                        } catch (Throwable e) {
                            M3U8DownloadTask.this.handlerError(e);
                        } catch (Throwable e2) {
                            M3U8DownloadTask.this.handlerError(e2);
                        } catch (Throwable e22) {
                            M3U8DownloadTask.this.handlerError(e22);
                        }
                    }
                }.start();
            }

            public void onStart() {
                M3U8DownloadTask.this.onTaskDownloadListener.onStart();
            }

            public void onError(Throwable th) {
                M3U8DownloadTask.this.handlerError(th);
            }
        });
    }

    private void startDownload(M3U8 m3u8) {
        File file = new File(this.saveDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.totalTs = m3u8.getTsList().size();
        ExecutorService executorService = this.executor;
        if (executorService != null) {
            executorService.shutdownNow();
        }
        M3U8Log.d("executor is shutDown ! Downloading !");
        this.curTs = 0;
        this.isRunning = true;
        this.isStartDownload = true;
        this.executor = null;
        ReentrantLock reentrantLock = new ReentrantLock();
        this.executor = Executors.newFixedThreadPool(this.threadCount);
        String basePath = m3u8.getBasePath();
        String domain = m3u8.getDomain();
        this.netSpeedTimer = new Timer();
        this.netSpeedTimer.schedule(new TimerTask() {
            public void run() {
                M3U8DownloadTask.this.onTaskDownloadListener.onProgress(M3U8DownloadTask.this.curLength);
            }
        }, 0, 1000);
        for (final M3U8Ts m3U8Ts : m3u8.getTsList()) {
            final File file2 = file;
            final String str = basePath;
            final String str2 = domain;
            final ReentrantLock reentrantLock2 = reentrantLock;
            this.executor.execute(new Runnable() {
                /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
                    jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.m3u8downloader.M3U8DownloadTask.4.run():void, dom blocks: []
                    	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
                    	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
                    	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
                    	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
                    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
                    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
                    	at java.util.ArrayList.forEach(Unknown Source)
                    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
                    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
                    	at java.util.ArrayList.forEach(Unknown Source)
                    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
                    	at jadx.core.ProcessClass.process(ProcessClass.java:32)
                    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:293)
                    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
                    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:201)
                    */
                /* JADX WARNING: Removed duplicated region for block: B:74:0x013d A:{SYNTHETIC, Splitter: B:74:0x013d} */
                /* JADX WARNING: Removed duplicated region for block: B:65:0x012c A:{SYNTHETIC, Splitter: B:65:0x012c} */
                /* JADX WARNING: Removed duplicated region for block: B:56:0x011b A:{SYNTHETIC, Splitter: B:56:0x011b} */
                /* JADX WARNING: Removed duplicated region for block: B:85:0x019d A:{SYNTHETIC, Splitter: B:85:0x019d} */
                /* JADX WARNING: Removed duplicated region for block: B:89:0x01a4 A:{SYNTHETIC, Splitter: B:89:0x01a4} */
                /* JADX WARNING: Removed duplicated region for block: B:74:0x013d A:{SYNTHETIC, Splitter: B:74:0x013d} */
                /* JADX WARNING: Removed duplicated region for block: B:65:0x012c A:{SYNTHETIC, Splitter: B:65:0x012c} */
                /* JADX WARNING: Removed duplicated region for block: B:56:0x011b A:{SYNTHETIC, Splitter: B:56:0x011b} */
                /* JADX WARNING: Removed duplicated region for block: B:85:0x019d A:{SYNTHETIC, Splitter: B:85:0x019d} */
                /* JADX WARNING: Removed duplicated region for block: B:89:0x01a4 A:{SYNTHETIC, Splitter: B:89:0x01a4} */
                /* JADX WARNING: Removed duplicated region for block: B:85:0x019d A:{SYNTHETIC, Splitter: B:85:0x019d} */
                /* JADX WARNING: Removed duplicated region for block: B:89:0x01a4 A:{SYNTHETIC, Splitter: B:89:0x01a4} */
                public void run() {
                    /*
                    r12 = this;
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ Exception -> 0x002c }
                    r0 = r0.encryptKey;	 Catch:{ Exception -> 0x002c }
                    r1 = r2;	 Catch:{ Exception -> 0x002c }
                    r1 = r1.obtainEncodeTsFileName();	 Catch:{ Exception -> 0x002c }
                    r0 = com.m3u8downloader.M3U8EncryptHelper.encryptFileName(r0, r1);	 Catch:{ Exception -> 0x002c }
                    r1 = new java.io.File;	 Catch:{ Exception -> 0x002c }
                    r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x002c }
                    r2.<init>();	 Catch:{ Exception -> 0x002c }
                    r3 = r3;	 Catch:{ Exception -> 0x002c }
                    r2.append(r3);	 Catch:{ Exception -> 0x002c }
                    r3 = java.io.File.separator;	 Catch:{ Exception -> 0x002c }
                    r2.append(r3);	 Catch:{ Exception -> 0x002c }
                    r2.append(r0);	 Catch:{ Exception -> 0x002c }
                    r0 = r2.toString();	 Catch:{ Exception -> 0x002c }
                    r1.<init>(r0);	 Catch:{ Exception -> 0x002c }
                    goto L_0x004d;
                L_0x002c:
                    r1 = new java.io.File;
                    r0 = new java.lang.StringBuilder;
                    r0.<init>();
                    r2 = r3;
                    r0.append(r2);
                    r2 = java.io.File.separator;
                    r0.append(r2);
                    r2 = r2;
                    r2 = r2.getUrl();
                    r0.append(r2);
                    r0 = r0.toString();
                    r1.<init>(r0);
                L_0x004d:
                    r0 = r1.exists();
                    if (r0 != 0) goto L_0x01a8;
                L_0x0053:
                    r0 = 0;
                    r2 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r2;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r4 = r4;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r5 = r5;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r3.obtainFullUrl(r4, r5);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2.<init>(r3);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2 = r2.openConnection();	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2 = (java.net.HttpURLConnection) r2;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r3.connTimeout;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2.setConnectTimeout(r3);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r3.readTimeout;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2.setReadTimeout(r3);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r2.getResponseCode();	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    if (r3 != r4) goto L_0x00ee;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                L_0x0083:
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r3.isStartDownload;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r4 = 0;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    if (r3 == 0) goto L_0x009c;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                L_0x008c:
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3.isStartDownload = r4;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r3.mHandler;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r5 = 1004; // 0x3ec float:1.407E-42 double:4.96E-321;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3.sendEmptyMessage(r5);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                L_0x009c:
                    r2 = r2.getInputStream();	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = new java.io.FileOutputStream;	 Catch:{ MalformedURLException -> 0x00e8, IOException -> 0x00e2, Exception -> 0x00dc, all -> 0x00d7 }
                    r3.<init>(r1);	 Catch:{ MalformedURLException -> 0x00e8, IOException -> 0x00e2, Exception -> 0x00dc, all -> 0x00d7 }
                    r0 = 8388608; // 0x800000 float:1.17549435E-38 double:4.144523E-317;
                    r0 = new byte[r0];	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                L_0x00a9:
                    r5 = r2.read(r0);	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r6 = -1;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    if (r5 == r6) goto L_0x00c1;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                L_0x00b0:
                    r6 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r7 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r7 = r7.curLength;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r9 = (long) r5;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r7 = r7 + r9;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r6.curLength = r7;	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    r3.write(r0, r4, r5);	 Catch:{ MalformedURLException -> 0x00d1, IOException -> 0x00cb, Exception -> 0x00c5, all -> 0x00c3 }
                    goto L_0x00a9;
                L_0x00c1:
                    r0 = r2;
                    goto L_0x0101;
                L_0x00c3:
                    r1 = move-exception;
                    goto L_0x00d9;
                L_0x00c5:
                    r0 = move-exception;
                    r11 = r2;
                    r2 = r0;
                    r0 = r11;
                    goto L_0x0114;
                L_0x00cb:
                    r0 = move-exception;
                    r11 = r2;
                    r2 = r0;
                    r0 = r11;
                    goto L_0x0125;
                L_0x00d1:
                    r0 = move-exception;
                    r11 = r2;
                    r2 = r0;
                    r0 = r11;
                    goto L_0x0136;
                L_0x00d7:
                    r1 = move-exception;
                    r3 = r0;
                L_0x00d9:
                    r0 = r2;
                    goto L_0x019b;
                L_0x00dc:
                    r3 = move-exception;
                    r11 = r3;
                    r3 = r0;
                    r0 = r2;
                    r2 = r11;
                    goto L_0x0114;
                L_0x00e2:
                    r3 = move-exception;
                    r11 = r3;
                    r3 = r0;
                    r0 = r2;
                    r2 = r11;
                    goto L_0x0125;
                L_0x00e8:
                    r3 = move-exception;
                    r11 = r3;
                    r3 = r0;
                    r0 = r2;
                    r2 = r11;
                    goto L_0x0136;
                L_0x00ee:
                    r3 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r4 = new java.lang.Throwable;	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2 = r2.getResponseCode();	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r2 = java.lang.String.valueOf(r2);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r4.<init>(r2);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3.handlerError(r4);	 Catch:{ MalformedURLException -> 0x0134, IOException -> 0x0123, Exception -> 0x0112, all -> 0x010e }
                    r3 = r0;
                L_0x0101:
                    if (r0 == 0) goto L_0x0108;
                L_0x0103:
                    r0.close();	 Catch:{ IOException -> 0x0107 }
                    goto L_0x0108;
                L_0x0108:
                    if (r3 == 0) goto L_0x0145;
                L_0x010a:
                    r3.close();	 Catch:{ IOException -> 0x0145 }
                    goto L_0x0145;
                L_0x010e:
                    r1 = move-exception;
                    r3 = r0;
                    goto L_0x019b;
                L_0x0112:
                    r2 = move-exception;
                    r3 = r0;
                L_0x0114:
                    r4 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x019a }
                    r4.handlerError(r2);	 Catch:{ all -> 0x019a }
                    if (r0 == 0) goto L_0x0120;
                L_0x011b:
                    r0.close();	 Catch:{ IOException -> 0x011f }
                    goto L_0x0120;
                L_0x0120:
                    if (r3 == 0) goto L_0x0145;
                L_0x0122:
                    goto L_0x010a;
                L_0x0123:
                    r2 = move-exception;
                    r3 = r0;
                L_0x0125:
                    r4 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x019a }
                    r4.handlerError(r2);	 Catch:{ all -> 0x019a }
                    if (r0 == 0) goto L_0x0131;
                L_0x012c:
                    r0.close();	 Catch:{ IOException -> 0x0130 }
                    goto L_0x0131;
                L_0x0131:
                    if (r3 == 0) goto L_0x0145;
                L_0x0133:
                    goto L_0x010a;
                L_0x0134:
                    r2 = move-exception;
                    r3 = r0;
                L_0x0136:
                    r4 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x019a }
                    r4.handlerError(r2);	 Catch:{ all -> 0x019a }
                    if (r0 == 0) goto L_0x0142;
                L_0x013d:
                    r0.close();	 Catch:{ IOException -> 0x0141 }
                    goto L_0x0142;
                L_0x0142:
                    if (r3 == 0) goto L_0x0145;
                L_0x0144:
                    goto L_0x010a;
                L_0x0145:
                    r0 = r6;
                    r0.lock();
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x0193 }
                    r0.curTs = r0.curTs + 1;	 Catch:{ all -> 0x0193 }
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x0193 }
                    r1 = r1.length();	 Catch:{ all -> 0x0193 }
                    r0.itemFileSize = r1;	 Catch:{ all -> 0x0193 }
                    r0 = r2;	 Catch:{ all -> 0x0193 }
                    r1 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x0193 }
                    r1 = r1.itemFileSize;	 Catch:{ all -> 0x0193 }
                    r0.setFileSize(r1);	 Catch:{ all -> 0x0193 }
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x0193 }
                    r0 = r0.mHandler;	 Catch:{ all -> 0x0193 }
                    r1 = 1002; // 0x3ea float:1.404E-42 double:4.95E-321;	 Catch:{ all -> 0x0193 }
                    r0.sendEmptyMessage(r1);	 Catch:{ all -> 0x0193 }
                    r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0193 }
                    r0.<init>();	 Catch:{ all -> 0x0193 }
                    r1 = "curTs=";	 Catch:{ all -> 0x0193 }
                    r0.append(r1);	 Catch:{ all -> 0x0193 }
                    r1 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x0193 }
                    r1 = r1.curTs;	 Catch:{ all -> 0x0193 }
                    r0.append(r1);	 Catch:{ all -> 0x0193 }
                    r1 = ". 新缓存";	 Catch:{ all -> 0x0193 }
                    r0.append(r1);	 Catch:{ all -> 0x0193 }
                    r0 = r0.toString();	 Catch:{ all -> 0x0193 }
                    com.m3u8downloader.utils.M3U8Log.d(r0);	 Catch:{ all -> 0x0193 }
                    r0 = r6;
                    r0.unlock();
                    goto L_0x01ea;
                L_0x0193:
                    r0 = move-exception;
                    r1 = r6;
                    r1.unlock();
                    throw r0;
                L_0x019a:
                    r1 = move-exception;
                L_0x019b:
                    if (r0 == 0) goto L_0x01a2;
                L_0x019d:
                    r0.close();	 Catch:{ IOException -> 0x01a1 }
                    goto L_0x01a2;
                L_0x01a2:
                    if (r3 == 0) goto L_0x01a7;
                L_0x01a4:
                    r3.close();	 Catch:{ IOException -> 0x01a7 }
                L_0x01a7:
                    throw r1;
                L_0x01a8:
                    r0 = r6;
                    r0.lock();
                    r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01eb }
                    r0.<init>();	 Catch:{ all -> 0x01eb }
                    r2 = "curTs=";	 Catch:{ all -> 0x01eb }
                    r0.append(r2);	 Catch:{ all -> 0x01eb }
                    r2 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x01eb }
                    r2 = r2.curTs;	 Catch:{ all -> 0x01eb }
                    r0.append(r2);	 Catch:{ all -> 0x01eb }
                    r2 = ". 缓存存在";	 Catch:{ all -> 0x01eb }
                    r0.append(r2);	 Catch:{ all -> 0x01eb }
                    r0 = r0.toString();	 Catch:{ all -> 0x01eb }
                    com.m3u8downloader.utils.M3U8Log.d(r0);	 Catch:{ all -> 0x01eb }
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x01eb }
                    r0.curTs = r0.curTs + 1;	 Catch:{ all -> 0x01eb }
                    r0 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x01eb }
                    r1 = r1.length();	 Catch:{ all -> 0x01eb }
                    r0.itemFileSize = r1;	 Catch:{ all -> 0x01eb }
                    r0 = r2;	 Catch:{ all -> 0x01eb }
                    r1 = com.m3u8downloader.M3U8DownloadTask.this;	 Catch:{ all -> 0x01eb }
                    r1 = r1.itemFileSize;	 Catch:{ all -> 0x01eb }
                    r0.setFileSize(r1);	 Catch:{ all -> 0x01eb }
                    r0 = r6;
                    r0.unlock();
                L_0x01ea:
                    return;
                L_0x01eb:
                    r0 = move-exception;
                    r1 = r6;
                    r1.unlock();
                    throw r0;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.m3u8downloader.M3U8DownloadTask.4.run():void");
                }
            });
        }
    }

    private void handlerError(Throwable th) {
        if (!"Task running".equals(th.getMessage())) {
            stop();
        }
        if (!"thread interrupted".equals(th.getMessage())) {
            Message obtain = Message.obtain();
            obtain.obj = th;
            obtain.what = 1001;
            this.mHandler.sendMessage(obtain);
        }
    }

    public void stop() {
        Timer timer = this.netSpeedTimer;
        if (timer != null) {
            timer.cancel();
            this.netSpeedTimer = null;
        }
        this.isRunning = false;
        ExecutorService executorService = this.executor;
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public File getM3u8File(String str) {
        try {
            return new File(MUtils.getSaveFileDir(str), this.m3u8FileName);
        } catch (Exception e) {
            M3U8Log.e(e.getMessage());
            return null;
        }
    }
}
