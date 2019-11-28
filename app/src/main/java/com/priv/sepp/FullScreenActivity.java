package com.priv.sepp;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.priv.sepp.widget.BaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import m3u8downloader.server.EncryptM3U8Server;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class FullScreenActivity extends BaseActivityDetail<StandardGSYVideoPlayer> {
    public static final String IMG_TRANSITION = "IMG_TRANSITION";
    public static final String TRANSITION = "TRANSITION";
    private int backupRendType;
    private boolean isTransition;
    private EncryptM3U8Server m3u8Server = new EncryptM3U8Server();
    private OrientationUtils orientationUtils;
    private String playUrl;
    private Transition transition;
    private String videoName;
    private PreViewGSYVideoPlayer videoPlayer;

    public void clickForFullScreen() {
    }

    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    protected void onCreate(@Nullable Bundle bundle) {
        String string;
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_full_screen);
        getSupportActionBar().hide();
        this.videoPlayer = (PreViewGSYVideoPlayer) findViewById(R.id.web_player);
        this.videoPlayer.getCurrentPlayer().setEnlargeImageRes(R.mipmap.custom_enlarge);
        this.videoPlayer.getCurrentPlayer().setShrinkImageRes(R.mipmap.custom_shrink);
        Boolean.valueOf(false);
        Bundle extras = getIntent().getExtras();
        String str = null;
        if (extras != null) {
            str = extras.getString("M3U8_URL");
            Boolean.valueOf(extras.getBoolean("online"));
            string = extras.getString("videoname");
        } else {
            string = null;
        }
        this.m3u8Server.execute();
        if (string == null) {
            string = "";
        }
        this.playUrl = this.m3u8Server.createLocalHttpUrl(str);
        this.videoName = string;
        GSYVideoType.setRenderType(1);
        resolveNormalVideoUI();
        initVideoBuilderMode();
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        this.videoPlayer.getTitleTextView().setVisibility(View.GONE);
        this.orientationUtils = new OrientationUtils(this, this.videoPlayer);
        this.orientationUtils.setEnable(false);
        this.videoPlayer.setLockClickListener(new LockClickListener() {
            public void onClick(View view, boolean z) {
                if (FullScreenActivity.this.orientationUtils != null) {
                    FullScreenActivity.this.orientationUtils.setEnable(true);
                    FullScreenActivity.this.videoPlayer.getCurrentPlayer().setRotateViewAuto(true);
                }
            }
        });
        this.videoPlayer.getFullscreenButton().setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(FullScreenActivity.this, "点击了返回", Toast.LENGTH_LONG).show();
            }
        });
        this.videoPlayer.getBackButton().setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(FullScreenActivity.this, "点击了返回", Toast.LENGTH_LONG).show();
            }
        });
        this.videoPlayer.setBackFromFullScreenListener(new OnClickListener() {
            public void onClick(View view) {
                FullScreenActivity.this.finish();
            }
        });
        showFull();
    }

    protected void onResume() {
        super.onResume();
        this.m3u8Server.decrypt();
    }

    protected void onPause() {
        super.onPause();
        this.m3u8Server.encrypt();
    }

    public void onBackPressed() {
        this.videoPlayer.onVideoPause();
        this.videoPlayer.onVideoReset();
        this.videoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        super.onBackPressed();
        finish();
    }

    private void initTransition() {
        if (!this.isTransition || VERSION.SDK_INT < 21) {
            this.videoPlayer.startPlayLogic();
            return;
        }
        postponeEnterTransition();
        ViewCompat.setTransitionName(this.videoPlayer, IMG_TRANSITION);
        startPostponedEnterTransition();
    }

    private void resolveNormalVideoUI() {
        this.videoPlayer.getTitleTextView().setVisibility(View.GONE);
        this.videoPlayer.getBackButton().setVisibility(View.GONE);
        this.videoPlayer.setEnlargeImageRes(R.mipmap.custom_enlarge);
        this.videoPlayer.setShrinkImageRes(R.mipmap.custom_shrink);
    }

    public void playVideo(String str, String str2) {
        getGSYVideoPlayer().onVideoPause();
        this.playUrl = str;
        this.videoName = str2;
        getGSYVideoPlayer().onVideoReset();
        getGSYVideoOptionBuilder().setVideoAllCallBack(this).build(getGSYVideoPlayer());
        getGSYVideoPlayer().startPlayLogic();
    }

    protected void onDestroy() {
        super.onDestroy();
        OrientationUtils orientationUtils = this.orientationUtils;
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        this.m3u8Server.finish();
    }

    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return this.videoPlayer;
    }

    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        return new GSYVideoOptionBuilder().setThumbImageView(new ImageView(this)).setUrl(this.playUrl).setCacheWithPlay(false).setRotateWithSystem(false).setVideoTitle(this.videoName).setIsTouchWiget(true).setRotateViewAuto(false).setAutoFullWithSize(true).setLockLand(false).setShowFullAnimation(false).setNeedLockFull(true);
    }
}
