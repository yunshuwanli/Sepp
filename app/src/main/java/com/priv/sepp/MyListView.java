package com.priv.sepp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyListView extends ListView implements OnTouchListener, OnGestureListener {
    private static final String TAG = "MyListView";
    private View deleteButton;
    private GestureDetector gestureDetector = new GestureDetector(getContext(), this);
    private boolean isDeleteShown;
    private ViewGroup itemLayout;
    private ImageView ivIcon;
    private OnDeleteListener mListener;
    private int minVelocity = 0;
    private int selectedItem;
    private int verticalMinDistance = 20;

    public interface OnDeleteListener {
        void onDelete(int i);
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public MyListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(this);
        this.verticalMinDistance = MyUtils.dip2px(getContext(), 100.0f);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.mListener = onDeleteListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG, "onTouch");
        if (this.isDeleteShown) {
            ((VideoListAdapter) getAdapter()).setPauseUpdate(false);
            this.itemLayout.removeView(this.deleteButton);
            this.deleteButton = null;
            this.isDeleteShown = false;
            return true;
        } else if (-1 == pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        } else {
            this.selectedItem = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            return this.gestureDetector.onTouchEvent(motionEvent);
        }
    }

    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, "onDown");
        this.selectedItem = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (!this.isDeleteShown && Math.abs(f) > Math.abs(f2) && Math.abs(motionEvent.getX() - motionEvent2.getX()) >= ((float) this.verticalMinDistance) && Math.abs(f) > ((float) this.minVelocity)) {
            Log.d(TAG, "onFling");
            this.deleteButton = LayoutInflater.from(getContext()).inflate(R.layout.delete_button, null);
            this.deleteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ((VideoListAdapter) MyListView.this.getAdapter()).setPauseUpdate(false);
                    MyListView.this.itemLayout.removeView(MyListView.this.deleteButton);
                    MyListView.this.deleteButton = null;
                    MyListView.this.isDeleteShown = false;
                    if (MyListView.this.mListener != null) {
                        MyListView.this.mListener.onDelete(MyListView.this.selectedItem);
                    }
                }
            });
            this.itemLayout = (ViewGroup) getChildAt(this.selectedItem - getFirstVisiblePosition());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(11);
            layoutParams.addRule(15);
            this.itemLayout.addView(this.deleteButton, layoutParams);
            this.isDeleteShown = true;
            ((VideoListAdapter) getAdapter()).setPauseUpdate(true);
        }
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress");
    }
}
