package com.priv.sepp.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeLayout extends FrameLayout {
    private Callback callback;
    private int contentHeight;
    private View contentView;
    private int contentWidth;
    private SwipeState currentState;
    private int deleteHeight;
    private View deleteView;
    private int deleteWidth;
    private float downX;
    private float downY;
    private OnSwipeStateChangeListener listener;
    private ViewDragHelper mViewDragHelper;

    public interface OnSwipeStateChangeListener {
        void onClose(Object obj);

        void onOpen(Object obj);
    }

    enum SwipeState {
        Open,
        Close
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SwipeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.currentState = SwipeState.Close;
        this.callback = new Callback() {
            public boolean tryCaptureView(View view, int i) {
                return view == SwipeLayout.this.contentView || view == SwipeLayout.this.deleteView;
            }

            public int getViewHorizontalDragRange(View view) {
                return SwipeLayout.this.deleteWidth;
            }

            public int clampViewPositionHorizontal(View view, int i, int i2) {
                if (view == SwipeLayout.this.contentView) {
                    if (i > 0) {
                        i = 0;
                    }
                    if (i < (-SwipeLayout.this.deleteWidth)) {
                        return -SwipeLayout.this.deleteWidth;
                    }
                    return i;
                } else if (view != SwipeLayout.this.deleteView) {
                    return i;
                } else {
                    if (i > SwipeLayout.this.contentWidth) {
                        i = SwipeLayout.this.contentWidth;
                    }
                    return i < SwipeLayout.this.contentWidth - SwipeLayout.this.deleteWidth ? SwipeLayout.this.contentWidth - SwipeLayout.this.deleteWidth : i;
                }
            }

            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                super.onViewPositionChanged(view, i, i2, i3, i4);
                if (view == SwipeLayout.this.contentView) {
                    SwipeLayout.this.deleteView.layout(SwipeLayout.this.deleteView.getLeft() + i3, SwipeLayout.this.deleteView.getTop() + i4, SwipeLayout.this.deleteView.getRight() + i3, SwipeLayout.this.deleteView.getBottom() + i4);
                } else if (view == SwipeLayout.this.deleteView) {
                    SwipeLayout.this.contentView.layout(SwipeLayout.this.contentView.getLeft() + i3, SwipeLayout.this.contentView.getTop() + i4, SwipeLayout.this.contentView.getRight() + i3, SwipeLayout.this.contentView.getBottom() + i4);
                }
                if (SwipeLayout.this.contentView.getLeft() == 0 && SwipeLayout.this.currentState != SwipeState.Close) {
                    SwipeLayout.this.currentState = SwipeState.Close;
                    if (SwipeLayout.this.listener != null) {
                        SwipeLayout.this.listener.onClose(SwipeLayout.this.getTag());
                    }
                    SwipeLayoutManager.getInstance().clearCurrentLayout();
                } else if (SwipeLayout.this.contentView.getLeft() == (-SwipeLayout.this.deleteWidth) && SwipeLayout.this.currentState != SwipeState.Open) {
                    SwipeLayout.this.currentState = SwipeState.Open;
                    if (SwipeLayout.this.listener != null) {
                        SwipeLayout.this.listener.onOpen(SwipeLayout.this.getTag());
                    }
                    SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
                }
            }

            public void onViewReleased(View view, float f, float f2) {
                super.onViewReleased(view, f, f2);
                if (SwipeLayout.this.contentView.getLeft() < (-SwipeLayout.this.deleteWidth) / 2) {
                    SwipeLayout.this.open();
                } else {
                    SwipeLayout.this.close();
                }
            }
        };
        init();
    }

    private void init() {
        this.mViewDragHelper = ViewDragHelper.create(this, this.callback);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.contentView = getChildAt(0);
        this.deleteView = getChildAt(1);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.contentHeight = this.contentView.getMeasuredHeight();
        this.contentWidth = this.contentView.getMeasuredWidth();
        this.deleteHeight = this.deleteView.getMeasuredHeight();
        this.deleteWidth = this.deleteView.getMeasuredWidth();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.contentView.layout(0, 0, this.contentWidth, this.contentHeight);
        this.deleteView.layout(this.contentView.getRight(), 0, this.contentView.getRight() + this.deleteWidth, this.deleteHeight);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean shouldInterceptTouchEvent = this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
        if (SwipeLayoutManager.getInstance().isShouldSwipe(this)) {
            return shouldInterceptTouchEvent;
        }
        SwipeLayoutManager.getInstance().closeCurrentLayout();
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (SwipeLayoutManager.getInstance().isShouldSwipe(this)) {
            int action = motionEvent.getAction();
            if (action == 0) {
                this.downX = motionEvent.getX();
                this.downY = motionEvent.getY();
            } else if (action == 2) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (Math.abs(x - this.downX) > Math.abs(y - this.downY)) {
                    requestDisallowInterceptTouchEvent(true);
                }
                this.downX = x;
                this.downY = y;
            }
            this.mViewDragHelper.processTouchEvent(motionEvent);
            return true;
        }
        requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public void open() {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        View view = this.contentView;
        viewDragHelper.smoothSlideViewTo(view, -this.deleteWidth, view.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void close() {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        View view = this.contentView;
        viewDragHelper.smoothSlideViewTo(view, 0, view.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener onSwipeStateChangeListener) {
        this.listener = onSwipeStateChangeListener;
    }
}
