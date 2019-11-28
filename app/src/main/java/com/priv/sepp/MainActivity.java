package com.priv.sepp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.InputDeviceCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.KeyGenerator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class MainActivity extends Activity {
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4096;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 4096;
    public static final int REQUEST_MEDIA_PROJECTION = 10387;
    private int MaxPos;
    private ArrayList<String> apiURLs = new ArrayList();
    private String appURL = "https://www.baidu.com";
    private String cLastUp;
    private String cString;
    private int currentAPIIndex = 0;
    private int currentPos;
    private Key key;
    private String lastURL = "";
    private File mFile;
    private Handler mHandler = new Handler();
    private String mLocalUrl = "";
    ArrayList<String> mTp = new ArrayList();
    private WebSettings mWebSettings;
    private WebView mWebview;
    private OrientationUtils orientationUtils;
    private Handler refreshHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                Uri parse = Uri.parse("http://www.qq.com");
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(parse);
                MainActivity.this.startActivity(intent);
            } else if (i != InputDeviceCompat.SOURCE_KEYBOARD) {
                if (i != InputDeviceCompat.SOURCE_DPAD) {
                    switch (i) {
                        case 768:
                            if (MainActivity.this.mWebview != null) {
                                MainActivity.this.startCheckTimeout();
                                Log.d("XTIMEOUT", MainActivity.this.appURL);
                                MainActivity.this.mWebview.loadUrl(MainActivity.this.appURL);
                                break;
                            }
                            return;
                        case 769:
                            MainActivity.this.updateWebUrl();
                            break;
                        case 770:
                            MainActivity mainActivity = MainActivity.this;
                            mainActivity.getAPI(mainActivity.lastURL);
                            break;
                    }
                }
                MainActivity.this.updateLast();
            } else if (MainActivity.this.mWebview != null && MainActivity.this.mWebview.getProgress() < 100) {
                Log.d("XTIMEOUT", "Timeout>>>>>");
                MainActivity.this.updateWebUrl();
            }
        }
    };
    private long timeout = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    private Timer timer;
    private StandardGSYVideoPlayer videoPlayer;

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void downloadMovie(String str) {
        }

        @JavascriptInterface
        public void showDescription(String str) {
        }

        @JavascriptInterface
        public void openBrowser(String str) {
            MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        }

        @JavascriptInterface
        public void onRegSuccess(String str) {
            Toast.makeText(MainActivity.this.getApplicationContext(), str, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void play(String str, String str2) {
            Intent intent = new Intent(MainActivity.this.getApplicationContext(), FullScreenActivity.class);
            intent.putExtra("M3U8_URL", str);
            intent.putExtra("online", true);
            intent.putExtra("videoname", str2);
            MainActivity.this.startActivity(intent);
        }

        @JavascriptInterface
        public void onBack(String str) {
            MainActivity.this.finish();
        }

        @JavascriptInterface
        public void showPlayer(String str) {
            if (str.contentEquals("1")) {
                MainActivity.this.showVideoPlayer(Boolean.valueOf(true));
            } else {
                MainActivity.this.showVideoPlayer(Boolean.valueOf(false));
            }
        }

        @JavascriptInterface
        public void sendMessage(String r2, String r3, String r4) {
            /*
            r1 = this;
            r0 = r2.hashCode();
            switch(r0) {
                case -1013481626: goto L_0x0030;
                case -45886082: goto L_0x0026;
                case 3443508: goto L_0x001c;
                case 15113096: goto L_0x0012;
                case 689336382: goto L_0x0008;
                default: goto L_0x0007;
            };
        L_0x0007:
            goto L_0x003a;
        L_0x0008:
            r0 = "showPlayer";
            r2 = r2.equals(r0);
            if (r2 == 0) goto L_0x003a;
        L_0x0010:
            r2 = 4;
            goto L_0x003b;
        L_0x0012:
            r0 = "downloadMovie";
            r2 = r2.equals(r0);
            if (r2 == 0) goto L_0x003a;
        L_0x001a:
            r2 = 3;
            goto L_0x003b;
        L_0x001c:
            r0 = "play";
            r2 = r2.equals(r0);
            if (r2 == 0) goto L_0x003a;
        L_0x0024:
            r2 = 1;
            goto L_0x003b;
        L_0x0026:
            r0 = "openBrowser";
            r2 = r2.equals(r0);
            if (r2 == 0) goto L_0x003a;
        L_0x002e:
            r2 = 0;
            goto L_0x003b;
        L_0x0030:
            r0 = "onBack";
            r2 = r2.equals(r0);
            if (r2 == 0) goto L_0x003a;
        L_0x0038:
            r2 = 2;
            goto L_0x003b;
        L_0x003a:
            r2 = -1;
        L_0x003b:
            switch(r2) {
                case 0: goto L_0x004f;
                case 1: goto L_0x004b;
                case 2: goto L_0x0047;
                case 3: goto L_0x0043;
                case 4: goto L_0x003f;
                default: goto L_0x003e;
            };
        L_0x003e:
            goto L_0x0052;
        L_0x003f:
            r1.showPlayer(r3);
            goto L_0x0052;
        L_0x0043:
            r1.downloadMovie(r3);
            goto L_0x0052;
        L_0x0047:
            r1.onBack(r3);
            goto L_0x0052;
        L_0x004b:
            r1.play(r3, r4);
            goto L_0x0052;
        L_0x004f:
            r1.openBrowser(r3);
        L_0x0052:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.InJavaScriptLocalObj.sendMessage(java.lang.String, java.lang.String, java.lang.String):void");
        }
    }

    public void getKey(String str) {
        try {
            KeyGenerator instance = KeyGenerator.getInstance(Des.ALGORITHM);
            instance.init(new SecureRandom(str.getBytes()));
            this.key = instance.generateKey();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error initializing SqlMap class. Cause: ");
            stringBuilder.append(e);
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getKey("xiangkankan");
        setContentView(R.layout.activity_main);
        findViewById(R.id.Shotter).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.requestScreenShot(true);
            }
        });
        setupVideoPlayer();
        requestScreenShot(false);
        setupWebview();
    }

    private void setupVideoPlayer() {
        this.videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.miniVideoView);
        this.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        this.videoPlayer.setLockClickListener(new LockClickListener() {
            public void onClick(View view, boolean z) {
                if (MainActivity.this.orientationUtils != null) {
                    MainActivity.this.orientationUtils.setEnable(true);
                }
            }
        });
        this.orientationUtils = new OrientationUtils(this, this.videoPlayer);
        this.videoPlayer.setNeedLockFull(true);
        this.videoPlayer.getBackButton().setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.d("MMMPPPP", " videoPlayer.getBackButton()");
            }
        });
        this.videoPlayer.setBackFromFullScreenListener(new OnClickListener() {
            public void onClick(View view) {
                Log.d("MMMPPPP", " videoPlayer.setBackFromFullScreenListener");
            }
        });
        List arrayList = new ArrayList();
        arrayList.add(new VideoOptionModel(1, "allowed_media_types", "video"));
        arrayList.add(new VideoOptionModel(1, "analyzemaxduration", 100));
        arrayList.add(new VideoOptionModel(1, "probesize", 10240));
        arrayList.add(new VideoOptionModel(1, "flush_packets", 1));
        arrayList.add(new VideoOptionModel(4, "framedrop", 1));
        arrayList.add(new VideoOptionModel(1, "analyzeduration", 1));
        arrayList.add(new VideoOptionModel(4, "packet-buffering", 0));
        arrayList.add(new VideoOptionModel(4, "start-on-prepared", 0));
        arrayList.add(new VideoOptionModel(1, "http-detect-range-support", 0));
        arrayList.add(new VideoOptionModel(2, "skip_loop_filter", 48));
        arrayList.add(new VideoOptionModel(2, "skip_loop_filter", 8));
        arrayList.add(new VideoOptionModel(4, "find_stream_info", 0));
        GSYVideoManager.instance().setOptionModelList(arrayList);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        this.videoPlayer.getCurrentPlayer().setUp("http://hcjs2ra2rytd8v8np1q.exp.bcevod.com/mda-hegtjx8n5e8jt9zv/mda-hegtjx8n5e8jt9zv.m3u8", false, "测试影片1");
        this.videoPlayer.getCurrentPlayer().startPlayLogic();
    }

    public void showVideoPlayer(Boolean bool) {
        if (bool.booleanValue()) {
            this.videoPlayer.setVisibility(View.VISIBLE);
        } else {
            this.videoPlayer.setVisibility(View.GONE);
        }
    }

    private void setupWebview() {
        this.mWebview = (WebView) findViewById(R.id.webview);
        this.mWebSettings = this.mWebview.getSettings();
        this.mWebSettings.setJavaScriptEnabled(true);
        this.mWebSettings.setDomStorageEnabled(true);
        this.mWebSettings.setTextZoom(100);
        this.mWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "jsBridgeInstance");
        this.mWebSettings.setUseWideViewPort(true);
        this.mWebSettings.setLoadWithOverviewMode(true);
        this.mWebSettings.setSupportZoom(true);
        this.mWebSettings.setBuiltInZoomControls(true);
        this.mWebSettings.setDisplayZoomControls(false);
        this.mWebSettings.setAllowFileAccess(true);
        this.mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        this.mWebSettings.setLoadsImagesAutomatically(true);
        this.mWebSettings.setDefaultTextEncodingName("utf-8");
        this.mWebSettings.setUserAgentString("YaseMessager");
        this.mWebview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("shouldOverrideUrlLoading:");
                stringBuilder.append(str);
                printStream.print(stringBuilder.toString());
                if (str.startsWith("xhttp://") || str.startsWith("xhttps://")) {
                    String replace;
                    if (str.startsWith("xhttp://")) {
                        replace = str.replace("xhttp://", "http://");
                    } else {
                        replace = str.replace("xhttps://", "https://");
                    }
                    Uri parse = Uri.parse(replace);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(parse);
                    MainActivity.this.startActivity(intent);
                    return false;
                }
                webView.loadUrl(str);
                return true;
            }

            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                String uri = webResourceRequest.getUrl().toString();
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("shouldOverrideUrlLoading2:");
                stringBuilder.append(uri);
                printStream.print(stringBuilder.toString());
                Toast.makeText(MainActivity.this.getApplicationContext(), uri, Toast.LENGTH_SHORT).show();
                if (uri.startsWith("http://") || uri.startsWith("https://")) {
                    String replace;
                    if (uri.startsWith("http://")) {
                        replace = uri.replace("http://", "http://");
                    } else {
                        replace = uri.replace("https://", "https://");
                    }
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(replace)));
                    return true;
                } else if (VERSION.SDK_INT < 21) {
                    return true;
                } else {
                    webView.loadUrl(webResourceRequest.getUrl().toString());
                    return true;
                }
            }

            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
                Log.e("11onReceivedError", str);
                if (i == -2 || i == -6 || i == -8) {
                    webView.loadUrl("about:blank");
                }
            }

            @TargetApi(23)
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                Log.e("onReceivedError>>", webResourceError.toString());
                if (webResourceError.toString().startsWith("找不到")) {
                    webView.loadUrl("about:blank");
                } else if (webResourceError.getErrorCode() == -2 || webResourceError.getErrorCode() == -6 || webResourceError.getErrorCode() == -8) {
                    webView.loadUrl("about:blank");
                }
            }

            @TargetApi(23)
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                Log.e("onReceivedHttpError", webResourceResponse.toString());
                webResourceResponse.getStatusCode();
            }

            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onPageStarted:....");
                stringBuilder.append(str);
                Log.e("onPage", stringBuilder.toString());
            }

            public void onPageFinished(WebView webView, String str) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onPageFinished:....");
                stringBuilder.append(str);
                Log.e("onPage", stringBuilder.toString());
                super.onPageFinished(webView, str);
                MainActivity.this.timer.cancel();
                MainActivity.this.timer.purge();
                if (str.equals("about:blank")) {
                    MainActivity.this.updateWebUrl();
                }
            }
        });
        this.mWebview.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView webView, String str) {
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("标题在这里");
                stringBuilder.append(str);
                printStream.println(stringBuilder.toString());
                super.onReceivedTitle(webView, str);
                if (VERSION.SDK_INT >= 23) {
                    return;
                }
                if (str.contains("404") || str.contains("500") || str.contains("Error")) {
                    webView.loadUrl("about:blank");
                }
            }

            public void onProgressChanged(WebView webView, int i) {
                StringBuilder stringBuilder;
                if (i < 100) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(i);
                    stringBuilder.append("%");
                    stringBuilder.toString();
                } else if (i == 100) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(i);
                    stringBuilder.append("%");
                    stringBuilder.toString();
                }
            }
        });
        String GetLastAvailable = GetLastAvailable();
        if (GetLastAvailable != null) {
            Log.d("UUUUU", Des.decode(GetLastAvailable));
            this.mWebview.loadUrl(Des.decode(GetLastAvailable));
        } else {
            LoadConfig();
        }
        startCheckTimeout();
        this.mWebview.loadUrl(getWebUrl());
    }

    private void updateLast() {
        this.cString = LoadConfig();
        String str = this.cString;
        if (str != null) {
            Log.d("UUUUUU100", str);
            str = Des.encode(this.cString);
            Log.d("UUUUUU101>>", str);
            String[] split = str.split("##");
            if (split.length > 1) {
                this.mTp.clear();
                for (int i = 0; i <= split.length - 2; i++) {
                    this.mTp.add(split[i]);
                }
                this.cLastUp = split[split.length - 1];
                this.currentPos = 0;
                this.MaxPos = this.mTp.size();
            }
        }
        startCheckTimeout();
        this.mWebview.loadUrl((String) this.mTp.get(this.currentPos));
    }

    public void startCheckTimeout() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                MainActivity.this.refreshHandler.sendEmptyMessage(InputDeviceCompat.SOURCE_KEYBOARD);
                MainActivity.this.timer.cancel();
                MainActivity.this.timer.purge();
            }
        }, this.timeout, 1);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || !this.mWebview.canGoBack()) {
            return super.onKeyDown(i, keyEvent);
        }
        this.mWebview.goBack();
        return true;
    }

    protected void onDestroy() {
        WebView webView = this.mWebview;
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", NanoHTTPD.MIME_HTML, "utf-8", null);
            this.mWebview.clearHistory();
            ((ViewGroup) this.mWebview.getParent()).removeView(this.mWebview);
            this.mWebview.destroy();
            this.mWebview = null;
        }
        super.onDestroy();
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x005e A:{SYNTHETIC, Splitter: B:26:0x005e} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x006a A:{SYNTHETIC, Splitter: B:33:0x006a} */
    private String LoadConfig() {
        /*
        r5 = this;
        r0 = new java.io.File;
        r1 = r5.getFilesDir();
        r2 = "conf.ini";
        r0.<init>(r1, r2);
        r5.mFile = r0;
        r0 = r5.mFile;
        r0 = r0.exists();
        r1 = 0;
        if (r0 == 0) goto L_0x0073;
    L_0x0016:
        r0 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r2 = r5.mFile;	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r0.<init>(r2);	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r2 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r3 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r3.<init>(r0);	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r2.<init>(r3);	 Catch:{ IOException -> 0x0057, all -> 0x0054 }
        r0 = "";
    L_0x0029:
        r3 = r2.readLine();	 Catch:{ IOException -> 0x0052 }
        if (r3 == 0) goto L_0x003f;
    L_0x002f:
        r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0052 }
        r4.<init>();	 Catch:{ IOException -> 0x0052 }
        r4.append(r0);	 Catch:{ IOException -> 0x0052 }
        r4.append(r3);	 Catch:{ IOException -> 0x0052 }
        r0 = r4.toString();	 Catch:{ IOException -> 0x0052 }
        goto L_0x0029;
    L_0x003f:
        r3 = r0.length();	 Catch:{ IOException -> 0x0052 }
        if (r3 <= 0) goto L_0x004e;
    L_0x0045:
        r2.close();	 Catch:{ IOException -> 0x0049 }
        goto L_0x004d;
    L_0x0049:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x004d:
        return r0;
    L_0x004e:
        r2.close();	 Catch:{ IOException -> 0x0062 }
        goto L_0x0066;
    L_0x0052:
        r0 = move-exception;
        goto L_0x0059;
    L_0x0054:
        r0 = move-exception;
        r2 = r1;
        goto L_0x0068;
    L_0x0057:
        r0 = move-exception;
        r2 = r1;
    L_0x0059:
        r0.printStackTrace();	 Catch:{ all -> 0x0067 }
        if (r2 == 0) goto L_0x0066;
    L_0x005e:
        r2.close();	 Catch:{ IOException -> 0x0062 }
        goto L_0x0066;
    L_0x0062:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x0066:
        return r1;
    L_0x0067:
        r0 = move-exception;
    L_0x0068:
        if (r2 == 0) goto L_0x0072;
    L_0x006a:
        r2.close();	 Catch:{ IOException -> 0x006e }
        goto L_0x0072;
    L_0x006e:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x0072:
        throw r0;
    L_0x0073:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.LoadConfig():java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0028 A:{SYNTHETIC, Splitter: B:12:0x0028} */
    private void SaveConfig(String r4) {
        /*
        r3 = this;
        r0 = new java.io.File;
        r1 = r3.getFilesDir();
        r2 = "conf.ini";
        r0.<init>(r1, r2);
        r3.mFile = r0;
        r0 = 0;
        r1 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r2 = r3.mFile;	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r1.<init>(r2);	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r4 = r4.getBytes();	 Catch:{ IOException -> 0x0023, all -> 0x0020 }
        r1.write(r4);	 Catch:{ IOException -> 0x0023, all -> 0x0020 }
        r1.close();	 Catch:{ IOException -> 0x0031 }
        goto L_0x0031;
    L_0x0020:
        r4 = move-exception;
        r0 = r1;
        goto L_0x0026;
    L_0x0023:
        r0 = r1;
        goto L_0x002c;
    L_0x0025:
        r4 = move-exception;
    L_0x0026:
        if (r0 == 0) goto L_0x002b;
    L_0x0028:
        r0.close();	 Catch:{ IOException -> 0x002b }
    L_0x002b:
        throw r4;
    L_0x002c:
        if (r0 == 0) goto L_0x0031;
    L_0x002e:
        r0.close();	 Catch:{ IOException -> 0x0031 }
    L_0x0031:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.SaveConfig(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0056 A:{SYNTHETIC, Splitter: B:29:0x0056} */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0049 A:{SYNTHETIC, Splitter: B:21:0x0049} */
    private String GetLastAvailable() {
        /*
        r4 = this;
        r0 = new java.io.File;
        r1 = r4.getFilesDir();
        r2 = "av.dat";
        r0.<init>(r1, r2);
        r4.mFile = r0;
        r0 = r4.mFile;
        r0 = r0.exists();
        r1 = 0;
        if (r0 == 0) goto L_0x005f;
    L_0x0016:
        r0 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r2 = r4.mFile;	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r0.<init>(r2);	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r2 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r3 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r3.<init>(r0);	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r2.<init>(r3);	 Catch:{ IOException -> 0x0042, all -> 0x0040 }
        r0 = r2.readLine();	 Catch:{ IOException -> 0x003e }
        r3 = r0.length();	 Catch:{ IOException -> 0x003e }
        if (r3 <= 0) goto L_0x003a;
    L_0x0031:
        r2.close();	 Catch:{ IOException -> 0x0035 }
        goto L_0x0039;
    L_0x0035:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x0039:
        return r0;
    L_0x003a:
        r2.close();	 Catch:{ IOException -> 0x004d }
        goto L_0x0051;
    L_0x003e:
        r0 = move-exception;
        goto L_0x0044;
    L_0x0040:
        r0 = move-exception;
        goto L_0x0054;
    L_0x0042:
        r0 = move-exception;
        r2 = r1;
    L_0x0044:
        r0.printStackTrace();	 Catch:{ all -> 0x0052 }
        if (r2 == 0) goto L_0x0051;
    L_0x0049:
        r2.close();	 Catch:{ IOException -> 0x004d }
        goto L_0x0051;
    L_0x004d:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x0051:
        return r1;
    L_0x0052:
        r0 = move-exception;
        r1 = r2;
    L_0x0054:
        if (r1 == 0) goto L_0x005e;
    L_0x0056:
        r1.close();	 Catch:{ IOException -> 0x005a }
        goto L_0x005e;
    L_0x005a:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x005e:
        throw r0;
    L_0x005f:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.GetLastAvailable():java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0028 A:{SYNTHETIC, Splitter: B:12:0x0028} */
    private void SaveAvailable(String r4) {
        /*
        r3 = this;
        r0 = new java.io.File;
        r1 = r3.getFilesDir();
        r2 = "av.dat";
        r0.<init>(r1, r2);
        r3.mFile = r0;
        r0 = 0;
        r1 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r2 = r3.mFile;	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r1.<init>(r2);	 Catch:{ IOException -> 0x002c, all -> 0x0025 }
        r4 = r4.getBytes();	 Catch:{ IOException -> 0x0023, all -> 0x0020 }
        r1.write(r4);	 Catch:{ IOException -> 0x0023, all -> 0x0020 }
        r1.close();	 Catch:{ IOException -> 0x0031 }
        goto L_0x0031;
    L_0x0020:
        r4 = move-exception;
        r0 = r1;
        goto L_0x0026;
    L_0x0023:
        r0 = r1;
        goto L_0x002c;
    L_0x0025:
        r4 = move-exception;
    L_0x0026:
        if (r0 == 0) goto L_0x002b;
    L_0x0028:
        r0.close();	 Catch:{ IOException -> 0x002b }
    L_0x002b:
        throw r4;
    L_0x002c:
        if (r0 == 0) goto L_0x0031;
    L_0x002e:
        r0.close();	 Catch:{ IOException -> 0x0031 }
    L_0x0031:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.SaveAvailable(java.lang.String):void");
    }

    private void GetAvailablePos() {
        String format = String.format("/pos", new Object[0]);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.cLastUp);
        stringBuilder.append(format);
        new OkHttpClient().newCall(new Builder().url(stringBuilder.toString()).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("RET", iOException.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                MainActivity mainActivity = MainActivity.this;
                mainActivity.appURL = mainActivity.parserAPI(string, mainActivity.apiURLs);
                MainActivity.this.SaveURLJsonString(string);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("result", string);
                message.setData(bundle);
                message.what = InputDeviceCompat.SOURCE_DPAD;
                MainActivity.this.refreshHandler.sendMessage(message);
            }
        });
    }

    private String getWebUrl() {
        String ReadURLJsonString = ReadURLJsonString();
        if (ReadURLJsonString != null) {
            this.appURL = parserAPI(ReadURLJsonString, this.apiURLs);
        } else {
            this.appURL = "http://www.fxxxfbaidu.com/";
            this.apiURLs.add("http://192.168.8.117/surl.json");
            this.apiURLs.add("http://192.168.8.117/1surl.json");
            this.apiURLs.add("http://192.168.8.117/1surl.json");
            this.apiURLs.add("http://192.168.8.117/1surl.json");
            this.apiURLs.add("http://192.168.8.117/1surl.json");
            this.apiURLs.add("https://github.com/dedemao/weixinPay/issues/11");
        }
        return this.appURL;
    }

    private void updateWebUrl() {
        if (this.currentAPIIndex < this.apiURLs.size()) {
            getAPI((String) this.apiURLs.get(this.currentAPIIndex));
        }
    }

    private void getAPI(String str) {
        new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build().newCall(new Builder().url(str).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("XTIMEOUT...", iOException.getMessage());
                if (!(iOException instanceof SocketTimeoutException)) {
                    boolean z = iOException instanceof ConnectException;
                }
                MainActivity.this.currentAPIIndex = MainActivity.this.currentAPIIndex + 1;
                Message message = new Message();
                message.what = 769;
                MainActivity.this.refreshHandler.sendMessage(message);
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message;
                if (!response.isSuccessful()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(response.code());
                    stringBuilder.append("");
                    Log.e("XTIMEOUT...", stringBuilder.toString());
                    MainActivity.this.currentAPIIndex = MainActivity.this.currentAPIIndex + 1;
                    message = new Message();
                    message.what = 769;
                    MainActivity.this.refreshHandler.sendMessage(message);
                } else if (MainActivity.this.currentAPIIndex < MainActivity.this.apiURLs.size() - 1 || MainActivity.this.lastURL.length() != 0) {
                    MainActivity.this.currentAPIIndex = 0;
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.appURL = mainActivity.parserAPI(string, mainActivity.apiURLs);
                    MainActivity.this.SaveURLJsonString(string);
                    message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("appUrl", MainActivity.this.appURL);
                    message.setData(bundle);
                    message.what = 768;
                    MainActivity.this.refreshHandler.sendMessage(message);
                } else if (MainActivity.this.lastURL.equals("")) {
                    Matcher matcher = Pattern.compile("<meta name=\"description\" content=\"(.+?)\">").matcher(string);
                    while (matcher.find()) {
                        String group = matcher.group(1);
                        Log.e("XTIMEOUT...", group);
                        String[] split = group.split("\\-");
                        byte[] bArr = new byte[split.length];
                        int i = 0;
                        while (i < split.length && MyUtils.isNumeric(split[i])) {
                            bArr[i] = (byte) Integer.parseInt(split[i]);
                            i++;
                        }
                        MainActivity.this.lastURL = Des.encode(new String(bArr));
                        if (MainActivity.this.lastURL == null || !MainActivity.this.lastURL.startsWith("http")) {
                            MainActivity.this.lastURL = "";
                        } else {
                            Message message2 = new Message();
                            message2.what = 770;
                            MainActivity.this.refreshHandler.sendMessage(message2);
                        }
                    }
                }
            }
        });
    }

    private String parserAPI(String str, ArrayList<String> arrayList) {
        if (arrayList == null) {
            return null;
        }
        arrayList.clear();
        try {
            JSONObject jSONObject = new JSONObject(str);
            str = jSONObject.getString("web");
            JSONArray jSONArray = jSONObject.getJSONArray("apis");
            for (int i = 0; i < jSONArray.length() - 1; i++) {
                if (jSONArray.getJSONObject(i) != null) {
                    String string = jSONArray.getJSONObject(i).getString("key");
                    String[] split = string.split("\\-");
                    byte[] bArr = new byte[split.length];
                    int i2 = 0;
                    while (i2 < split.length && MyUtils.isNumeric(split[i2])) {
                        bArr[i2] = (byte) Integer.parseInt(split[i2]);
                        i2++;
                    }
                    String encode = Des.encode(new String(bArr));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(i);
                    stringBuilder.append("");
                    Log.e("XTIMEOUT_I", stringBuilder.toString());
                    Log.e("XTIMEOUT_U", encode);
                    if (string != null) {
                        arrayList.add(encode);
                    }
                }
            }
            return str;
        } catch (JSONException e) {
            Log.e("XTIMEOUT", e.getMessage());
            Log.e("XTIMEOUT?", "BAD");
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x002e A:{SYNTHETIC, Splitter: B:18:0x002e} */
    private void SaveURLJsonString(String r4) {
        /*
        r3 = this;
        r0 = new java.io.File;
        r1 = r3.getFilesDir();
        r2 = "surl.dat";
        r0.<init>(r1, r2);
        if (r4 != 0) goto L_0x000e;
    L_0x000d:
        return;
    L_0x000e:
        r1 = r4.length();
        if (r1 != 0) goto L_0x0015;
    L_0x0014:
        return;
    L_0x0015:
        r1 = 0;
        r2 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0032, all -> 0x002b }
        r2.<init>(r0);	 Catch:{ IOException -> 0x0032, all -> 0x002b }
        r4 = r4.getBytes();	 Catch:{ IOException -> 0x0029, all -> 0x0026 }
        r2.write(r4);	 Catch:{ IOException -> 0x0029, all -> 0x0026 }
        r2.close();	 Catch:{ IOException -> 0x0037 }
        goto L_0x0037;
    L_0x0026:
        r4 = move-exception;
        r1 = r2;
        goto L_0x002c;
    L_0x0029:
        r1 = r2;
        goto L_0x0032;
    L_0x002b:
        r4 = move-exception;
    L_0x002c:
        if (r1 == 0) goto L_0x0031;
    L_0x002e:
        r1.close();	 Catch:{ IOException -> 0x0031 }
    L_0x0031:
        throw r4;
    L_0x0032:
        if (r1 == 0) goto L_0x0037;
    L_0x0034:
        r1.close();	 Catch:{ IOException -> 0x0037 }
    L_0x0037:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.SaveURLJsonString(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0043 A:{SYNTHETIC, Splitter: B:21:0x0043} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0050 A:{SYNTHETIC, Splitter: B:29:0x0050} */
    private String ReadURLJsonString() {
        /*
        r4 = this;
        r0 = new java.io.File;
        r1 = r4.getFilesDir();
        r2 = "surl.dat";
        r0.<init>(r1, r2);
        r1 = r0.exists();
        r2 = 0;
        if (r1 == 0) goto L_0x0059;
    L_0x0012:
        r1 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r1.<init>(r0);	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r0 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r3 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r3.<init>(r1);	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r0.<init>(r3);	 Catch:{ IOException -> 0x003c, all -> 0x003a }
        r1 = r0.readLine();	 Catch:{ IOException -> 0x0038 }
        r3 = r1.length();	 Catch:{ IOException -> 0x0038 }
        if (r3 <= 0) goto L_0x0034;
    L_0x002b:
        r0.close();	 Catch:{ IOException -> 0x002f }
        goto L_0x0033;
    L_0x002f:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x0033:
        return r1;
    L_0x0034:
        r0.close();	 Catch:{ IOException -> 0x0047 }
        goto L_0x004b;
    L_0x0038:
        r1 = move-exception;
        goto L_0x003e;
    L_0x003a:
        r1 = move-exception;
        goto L_0x004e;
    L_0x003c:
        r1 = move-exception;
        r0 = r2;
    L_0x003e:
        r1.printStackTrace();	 Catch:{ all -> 0x004c }
        if (r0 == 0) goto L_0x004b;
    L_0x0043:
        r0.close();	 Catch:{ IOException -> 0x0047 }
        goto L_0x004b;
    L_0x0047:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x004b:
        return r2;
    L_0x004c:
        r1 = move-exception;
        r2 = r0;
    L_0x004e:
        if (r2 == 0) goto L_0x0058;
    L_0x0050:
        r2.close();	 Catch:{ IOException -> 0x0054 }
        goto L_0x0058;
    L_0x0054:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x0058:
        throw r1;
    L_0x0059:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.ReadURLJsonString():java.lang.String");
    }

    public void requestScreenShot(boolean z) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            if (z) {
                toast("请允许存储权限，否则将不能正常保存分享二维码");
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4096);
        } else if (z) {
            saveBitmapForSdCard(this, captureScreenWindow());
        }
    }

    private Intent createScreenCaptureIntent() {
        return ((MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent();
    }

    private void toast(String str) {
        Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 4096) {
            int i2 = iArr[0];
        }
        if (i == 4096 && iArr[0] == 0) {
            saveBitmapForSdCard(this, captureScreenWindow());
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 10387) {
            if (i2 == -1 && intent != null) {
                long currentTimeMillis = System.currentTimeMillis() / 1000;
                saveBitmapForSdCard(this, captureScreenWindow());
            } else if (i2 == 0) {
                toast("取消截屏 , 请允许访问相册权限.");
            } else {
                toast("unknow exceptions!");
            }
        }
    }

    public Bitmap captureScreenWindow() {
        getWindow().getDecorView().setDrawingCacheEnabled(true);
        return getWindow().getDecorView().getDrawingCache();
    }

    public void saveBitmapForSdCard(Activity activity, Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SystemClock.currentThreadTimeMillis());
        stringBuilder.append(".jpg");
        String stringBuilder2 = stringBuilder.toString();
        if (TextUtils.isEmpty(this.mLocalUrl)) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(MyApplication.getContext().getExternalFilesDir("screenshot").getAbsoluteFile());
            stringBuilder3.append("/");
            stringBuilder3.append(stringBuilder2);
            this.mLocalUrl = stringBuilder3.toString();
        }
        saveBmp2Gallery(bitmap, stringBuilder2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x006a A:{SYNTHETIC, Splitter: B:22:0x006a} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006a A:{SYNTHETIC, Splitter: B:22:0x006a} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x008a A:{SYNTHETIC, Splitter: B:27:0x008a} */
    public void saveBmp2Gallery(Bitmap r5, String r6) {
        /*
        r4 = this;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = android.os.Environment.getExternalStorageDirectory();
        r0.append(r1);
        r1 = java.io.File.separator;
        r0.append(r1);
        r1 = android.os.Environment.DIRECTORY_DCIM;
        r0.append(r1);
        r1 = java.io.File.separator;
        r0.append(r1);
        r1 = "Camera";
        r0.append(r1);
        r1 = java.io.File.separator;
        r0.append(r1);
        r0 = r0.toString();
        r1 = 0;
        r2 = new java.io.File;	 Catch:{ Exception -> 0x0063 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0063 }
        r3.<init>();	 Catch:{ Exception -> 0x0063 }
        r3.append(r6);	 Catch:{ Exception -> 0x0063 }
        r6 = ".jpg";
        r3.append(r6);	 Catch:{ Exception -> 0x0063 }
        r6 = r3.toString();	 Catch:{ Exception -> 0x0063 }
        r2.<init>(r0, r6);	 Catch:{ Exception -> 0x0063 }
        r6 = r2.toString();	 Catch:{ Exception -> 0x005f }
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x005f }
        r0.<init>(r6);	 Catch:{ Exception -> 0x005f }
        r6 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x005c, all -> 0x0059 }
        r1 = 90;
        r5.compress(r6, r1, r0);	 Catch:{ Exception -> 0x005c, all -> 0x0059 }
        r0.close();	 Catch:{ IOException -> 0x0054 }
        goto L_0x006d;
    L_0x0054:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x006d;
    L_0x0059:
        r5 = move-exception;
        r1 = r0;
        goto L_0x0088;
    L_0x005c:
        r5 = move-exception;
        r1 = r0;
        goto L_0x0065;
    L_0x005f:
        r5 = move-exception;
        goto L_0x0065;
    L_0x0061:
        r5 = move-exception;
        goto L_0x0088;
    L_0x0063:
        r5 = move-exception;
        r2 = r1;
    L_0x0065:
        r5.getStackTrace();	 Catch:{ all -> 0x0061 }
        if (r1 == 0) goto L_0x006d;
    L_0x006a:
        r1.close();	 Catch:{ IOException -> 0x0054 }
    L_0x006d:
        r5 = new android.content.Intent;
        r6 = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
        r5.<init>(r6);
        r6 = android.net.Uri.fromFile(r2);
        r5.setData(r6);
        r6 = com.yase999.app.MyApplication.getContext();
        r6.sendBroadcast(r5);
        r5 = "保存成功，请在相册中查看分享二维码！";
        r4.toast(r5);
        return;
    L_0x0088:
        if (r1 == 0) goto L_0x0092;
    L_0x008a:
        r1.close();	 Catch:{ IOException -> 0x008e }
        goto L_0x0092;
    L_0x008e:
        r6 = move-exception;
        r6.printStackTrace();
    L_0x0092:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.MainActivity.saveBmp2Gallery(android.graphics.Bitmap, java.lang.String):void");
    }
}
