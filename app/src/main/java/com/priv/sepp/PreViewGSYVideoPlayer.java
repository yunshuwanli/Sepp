package com.priv.sepp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.util.List;

public class PreViewGSYVideoPlayer extends NormalGSYVideoPlayer {
    private boolean mIsFromUser;
    private boolean mOpenPreView;
    private int mPreProgress;
    private ImageView mPreView;
    private RelativeLayout mPreviewLayout;
    private int mSourcePosition;
    private TextView mSwitchSize;
    private String mTypeText;

    private void showPreView(String str, long j) {
    }

    private void startDownFrame(String str) {
    }

    public int getLayoutId() {
        return R.layout.video_layout_preview;
    }

    protected int getVolumeLayoutId() {
        return R.layout.video_volume_dialog;
    }

    public PreViewGSYVideoPlayer(Context context, Boolean bool) {
        super(context, bool);
        this.mOpenPreView = true;
        this.mPreProgress = -2;
        this.mSourcePosition = 0;
        this.mTypeText = "标准";
        this.mThreshold = 10;
        if (bool.booleanValue()) {
            findViewById(R.id.switchSize).setVisibility(VISIBLE);
        }
    }

    public PreViewGSYVideoPlayer(Context context) {
        super(context);
        this.mOpenPreView = true;
        this.mPreProgress = -2;
        this.mSourcePosition = 0;
        this.mTypeText = "标准";
        this.mThreshold = 10;
    }

    public PreViewGSYVideoPlayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOpenPreView = true;
        this.mPreProgress = -2;
        this.mSourcePosition = 0;
        this.mTypeText = "标准";
        this.mThreshold = 10;
    }

    protected void init(Context context) {
        super.init(context);
        initView();
    }

    private void initView() {
        this.mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        this.mPreView = (ImageView) findViewById(R.id.preview_image);
        this.mSwitchSize = (TextView) findViewById(R.id.switchSize);
        this.mSwitchSize.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PreViewGSYVideoPlayer.this.showSwitchDialog();
            }
        });
    }

    public boolean setUp(List<SwitchVideoModel> list, boolean z, String str) {
        MyApplication.getUrlList().clear();
        for (int i = 0; i < list.size(); i++) {
            MyApplication.getUrlList().add(list.get(i));
        }
        return setUp(((SwitchVideoModel) list.get(this.mSourcePosition)).getUrl(), z, str);
    }

    protected void touchSurfaceMove(float f, float f2, float f3) {
        int i = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? this.mScreenHeight : this.mScreenWidth;
        int i2 = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? this.mScreenWidth : this.mScreenHeight;
        if (this.mChangePosition) {
            int duration = getDuration();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mDownPosition);
            stringBuilder.append(" -- ");
            stringBuilder.append((int) getGSYVideoManager().getCurrentPosition());
            Log.d("mDownPosition", stringBuilder.toString());
            this.mSeekTimePosition = (int) (((float) this.mDownPosition) + (((((float) 240000) * f) / ((float) i)) / this.mSeekRatio));
            stringBuilder = new StringBuilder();
            stringBuilder.append("deltaX=");
            stringBuilder.append(f);
            stringBuilder.append("  >>>  ");
            stringBuilder.append(this.mSeekTimePosition);
            Log.d("totalTimeDuration", stringBuilder.toString());
            if (this.mSeekTimePosition > duration) {
                this.mSeekTimePosition = duration;
            }
            showProgressDialog(f, CommonUtil.stringForTime(this.mSeekTimePosition), this.mSeekTimePosition, CommonUtil.stringForTime(duration), duration);
        } else if (this.mChangeVolume) {
            f = -f2;
            int streamMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
            float f4 = (float) i2;
            this.mAudioManager.setStreamVolume(3, this.mGestureDownVolume + ((int) (((((float) streamMaxVolume) * f) * 3.0f) / f4)), 0);
            showVolumeDialog(-f, (int) (((float) ((this.mGestureDownVolume * 100) / streamMaxVolume)) + (((3.0f * f) * 100.0f) / f4)));
        } else if (!this.mChangePosition && this.mBrightness && Math.abs(f2) > ((float) this.mThreshold)) {
            onBrightnessSlide((-f2) / ((float) i2));
            this.mDownY = f3;
        }
    }

    protected void touchSurfaceMoveFullLogic(float f, float f2) {
        int i = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? this.mScreenHeight : this.mScreenWidth;
        if (f > ((float) this.mThreshold) || f2 > ((float) this.mThreshold)) {
            if (f < ((float) this.mThreshold)) {
                boolean z = Math.abs(((float) CommonUtil.getScreenHeight(getContext())) - this.mDownY) > ((float) this.mSeekEndOffset);
                if (this.mFirstTouch) {
                    boolean z2 = this.mDownX < ((float) i) * 0.5f && z;
                    this.mBrightness = z2;
                    this.mFirstTouch = false;
                }
                if (!this.mBrightness) {
                    this.mChangeVolume = z;
                    this.mGestureDownVolume = this.mAudioManager.getStreamVolume(3);
                }
                this.mShowVKey = z ^ true;
            } else if (Math.abs(((float) CommonUtil.getScreenWidth(getContext())) - this.mDownX) > ((float) this.mSeekEndOffset)) {
                this.mChangePosition = true;
                this.mDownPosition = (int) getGSYVideoManager().getCurrentPosition();
            } else {
                this.mShowVKey = true;
            }
        }
    }

    protected void touchSurfaceUp() {
        int i;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mDownPosition);
        stringBuilder.append(" -- ");
        stringBuilder.append(this.mSeekTimePosition);
        Log.d("mDownPosition--||111", stringBuilder.toString());
        if (this.mChangePosition) {
            int duration = getDuration();
            i = this.mSeekTimePosition * 100;
            if (duration == 0) {
                duration = 1;
            }
            i /= duration;
            if (this.mBottomProgressBar != null) {
                this.mBottomProgressBar.setProgress(i);
            }
        }
        this.mTouchingProgressBar = false;
        dismissProgressDialog();
        dismissVolumeDialog();
        dismissBrightnessDialog();
        if (this.mChangePosition && getGSYVideoManager() != null && this.mCurrentState < 7) {
            try {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.mDownPosition);
                stringBuilder2.append(" -- ");
                stringBuilder2.append(this.mSeekTimePosition);
                Log.d("mDownPosition--||", stringBuilder2.toString());
                getGSYVideoManager().seekTo((long) this.mSeekTimePosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i = getDuration();
            int i2 = this.mSeekTimePosition * 100;
            if (i == 0) {
                i = 1;
            }
            i2 /= i;
            if (this.mProgressBar != null) {
                this.mProgressBar.setProgress(i2);
            }
            if (this.mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekPosition");
                this.mVideoAllCallBack.onTouchScreenSeekPosition(this.mOriginUrl, this.mTitle, this);
            }
        } else if (this.mBrightness) {
            if (this.mVideoAllCallBack != null && isCurrentMediaListener()) {
                Debuger.printfLog("onTouchScreenSeekLight");
                this.mVideoAllCallBack.onTouchScreenSeekLight(this.mOriginUrl, this.mTitle, this);
            }
        } else if (this.mChangeVolume && this.mVideoAllCallBack != null && isCurrentMediaListener()) {
            Debuger.printfLog("onTouchScreenSeekVolume");
            this.mVideoAllCallBack.onTouchScreenSeekVolume(this.mOriginUrl, this.mTitle, this);
        }
    }

    protected void prepareVideo() {
        super.prepareVideo();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
    }

    protected void setTextAndProgress(int i) {
        super.setTextAndProgress(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getBuffterPoint());
        stringBuilder.append("");
        Log.d("setTextAndProgress", stringBuilder.toString());
    }

    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean z, boolean z2) {
        return super.startWindowFullscreen(context, z, z2);
    }

    public void onPrepared() {
        super.onPrepared();
    }

    public boolean isOpenPreView() {
        return this.mOpenPreView;
    }

    public void setOpenPreView(boolean z) {
        this.mOpenPreView = z;
    }

    private void showSwitchDialog() {
        if (this.mIfCurrentIsFullscreen) {
            SwitchVideoTypeDialog switchVideoTypeDialog = new SwitchVideoTypeDialog(getContext());
            switchVideoTypeDialog.initList(MyApplication.getUrlList(), new SwitchVideoTypeDialog.OnListItemClickListener() {
                public void onItemClick(int i) {
                    String name = ((SwitchVideoModel) MyApplication.getUrlList().get(i)).getName();
                    if (((SwitchVideoModel) MyApplication.getUrlList().get(i)).getUrl().startsWith("httpx")) {
                        Toast.makeText(PreViewGSYVideoPlayer.this.getContext(), "超清线路为VIP会员专享，请升级成VIP会员", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (PreViewGSYVideoPlayer.this.mSourcePosition == i) {
                        Context context = PreViewGSYVideoPlayer.this.getContext();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("已经是 ");
                        stringBuilder.append(name);
                        Toast.makeText(context, stringBuilder.toString(), 1).show();
                    } else if (PreViewGSYVideoPlayer.this.mCurrentState == 2 || PreViewGSYVideoPlayer.this.mCurrentState == 5) {
                        final String url = ((SwitchVideoModel) MyApplication.getUrlList().get(i)).getUrl();
                        PreViewGSYVideoPlayer.this.onVideoPause();
                        final long access$400 = PreViewGSYVideoPlayer.this.mCurrentPosition;
                        PreViewGSYVideoPlayer.this.getGSYVideoManager().releaseMediaPlayer();
//                        PreViewGSYVideoPlayer.this.access$500();
//                        PreViewGSYVideoPlayer.this.access$600();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                PreViewGSYVideoPlayer.this.setUp(url, PreViewGSYVideoPlayer.this.mCache, PreViewGSYVideoPlayer.this.mCachePath, PreViewGSYVideoPlayer.this.mTitle);
                                PreViewGSYVideoPlayer.this.setSeekOnStart(access$400);
                                PreViewGSYVideoPlayer.this.startPlayLogic();
//                                PreViewGSYVideoPlayer.this.access$500();
//                                PreViewGSYVideoPlayer.this.access$600();
                            }
                        }, 500);
                        PreViewGSYVideoPlayer.this.mTypeText = name;
                        PreViewGSYVideoPlayer.this.mSwitchSize.setText(name.substring(0, 2));
                        PreViewGSYVideoPlayer.this.mSourcePosition = i;
                    }
                }
            });
            switchVideoTypeDialog.show();
        }
    }
}
