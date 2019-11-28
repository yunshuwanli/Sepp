package com.priv.sepp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.priv.sepp.widget.BaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import fi.iki.elonen.NanoHTTPD;
import m3u8downloader.M3U8Downloader;
import m3u8downloader.M3U8DownloaderConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class PlayerWebViewActivity extends BaseActivityDetail<StandardGSYVideoPlayer> {
    protected static final LayoutParams COVER_SCREEN_PARAMS = new LayoutParams(-1, -1);
    private static final int FILECHOOSER_RESULTCODE = 2;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4096;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 4096;
    public static final int REQUEST_MEDIA_PROJECTION = 10387;
    private static final String TAG = "PlayerWebViewActivity";
    private int MaxPos;
    private ArrayList<String> apiURLs = new ArrayList();
    private String apkPath;
    private String appURL = "https://www.baidu.com";
    private int backupRendType;
    private ImageView btnGoback;
    private String cLastUp;
    private String cString;
    private int currentAPIIndex = 0;
    private int currentPos;
    private View customView;
    private CustomViewCallback customViewCallback;
    private ProgressDialog downloadDialog;
    private String encryptKey = "63F06F99D823D33AAB89A0A93DECFEE0";
    private FrameLayout fullscreenContainer;
    private boolean isSmall;
    private Key key;
    private String lastURL = "";
    private String mCameraPhotoPath;
    private File mFile;
    private ValueCallback<Uri[]> mFilePathCallback;
    private Handler mHandler = new Handler();
    private String mLocalUrl = "";
    ArrayList<String> mTp = new ArrayList();
    private ValueCallback<Uri> mUploadMessage;
    private WebSettings mWebSettings;
    WebView mWebview;
    private String newVersionUrl;
    private String playUrl;
    private Handler refreshHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                Uri parse = Uri.parse("http://www.qq.com");
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(parse);
                PlayerWebViewActivity.this.startActivity(intent);
            } else if (i == InputDeviceCompat.SOURCE_KEYBOARD) {
                PlayerWebViewActivity.this.updateWebUrl();
            } else if (i != InputDeviceCompat.SOURCE_DPAD) {
                PlayerWebViewActivity playerWebViewActivity;
                StringBuilder stringBuilder;
                if (i != 2048) {
                    switch (i) {
                        case 3:
                            PlayerWebViewActivity playerWebViewActivity2 = PlayerWebViewActivity.this;
                            String access$000 = playerWebViewActivity2.newVersionUrl;
                            if (message.arg1 != 1) {
                                z = false;
                            }
                            playerWebViewActivity2.showUpdateDialog(access$000, z);
                            break;
                        case 4:
                            PlayerWebViewActivity.this.downloadDialog.dismiss();
                            PlayerWebViewActivity.this.toast("新版本下载失败，请稍后再试。");
                            break;
                        case 5:
                            PlayerWebViewActivity.this.downloadDialog.setProgress(message.arg1);
                            if (message.arg1 == 100) {
                                PlayerWebViewActivity.this.downloadDialog.dismiss();
                                Context context = PlayerWebViewActivity.this;
                                new InstallUtil(context, context.apkPath).install();
                                break;
                            }
                            break;
                        default:
                            switch (i) {
                                case 768:
                                    if (PlayerWebViewActivity.this.appURL != null) {
                                        playerWebViewActivity = PlayerWebViewActivity.this;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(PlayerWebViewActivity.this.appURL);
                                        stringBuilder.append("/v.html?t=");
                                        stringBuilder.append(System.currentTimeMillis());
                                        playerWebViewActivity.checkUrl(stringBuilder.toString());
                                        break;
                                    }
                                    PlayerWebViewActivity.this.updateWebUrl();
                                    break;
                                case 769:
                                    PlayerWebViewActivity.this.updateWebUrl();
                                    break;
                                case 770:
                                    playerWebViewActivity = PlayerWebViewActivity.this;
                                    playerWebViewActivity.getAPI(playerWebViewActivity.lastURL);
                                    break;
                            }
                            break;
                    }
                } else if (PlayerWebViewActivity.this.mWebview != null) {
                    if (PlayerWebViewActivity.this.appURL == null) {
                        PlayerWebViewActivity.this.updateWebUrl();
                    } else {
                        WebView webView = PlayerWebViewActivity.this.mWebview;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(PlayerWebViewActivity.this.appURL);
                        stringBuilder.append("?t=");
                        stringBuilder.append(System.currentTimeMillis());
                        webView.loadUrl(stringBuilder.toString());
                        playerWebViewActivity = PlayerWebViewActivity.this;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(PlayerWebViewActivity.this.appURL);
                        stringBuilder.append("/v2.html?t=");
                        stringBuilder.append(System.currentTimeMillis());
                        playerWebViewActivity.checkVersion(stringBuilder.toString());
                    }
                }
            }
        }
    };
    private boolean startShotter = false;
    private long timeout = 10000;
    private Timer timer;
    private String versionKey = "version=1.1";
    private String videoName;
    PreViewGSYVideoPlayer webPlayer;
    NestedScrollView webTopLayout;
    RelativeLayout webTopLayoutVideo;

    static class FullscreenHolder extends FrameLayout {
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        public FullscreenHolder(Context context) {
            super(context);
            setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }
    }

    public final class InJavaScriptLocalObj {
        public int inApp() {
            return 1;
        }

        @JavascriptInterface
        public void showDescription(String str) {
        }

        public void openBrowser(String str) {
            PlayerWebViewActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        }

        @JavascriptInterface
        public void onRegSuccess(String str) {
            Toast.makeText(PlayerWebViewActivity.this.getApplicationContext(), str, 1).show();
        }

        public void play(final String str, final String str2) {
            PlayerWebViewActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    PlayerWebViewActivity.this.playVideo(str, str2);
                }
            });
        }

        public void onBack(String str) {
            PlayerWebViewActivity.this.finish();
        }

        public void showPlayer(final String str) {
            PlayerWebViewActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (str.contentEquals("1")) {
                        PlayerWebViewActivity.this.showVideoPlayer(true);
                    } else {
                        PlayerWebViewActivity.this.showVideoPlayer(false);
                    }
                }
            });
        }

        @JavascriptInterface
        public int sendMessage(String str, String str2, String str3) {
            if (str.equals("openBrowser")) {
                openBrowser(str2);
                return 0;
            } else if (str.equals("play")) {
                play(str2, str3);
                return 0;
            } else if (str.equals("onBack")) {
                onBack(str2);
                return 0;
            } else if (str.equals("downloadMovie")) {
                downloadMovie(str2);
                return 0;
            } else if (str.equals("showPlayer")) {
                showPlayer(str2);
                return 0;
            } else if (str.equals("getPlayerHeight")) {
                return PlayerWebViewActivity.this.getPlayerHeight();
            } else {
                if (str.equals("downloadManager")) {
                    PlayerWebViewActivity.this.startDownloadManagerActivity();
                    return 0;
                } else if (str.equals("inApp")) {
                    return 1;
                } else {
                    if (str.equals("saveQRCode")) {
                        PlayerWebViewActivity.this.startShotter = true;
                        PlayerWebViewActivity.this.requestScreenShot(true);
                        return 0;
                    } else if (str.equals("showPostComment")) {
                        PlayerWebViewActivity.this.showInputDialog();
                        return 0;
                    } else if (str.equals("installApp")) {
                        PlayerWebViewActivity.this.installApp(str2, str3);
                        return 0;
                    } else if (!str.equals("playRingtone")) {
                        return 0;
                    } else {
                        RingtoneManager.getRingtone(PlayerWebViewActivity.
                                this.getApplicationContext(), RingtoneManager.getDefaultUri(2)).play();
                        return 0;
                    }
                }
            }
        }

        public void downloadMovie(String str) {
            String[] split = str.split("\\$");
            if (split.length == 4) {
                String str2 = split[0];
                String str3 = split[1];
                String str4 = split[2];
                str = split[3];
                Intent intent = new Intent();
                intent.setAction("kankan.download.m3u8");
                Bundle bundle = new Bundle();
                bundle.putString("url", str2);
                bundle.putBoolean("downloadNow", true);
                bundle.putString("name", str3);
                bundle.putString("icon", str);
                bundle.putBoolean("newdownload", true);
                bundle.putInt("secs", Integer.parseInt(str4));
                intent.putExtras(bundle);
                PlayerWebViewActivity.this.sendBroadcast(intent);
            }
        }
    }

    private void loadCover(ImageView imageView, String str) {
    }

    public void clickForFullScreen() {
    }

    public boolean getDetailOrientationRotateAuto() {
        return false;
    }

    public int getPlayerHeight() {
        return 200;
    }

    public PlayerWebViewActivity() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
        stringBuilder.append(File.separator);
        stringBuilder.append("apks/release.apk");
        this.apkPath = stringBuilder.toString();
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
        setContentView((int) R.layout.activity_player_web_view);
        getSupportActionBar().hide();
        this.mWebview = (WebView) findViewById(R.id.webview);
        this.webPlayer = (PreViewGSYVideoPlayer) findViewById(R.id.web_player);
        this.webTopLayout = (NestedScrollView) findViewById(R.id.web_top_layout);
        this.webTopLayoutVideo = (RelativeLayout) findViewById(R.id.web_top_layout_video);
        this.btnGoback = (ImageView) findViewById(R.id.btn_goback);
        findViewById(R.id.btn_next_video).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PlayerWebViewActivity.this.downloadMovie("");
                PlayerWebViewActivity.this.startDownloadManagerActivity();
            }
        });
        findViewById(R.id.btn_goback).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PlayerWebViewActivity.this.orientationUtils.setEnable(false);
                PlayerWebViewActivity.this.mWebview.loadUrl("javascript:goback();");
                PlayerWebViewActivity.this.getGSYVideoPlayer().onVideoPause();
            }
        });
        this.backupRendType = GSYVideoType.getRenderType();
        resolveNormalVideoUI();
        initVideoBuilderMode();
        List arrayList = new ArrayList();
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
        GSYVideoManager.instance().setOptionModelList(arrayList);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        this.webPlayer.setLockClickListener(new LockClickListener() {
            public void onClick(View view, boolean z) {
                if (PlayerWebViewActivity.this.orientationUtils != null) {
                    PlayerWebViewActivity.this.orientationUtils.setEnable(true);
                    PlayerWebViewActivity.this.webPlayer.getCurrentPlayer().setRotateViewAuto(true);
                }
            }
        });
        this.webPlayer.setBackFromFullScreenListener(new OnClickListener() {
            public void onClick(View view) {
                PlayerWebViewActivity.this.orientationUtils.setEnable(false);
                PlayerWebViewActivity.this.webPlayer.onBackFullscreen();
                PlayerWebViewActivity.this.orientationUtils.setEnable(false);
            }
        });
        setupWebview();
        showVideoPlayer(false);
        startDownloadService();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.orientationUtils.setEnable(false);
    }

    public void showVideoPlayer(boolean z) {
        if (z) {
            this.webPlayer.setVisibility(View.VISIBLE);
            this.btnGoback.setVisibility(View.VISIBLE);
            return;
        }
        this.webPlayer.onVideoPause();
        this.webPlayer.setVisibility(View.GONE);
        this.btnGoback.setVisibility(View.GONE);
        getGSYVideoPlayer().onVideoReset();
    }

    public void playVideo(String str, String str2) {
        getGSYVideoPlayer().onVideoPause();
        List arrayList = new ArrayList();
        String[] split = str.split("\\$");
        if (split.length == 4) {
            this.playUrl = split[0];
            arrayList.add(new SwitchVideoModel("自动", split[0]));
            arrayList.add(new SwitchVideoModel("超清(会员专享)", split[1]));
            arrayList.add(new SwitchVideoModel("标清", split[2]));
            arrayList.add(new SwitchVideoModel("流畅", split[3]));
        } else if (split.length == 1) {
            this.playUrl = str;
            arrayList.add(new SwitchVideoModel("自动", str));
        } else {
            this.playUrl = "";
        }
        this.videoName = str2;
        getGSYVideoPlayer().onVideoReset();
        showVideoPlayer(true);
        ((PreViewGSYVideoPlayer) getGSYVideoPlayer()).setUp(arrayList, true, str2);
        getGSYVideoOptionBuilder().setVideoAllCallBack(this).build(getGSYVideoPlayer());
        getGSYVideoPlayer().startPlayLogic();
    }

    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return this.webPlayer;
    }

    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        ImageView imageView = new ImageView(this);
        loadCover(imageView, this.playUrl);
        return new GSYVideoOptionBuilder().
                setThumbImageView(imageView).setUrl(this.playUrl)
                .setCacheWithPlay(false).setRotateWithSystem(false)
                .setVideoTitle(this.videoName).setIsTouchWiget(true)
                .setRotateViewAuto(false).setLockLand(false)
                .setShowFullAnimation(false).setNeedLockFull(true);
    }

    private void resolveNormalVideoUI() {
        this.webPlayer.getTitleTextView().setVisibility(View.GONE);
        this.webPlayer.getBackButton().setVisibility(View.GONE);
        this.webPlayer.setEnlargeImageRes(R.mipmap.custom_enlarge);
        this.webPlayer.setShrinkImageRes(R.mipmap.custom_shrink);
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
        this.mWebSettings.setAllowContentAccess(true);
        this.mWebSettings.setMediaPlaybackRequiresUserGesture(false);
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
                    PlayerWebViewActivity.this.startActivity(intent);
                    return false;
                }
                webView.loadUrl(str);
                return true;
            }

            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                String uri = webResourceRequest.getUrl().toString();
                if (uri.startsWith("xhttp://") || uri.startsWith("xhttps://")) {
                    String replace;
                    if (uri.startsWith("xhttp://")) {
                        replace = uri.replace("xhttp://", "http://");
                    } else {
                        replace = uri.replace("xhttps://", "https://");
                    }
                    PlayerWebViewActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(replace)));
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
            }

            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (str.equals("about:blank")) {
                    PlayerWebViewActivity.this.updateWebUrl();
                    return;
                }
                PlayerWebViewActivity.this.hideMessage();
                PlayerWebViewActivity.this.requestScreenShot(false);
            }
        });
        this.mWebview.setWebChromeClient(new WebChromeClient() {
            public View getVideoLoadingProgressView() {
                View frameLayout = new FrameLayout(PlayerWebViewActivity.this);
                frameLayout.setLayoutParams(new LayoutParams(-1, -1));
                return frameLayout;
            }

            public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
                PlayerWebViewActivity.this.showCustomView(view, customViewCallback);
                PlayerWebViewActivity.this.setRequestedOrientation(0);
            }

            public void onHideCustomView() {
                PlayerWebViewActivity.this.hideCustomView();
                PlayerWebViewActivity.this.setRequestedOrientation(1);
            }

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

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                Log.d(PlayerWebViewActivity.TAG, "onShowFileChooser");
                if (PlayerWebViewActivity.this.mFilePathCallback != null) {
                    PlayerWebViewActivity.this.mFilePathCallback.onReceiveValue(null);
                }
                PlayerWebViewActivity.this.mFilePathCallback = valueCallback;
                try {
                    PlayerWebViewActivity.this.startActivityForResult(fileChooserParams.createIntent(), 1);
                    return true;
                } catch (ActivityNotFoundException unused) {
                    PlayerWebViewActivity.this.mUploadMessage = null;
                    PlayerWebViewActivity.this.mFilePathCallback = null;
                    Toast.makeText(PlayerWebViewActivity.this.getBaseContext(), "Cannot Open File Chooser", 1).show();
                    return false;
                }
            }

            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                Log.d(PlayerWebViewActivity.TAG, "openFileChooser1");
                PlayerWebViewActivity.this.mUploadMessage = valueCallback;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                PlayerWebViewActivity.this.startActivityForResult(Intent.createChooser(intent, "Image Chooser"), 2);
            }

            public void openFileChooser(ValueCallback valueCallback, String str) {
                Log.d(PlayerWebViewActivity.TAG, "openFileChooser2");
                PlayerWebViewActivity.this.mUploadMessage = valueCallback;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                PlayerWebViewActivity.this.startActivityForResult(Intent.createChooser(intent, "Image Chooser"), 2);
            }

            public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
                Log.d(PlayerWebViewActivity.TAG, "openFileChooser3");
                PlayerWebViewActivity.this.mUploadMessage = valueCallback;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                PlayerWebViewActivity.this.startActivityForResult(Intent.createChooser(intent, "Image Chooser"), 2);
            }
        });
        getWebUrl();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.appURL);
        stringBuilder.append("/v.html?t=");
        stringBuilder.append(System.currentTimeMillis());
        checkUrl(stringBuilder.toString());
    }

    private void showCustomView(View view, CustomViewCallback customViewCallback) {
        if (this.customView != null) {
            customViewCallback.onCustomViewHidden();
            return;
        }
        getWindow().getDecorView();
        FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView();
        this.fullscreenContainer = new FullscreenHolder(this);
        this.fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        frameLayout.addView(this.fullscreenContainer, COVER_SCREEN_PARAMS);
        this.customView = view;
        setStatusBarVisibility(false);
        this.customViewCallback = customViewCallback;
    }

    private void hideCustomView() {
        if (this.customView != null) {
            setStatusBarVisibility(true);
            ((FrameLayout) getWindow().getDecorView()).removeView(this.fullscreenContainer);
            this.fullscreenContainer = null;
            this.customView = null;
            this.customViewCallback.onCustomViewHidden();
            this.mWebview.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusBarVisibility(boolean z) {
        getWindow().setFlags(z ? 0 : 1024, 1024);
    }

    private void hideMessage() {
        findViewById(R.id.messageLabel).setVisibility(8);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.customView != null) {
            hideCustomView();
        }
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
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.PlayerWebViewActivity.LoadConfig():java.lang.String");
    }

    public void downloadMovie(String str) {
        String[] split = "https://www3.laqddc.com/hls/2018/04/07/BQ2cqpyZ/playlist.m3u8$测试影片1测试影片1测试影片1测试影片1测试影片1测试影片1测试影片1测试影片1测试影片1$1000$http://r1.ykimg.com/050C00005B728474ADBAC3391F08F85F?x-oss-process=image/resize,w_290/interlace,1/quality,Q_100/sharpen,100".split("\\$");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(split.length);
        stringBuilder.append("");
        Log.d("params.length ", stringBuilder.toString());
        if (split.length == 4) {
            String str2 = split[0];
            String str3 = split[1];
            String str4 = split[2];
            str = split[3];
            Intent intent = new Intent();
            intent.setAction("kankan.download.m3u8");
            Bundle bundle = new Bundle();
            bundle.putString("url", str2);
            bundle.putBoolean("downloadNow", true);
            bundle.putString("name", str3);
            bundle.putString("icon", str);
            bundle.putBoolean("newdownload", true);
            bundle.putInt("secs", Integer.parseInt(str4));
            intent.putExtras(bundle);
            sendBroadcast(intent);
        }
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

    public void onActivityResult(int i, int i2, Intent intent) {
        Log.d(TAG, "onActivityResult");
        Uri[] path;
        if (i == 2) {
            if (this.mUploadMessage != null) {
                Uri data = (intent == null || i2 != -1) ? null : intent.getData();
                if (data != null) {
                    String path2 = ImageFilePath.getPath(this, data);
                    if (!TextUtils.isEmpty(path2)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("file:///");
                        stringBuilder.append(path2);
                        data = Uri.parse(stringBuilder.toString());
                    }
                }
                this.mUploadMessage.onReceiveValue(data);
                this.mUploadMessage = null;
            }
        } else if (i != 1 || this.mFilePathCallback == null) {
            super.onActivityResult(i, i2, intent);
        } else {
            if (i2 == -1) {
                String str;
                if (intent == null) {
                    str = this.mCameraPhotoPath;
                    if (str != null) {
                        Log.d("camera_photo_path", str);
                        path = new Uri[]{Uri.parse(this.mCameraPhotoPath)};
                        this.mFilePathCallback.onReceiveValue(path);
                        this.mFilePathCallback = null;
                    }
                } else {
                    str = intent.getDataString();
                    Log.d("camera_dataString", str);
                    if (str != null) {
                        path = new Uri[]{Uri.parse(str)};
                        this.mFilePathCallback.onReceiveValue(path);
                        this.mFilePathCallback = null;
                    }
                }
            }
            path = null;
            this.mFilePathCallback.onReceiveValue(path);
            this.mFilePathCallback = null;
        }
    }

    private String getWebUrl() {
        String ReadURLJsonString = ReadURLJsonString();
        if (ReadURLJsonString != null) {
            this.appURL = parserAPI(ReadURLJsonString, this.apiURLs);
        } else {
            this.appURL = "https://www.jw1649.com";
            this.apiURLs.add("https://www.jw1649.com/api.json");
            this.apiURLs.add("https://www.lemaifan.com/api.json");
            this.apiURLs.add("https://www.magicquan.com/api.json");
            this.apiURLs.add("https://www.taoke800.com/api.json");
            this.apiURLs.add("https://www.yidianyx.com/api.json");
            this.apiURLs.add("https://github.com/yasekingdom/heyjjj/issues/1");
        }
        return this.appURL;
    }

    private void updateWebUrl() {
        if (this.currentAPIIndex < this.apiURLs.size()) {
            getAPI((String) this.apiURLs.get(this.currentAPIIndex));
        }
    }

    private void getAPI(String str) {
        new Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build().newCall(new Request.Builder().url(str).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("XTIMEOUT...", iOException.getMessage());
                if (!(iOException instanceof SocketTimeoutException)) {
                    boolean z = iOException instanceof ConnectException;
                }
                PlayerWebViewActivity.this.currentAPIIndex = PlayerWebViewActivity.this.currentAPIIndex + 1;
                Message message = new Message();
                message.what = 769;
                PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message;
                if (!response.isSuccessful()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(response.code());
                    stringBuilder.append("");
                    Log.e("XTIMEOUT...", stringBuilder.toString());
                    PlayerWebViewActivity.this.currentAPIIndex = PlayerWebViewActivity.this.currentAPIIndex + 1;
                    message = new Message();
                    message.what = 769;
                    PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
                } else if (PlayerWebViewActivity.this.currentAPIIndex < PlayerWebViewActivity.this.apiURLs.size() - 1 || PlayerWebViewActivity.this.lastURL.length() != 0) {
                    PlayerWebViewActivity playerWebViewActivity = PlayerWebViewActivity.this;
                    playerWebViewActivity.appURL = playerWebViewActivity.parserAPI(string, playerWebViewActivity.apiURLs);
                    if (PlayerWebViewActivity.this.appURL != null) {
                        PlayerWebViewActivity.this.currentAPIIndex = 0;
                        PlayerWebViewActivity.this.SaveURLJsonString(string);
                        message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("appUrl", PlayerWebViewActivity.this.appURL);
                        message.setData(bundle);
                        message.what = 768;
                        PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
                        return;
                    }
                    PlayerWebViewActivity.this.currentAPIIndex = PlayerWebViewActivity.this.currentAPIIndex + 1;
                    message = new Message();
                    message.what = 769;
                    PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
                } else if (PlayerWebViewActivity.this.lastURL.equals("")) {
                    Matcher matcher = Pattern.compile("<meta name=\"description\" content=\"(.+?)\">").matcher(string);
                    while (matcher.find()) {
                        String[] split = matcher.group(1).split("\\-");
                        byte[] bArr = new byte[split.length];
                        int i = 0;
                        while (i < split.length && MyUtils.isNumeric(split[i])) {
                            bArr[i] = (byte) Integer.parseInt(split[i]);
                            i++;
                        }
                        PlayerWebViewActivity.this.lastURL = Des.encode(new String(bArr));
                        if (PlayerWebViewActivity.this.lastURL == null || !PlayerWebViewActivity.this.lastURL.startsWith("http")) {
                            PlayerWebViewActivity.this.lastURL = "";
                        } else {
                            Message message2 = new Message();
                            message2.what = 770;
                            PlayerWebViewActivity.this.refreshHandler.sendMessage(message2);
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
        try {
            JSONObject jSONObject = new JSONObject(str);
            str = jSONObject.getString("web");
            JSONArray jSONArray = jSONObject.getJSONArray("apis");
            arrayList.clear();
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
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.PlayerWebViewActivity.SaveURLJsonString(java.lang.String):void");
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
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.PlayerWebViewActivity.ReadURLJsonString():java.lang.String");
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

    private void toast(String str) {
        if (str != null) {
            Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 4096) {
            int i2 = iArr[0];
        }
        if (i == 4096) {
            if (iArr[0] != 0) {
                toast("很遗憾，您拒接了存储权限，无法为您保存相关数据。");
            } else if (this.startShotter) {
                saveBitmapForSdCard(this, captureScreenWindow());
            }
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    public Bitmap captureScreenWindow() {
        getWindow().getDecorView().setDrawingCacheEnabled(true);
        return getWindow().getDecorView().getDrawingCache();
    }

    public void saveBitmapForSdCard(Activity activity, Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SystemClock.currentThreadTimeMillis());
        stringBuilder.append(".jpg");
        saveBmp2Gallery(bitmap, stringBuilder.toString());
        this.startShotter = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0059 A:{SYNTHETIC, Splitter: B:22:0x0059} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0059 A:{SYNTHETIC, Splitter: B:22:0x0059} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0079 A:{SYNTHETIC, Splitter: B:27:0x0079} */
    public void saveBmp2Gallery(Bitmap r4, String r5) {
        /*
        r3 = this;
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
        r2 = new java.io.File;	 Catch:{ Exception -> 0x0052 }
        r2.<init>(r0, r5);	 Catch:{ Exception -> 0x0052 }
        r5 = r2.toString();	 Catch:{ Exception -> 0x004e }
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x004e }
        r0.<init>(r5);	 Catch:{ Exception -> 0x004e }
        r5 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x004b, all -> 0x0048 }
        r1 = 90;
        r4.compress(r5, r1, r0);	 Catch:{ Exception -> 0x004b, all -> 0x0048 }
        r0.close();	 Catch:{ IOException -> 0x0043 }
        goto L_0x005c;
    L_0x0043:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x005c;
    L_0x0048:
        r4 = move-exception;
        r1 = r0;
        goto L_0x0077;
    L_0x004b:
        r4 = move-exception;
        r1 = r0;
        goto L_0x0054;
    L_0x004e:
        r4 = move-exception;
        goto L_0x0054;
    L_0x0050:
        r4 = move-exception;
        goto L_0x0077;
    L_0x0052:
        r4 = move-exception;
        r2 = r1;
    L_0x0054:
        r4.getStackTrace();	 Catch:{ all -> 0x0050 }
        if (r1 == 0) goto L_0x005c;
    L_0x0059:
        r1.close();	 Catch:{ IOException -> 0x0043 }
    L_0x005c:
        r4 = new android.content.Intent;
        r5 = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
        r4.<init>(r5);
        r5 = android.net.Uri.fromFile(r2);
        r4.setData(r5);
        r5 = com.yase999.app.MyApplication.getContext();
        r5.sendBroadcast(r4);
        r4 = "保存成功，请在相册中查看分享二维码！";
        r3.toast(r4);
        return;
    L_0x0077:
        if (r1 == 0) goto L_0x0081;
    L_0x0079:
        r1.close();	 Catch:{ IOException -> 0x007d }
        goto L_0x0081;
    L_0x007d:
        r5 = move-exception;
        r5.printStackTrace();
    L_0x0081:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.PlayerWebViewActivity.saveBmp2Gallery(android.graphics.Bitmap, java.lang.String):void");
    }

    public void startCheckTimeout() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(InputDeviceCompat.SOURCE_KEYBOARD);
                PlayerWebViewActivity.this.timer.cancel();
                PlayerWebViewActivity.this.timer.purge();
            }
        }, this.timeout, 1);
    }

    public void postComment(final String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                WebView webView = PlayerWebViewActivity.this.mWebview;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("javascript:postComment(\"");
                stringBuilder.append(str);
                stringBuilder.append("\");");
                webView.loadUrl(stringBuilder.toString());
            }
        });
    }

    public void installApp(final String str, final String str2) {
        runOnUiThread(new Runnable() {
            public void run() {
                PlayerWebViewActivity playerWebViewActivity = PlayerWebViewActivity.this;
                String str = str2;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("安装 ");
                stringBuilder.append(str);
                playerWebViewActivity.startDownloadApk(str, stringBuilder.toString(), "正在下载新版本");
            }
        });
    }

    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_custom);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_common, null);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        TextView textView = (TextView) inflate.findViewById(R.id.titleTv);
        TextView textView2 = (TextView) inflate.findViewById(R.id.confirmBtn);
        TextView textView3 = (TextView) inflate.findViewById(R.id.cancelBtn);
        final EditText editText = (EditText) inflate.findViewById(R.id.editText);
        textView.setText("     请输入您的评论");
        textView2.setText("发布");
        textView3.setText("取消");
        textView3.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((InputMethodManager) editText.getContext()
                        .getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                create.dismiss();
            }
        });
        textView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ((InputMethodManager) editText.getContext()
                        .getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                create.dismiss();
                PlayerWebViewActivity.this.postComment(editText.getText().toString());
            }
        });
        create.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                ((InputMethodManager) editText.getContext()
                        .getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
        create.show();
        ((InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText, 2);
        new Timer().schedule(new TimerTask() {
            public void run() {
                ((InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText, 2);
            }
        }, 200);
    }

    private void checkUrl(String str) {
        Log.e("XTIMEOUT...", str);
        new Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(2, TimeUnit.SECONDS).build().newCall(new Request.Builder().url(str).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("XTIMEOUT...", iOException.getMessage());
                PlayerWebViewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
                PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(InputDeviceCompat.SOURCE_KEYBOARD);
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                PlayerWebViewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
                if (string.equals("1")) {
                    Message message = new Message();
                    message.what = 2048;
                    PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
                    return;
                }
                PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(InputDeviceCompat.SOURCE_KEYBOARD);
            }
        });
    }

    private void checkVersion(String str) {
        this.newVersionUrl = "";
        new Builder().connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS).build()
                .newCall(new Request.Builder().url(str).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(2);
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                String[] split = string.split("\\$");
                if (!string.startsWith("version=") || split.length <= 2) {
                    PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(2);
                    return;
                }
                if (!split[0].equals(PlayerWebViewActivity.this.versionKey)) {
                    PlayerWebViewActivity.this.newVersionUrl = split[1];
                    Message message = new Message();
                    message.what = 3;
                    message.arg1 = split[2].equals("1");
                    PlayerWebViewActivity.this.refreshHandler.sendMessage(message);
                }
            }
        });

    }

    private void showUpdateDialog(final String str, boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本升级").setCancelable(true).setIcon(R.mipmap.ic_launcher).setMessage("发现新版本！请及时更新").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                PlayerWebViewActivity.this.startDownloadApk(str, "版本升级中", "正在下载新版本");
            }
        });
        if (!z) {
            builder.setNegativeButton("取消", null);
        }
        builder.create().show();
    }

    private void startDownloadApk(String str, String str2, String str3) {
        this.downloadDialog = new ProgressDialog(this);
        this.downloadDialog.setTitle(str2);
        this.downloadDialog.setProgressStyle(1);
        ProgressDialog progressDialog = this.downloadDialog;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(str3);
        progressDialog.setMessage(stringBuilder.toString());
        this.downloadDialog.setCancelable(false);
        this.downloadDialog.show();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(Environment.getExternalStorageDirectory().getPath());
        stringBuilder2.append(File.separator);
        stringBuilder2.append("apks");
        StorageUtils.makeDir(stringBuilder2.toString());
        final File file = new File(this.apkPath);
        stringBuilder = new StringBuilder();
        stringBuilder.append("path=");
        stringBuilder.append(this.apkPath);
        Log.e("DownloadApk", stringBuilder.toString());
        new Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(2, TimeUnit.SECONDS).build().newCall(new Request.Builder().url(str).get().build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                PlayerWebViewActivity.this.refreshHandler.sendEmptyMessage(4);
            }

            /* JADX WARNING: Removed duplicated region for block: B:37:0x0091 A:{SYNTHETIC, Splitter: B:37:0x0091} */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x0099 A:{Catch:{ IOException -> 0x0095 }} */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0077 A:{SYNTHETIC, Splitter: B:28:0x0077} */
            /* JADX WARNING: Removed duplicated region for block: B:46:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x007f A:{Catch:{ IOException -> 0x007b }} */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x0091 A:{SYNTHETIC, Splitter: B:37:0x0091} */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x0099 A:{Catch:{ IOException -> 0x0095 }} */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0077 A:{SYNTHETIC, Splitter: B:28:0x0077} */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x007f A:{Catch:{ IOException -> 0x007b }} */
            /* JADX WARNING: Removed duplicated region for block: B:46:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x0091 A:{SYNTHETIC, Splitter: B:37:0x0091} */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x0099 A:{Catch:{ IOException -> 0x0095 }} */
            public void onResponse(okhttp3.Call r9, okhttp3.Response r10) throws IOException {
                /*
                r8 = this;
                r9 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
                r9 = new byte[r9];
                r0 = 0;
                r1 = r10.body();	 Catch:{ IOException -> 0x0060, all -> 0x005c }
                r1 = r1.contentLength();	 Catch:{ IOException -> 0x0060, all -> 0x005c }
                r3 = 0;
                r10 = r10.body();	 Catch:{ IOException -> 0x0060, all -> 0x005c }
                r10 = r10.byteStream();	 Catch:{ IOException -> 0x0060, all -> 0x005c }
                r5 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0058, all -> 0x0055 }
                r6 = r5;	 Catch:{ IOException -> 0x0058, all -> 0x0055 }
                r5.<init>(r6);	 Catch:{ IOException -> 0x0058, all -> 0x0055 }
            L_0x001e:
                r0 = r10.read(r9);	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6 = -1;
                if (r0 == r6) goto L_0x0045;
            L_0x0025:
                r6 = (long) r0;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r3 = r3 + r6;
                r6 = 0;
                r5.write(r9, r6, r0);	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r0 = new android.os.Message;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r0.<init>();	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6 = 5;
                r0.what = r6;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6 = 100;
                r6 = r6 * r3;
                r6 = r6 / r1;
                r6 = (int) r6;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r0.arg1 = r6;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6 = com.yase999.app.PlayerWebViewActivity.this;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6 = r6.refreshHandler;	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                r6.sendMessage(r0);	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                goto L_0x001e;
            L_0x0045:
                r5.flush();	 Catch:{ IOException -> 0x0053, all -> 0x0051 }
                if (r10 == 0) goto L_0x004d;
            L_0x004a:
                r10.close();	 Catch:{ IOException -> 0x007b }
            L_0x004d:
                r5.close();	 Catch:{ IOException -> 0x007b }
                goto L_0x008c;
            L_0x0051:
                r9 = move-exception;
                goto L_0x008f;
            L_0x0053:
                r9 = move-exception;
                goto L_0x005a;
            L_0x0055:
                r9 = move-exception;
                r5 = r0;
                goto L_0x008f;
            L_0x0058:
                r9 = move-exception;
                r5 = r0;
            L_0x005a:
                r0 = r10;
                goto L_0x0062;
            L_0x005c:
                r9 = move-exception;
                r10 = r0;
                r5 = r10;
                goto L_0x008f;
            L_0x0060:
                r9 = move-exception;
                r5 = r0;
            L_0x0062:
                r10 = "DownloadApk";
                r9 = r9.toString();	 Catch:{ all -> 0x008d }
                android.util.Log.e(r10, r9);	 Catch:{ all -> 0x008d }
                r9 = com.yase999.app.PlayerWebViewActivity.this;	 Catch:{ all -> 0x008d }
                r9 = r9.refreshHandler;	 Catch:{ all -> 0x008d }
                r10 = 4;
                r9.sendEmptyMessage(r10);	 Catch:{ all -> 0x008d }
                if (r0 == 0) goto L_0x007d;
            L_0x0077:
                r0.close();	 Catch:{ IOException -> 0x007b }
                goto L_0x007d;
            L_0x007b:
                r9 = move-exception;
                goto L_0x0083;
            L_0x007d:
                if (r5 == 0) goto L_0x008c;
            L_0x007f:
                r5.close();	 Catch:{ IOException -> 0x007b }
                goto L_0x008c;
            L_0x0083:
                r10 = "DownloadApk";
                r9 = r9.toString();
                android.util.Log.e(r10, r9);
            L_0x008c:
                return;
            L_0x008d:
                r9 = move-exception;
                r10 = r0;
            L_0x008f:
                if (r10 == 0) goto L_0x0097;
            L_0x0091:
                r10.close();	 Catch:{ IOException -> 0x0095 }
                goto L_0x0097;
            L_0x0095:
                r10 = move-exception;
                goto L_0x009d;
            L_0x0097:
                if (r5 == 0) goto L_0x00a6;
            L_0x0099:
                r5.close();	 Catch:{ IOException -> 0x0095 }
                goto L_0x00a6;
            L_0x009d:
                r10 = r10.toString();
                r0 = "DownloadApk";
                android.util.Log.e(r0, r10);
            L_0x00a6:
                throw r9;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.yase999.app.PlayerWebViewActivity.19.onResponse(okhttp3.Call, okhttp3.Response):void");
            }
        });
    }
}
