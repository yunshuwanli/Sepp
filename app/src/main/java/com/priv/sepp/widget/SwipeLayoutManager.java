package com.priv.sepp.widget;

public class SwipeLayoutManager {
    private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
    private SwipeLayout currentLayout;

    private SwipeLayoutManager() {
    }

    public static SwipeLayoutManager getInstance() {
        return mInstance;
    }

    public void setSwipeLayout(SwipeLayout swipeLayout) {
        this.currentLayout = swipeLayout;
    }

    public void clearCurrentLayout() {
        this.currentLayout = null;
    }

    public void closeCurrentLayout() {
        SwipeLayout swipeLayout = this.currentLayout;
        if (swipeLayout != null) {
            swipeLayout.close();
        }
    }

    public boolean isShouldSwipe(SwipeLayout swipeLayout) {
        SwipeLayout swipeLayout2 = this.currentLayout;
        boolean z = true;
        if (swipeLayout2 == null) {
            return true;
        }
        if (swipeLayout2 != swipeLayout) {
            z = false;
        }
        return z;
    }
}
