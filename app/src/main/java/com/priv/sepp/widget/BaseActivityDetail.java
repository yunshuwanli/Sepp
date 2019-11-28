package com.priv.sepp.widget;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

public abstract class BaseActivityDetail<T extends GSYBaseVideoPlayer> extends AppCompatActivity implements VideoAllCallBack {
    protected boolean isPause;
    protected boolean isPlay;
    protected OrientationUtils orientationUtils;

    public abstract void clickForFullScreen();

    public abstract boolean getDetailOrientationRotateAuto();

    public abstract GSYVideoOptionBuilder getGSYVideoOptionBuilder();

    public abstract T getGSYVideoPlayer();

    public boolean hideActionBarWhenFull() {
        return true;
    }

    public boolean hideStatusBarWhenFull() {
        return true;
    }

    public boolean isAutoFullWithSize() {
        return true;
    }

    public void onAutoComplete(String str, Object... objArr) {
    }

    public void onClickBlank(String str, Object... objArr) {
    }

    public void onClickBlankFullscreen(String str, Object... objArr) {
    }

    public void onClickResume(String str, Object... objArr) {
    }

    public void onClickResumeFullscreen(String str, Object... objArr) {
    }

    public void onClickSeekbar(String str, Object... objArr) {
    }

    public void onClickSeekbarFullscreen(String str, Object... objArr) {
    }

    public void onClickStartError(String str, Object... objArr) {
    }

    public void onClickStartIcon(String str, Object... objArr) {
    }

    public void onClickStartThumb(String str, Object... objArr) {
    }

    public void onClickStop(String str, Object... objArr) {
    }

    public void onClickStopFullscreen(String str, Object... objArr) {
    }

    public void onEnterFullscreen(String str, Object... objArr) {
    }

    public void onEnterSmallWidget(String str, Object... objArr) {
    }

    public void onPlayError(String str, Object... objArr) {
    }

    public void onQuitSmallWidget(String str, Object... objArr) {
    }

    public void onStartPrepared(String str, Object... objArr) {
    }

    public void onTouchScreenSeekLight(String str, Object... objArr) {
    }

    public void onTouchScreenSeekPosition(String str, Object... objArr) {
    }

    public void onTouchScreenSeekVolume(String str, Object... objArr) {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void initVideo() {
        this.orientationUtils = new OrientationUtils(this, getGSYVideoPlayer());
        this.orientationUtils.setEnable(false);
        if (getGSYVideoPlayer().getFullscreenButton() != null) {
            getGSYVideoPlayer().getFullscreenButton().setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    BaseActivityDetail.this.showFull();
                    BaseActivityDetail.this.clickForFullScreen();
                }
            });
        }
    }

    public void initVideoBuilderMode() {
        initVideo();
        getGSYVideoOptionBuilder().setVideoAllCallBack(this).build(getGSYVideoPlayer());
        getGSYVideoPlayer().startPlayLogic();
    }

    public void showFull() {
        if (this.orientationUtils.getIsLand() != 1) {
            this.orientationUtils.resolveByClick();
        }
        getGSYVideoPlayer().startWindowFullscreen(this, hideActionBarWhenFull(), hideStatusBarWhenFull());
    }

    public void onBackPressed() {
        OrientationUtils orientationUtils = this.orientationUtils;
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (!GSYVideoManager.backFromWindowFull(this)) {
            super.onBackPressed();
        }
    }

    protected void onPause() {
        super.onPause();
        getGSYVideoPlayer().getCurrentPlayer().onVideoPause();
        this.isPause = true;
    }

    protected void onResume() {
        super.onResume();
        getGSYVideoPlayer().getCurrentPlayer().onVideoResume();
        this.isPause = false;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.isPlay) {
            getGSYVideoPlayer().getCurrentPlayer().release();
        }
        OrientationUtils orientationUtils = this.orientationUtils;
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.isPlay && !this.isPause) {
            getGSYVideoPlayer().onConfigurationChanged(this, configuration, this.orientationUtils, hideActionBarWhenFull(), hideStatusBarWhenFull());
        }
        getSupportActionBar().hide();
    }

    public void onPrepared(String str, Object... objArr) {
        OrientationUtils orientationUtils = this.orientationUtils;
        if (orientationUtils != null) {
            boolean z = getDetailOrientationRotateAuto() && !isAutoFullWithSize();
            orientationUtils.setEnable(z);
            this.isPlay = true;
            return;
        }
        throw new NullPointerException("initVideo() or initVideoBuilderMode() first");
    }

    public void onQuitFullscreen(String str, Object... objArr) {
        OrientationUtils orientationUtils = this.orientationUtils;
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }
}
