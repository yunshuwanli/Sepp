package com.priv.sepp;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.priv.sepp.widget.BaseActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class WebDetailActivity extends BaseActivityDetail<StandardGSYVideoPlayer> {
    private int backupRendType;
    private boolean isSmall;
    PreViewGSYVideoPlayer webPlayer;
    NestedScrollView webTopLayout;
    RelativeLayout webTopLayoutVideo;
    ScrollWebView webView;

    private void loadCover(ImageView imageView, String str) {
    }

    public void clickForFullScreen() {
    }

    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_web_detail);
        getSupportActionBar().hide();
        this.webView = (ScrollWebView) findViewById(R.id.scroll_webView);
        this.webPlayer = (PreViewGSYVideoPlayer) findViewById(R.id.web_player);
        this.webTopLayout = (NestedScrollView) findViewById(R.id.web_top_layout);
        this.webTopLayoutVideo = (RelativeLayout) findViewById(R.id.web_top_layout_video);
        findViewById(R.id.btn_next_video).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (WebDetailActivity.this.webPlayer.getVisibility() == View.GONE) {
                    WebDetailActivity.this.webPlayer.setVisibility(View.VISIBLE);
                } else {
                    WebDetailActivity.this.webPlayer.setVisibility(View.GONE);
                }
            }
        });
        this.backupRendType = GSYVideoType.getRenderType();
        GSYVideoType.setRenderType(1);
        resolveNormalVideoUI();
        initVideoBuilderMode();
        this.webPlayer.setLockClickListener(new LockClickListener() {
            public void onClick(View view, boolean z) {
                if (WebDetailActivity.this.orientationUtils != null) {
                    WebDetailActivity.this.orientationUtils.setEnable(true);
                    WebDetailActivity.this.webPlayer.getCurrentPlayer().setRotateViewAuto(true);
                }
            }
        });
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.loadUrl("https://www.baidu.com");
    }

    protected void onDestroy() {
        super.onDestroy();
        GSYVideoType.setRenderType(this.backupRendType);
    }

    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return this.webPlayer;
    }

    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        String str = "https://www3.laqddc.com/hls/2018/04/07/BQ2cqpyZ/playlist.m3u8";
        ImageView imageView = new ImageView(this);
        loadCover(imageView, str);
        return new GSYVideoOptionBuilder().setThumbImageView(imageView).setUrl(str)
                .setCacheWithPlay(false).setRotateWithSystem(false).setVideoTitle("测试视频")
                .setIsTouchWiget(true).setRotateViewAuto(false).setLockLand(false)
                .setShowFullAnimation(false).setNeedLockFull(true);
    }

    private void resolveNormalVideoUI() {
        this.webPlayer.getTitleTextView().setVisibility(View.GONE);
        this.webPlayer.getBackButton().setVisibility(View.GONE);
    }
}
