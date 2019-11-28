package com.priv.sepp;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ScrollWebView extends WebView {
    private OnScrollListener mOnScrollListener;

    public interface OnScrollListener {
        void onScroll(int i, int i2, int i3, int i4);
    }

    public ScrollWebView(Context context) {
        super(context);
    }

    public ScrollWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ScrollWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScroll(i, i2, i3, i4);
        }
    }

    public OnScrollListener getOnScrollListener() {
        return this.mOnScrollListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }
}
