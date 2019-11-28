package com.priv.sepp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.Buffer;

public class Shotter {
    int mHeight;
    private ImageReader mImageReader;
    private String mLocalUrl = "";
    private MediaProjection mMediaProjection;
    private OnShotListener mOnShotListener;
    private final SoftReference<Context> mRefContext;
    private VirtualDisplay mVirtualDisplay;
    int mWidth;

    public interface OnShotListener {
        void onFinish();
    }

    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {
        @TargetApi(19)
        protected Bitmap doInBackground(Image... imageArr) {
            if (imageArr == null || imageArr.length < 1 || imageArr[0] == null) {
                return null;
            }
            File file;
            Image image = imageArr[0];
            int width = image.getWidth();
            int height = image.getHeight();
            Plane[] planes = image.getPlanes();
            Buffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            Bitmap createBitmap = Bitmap.createBitmap(((planes[0].getRowStride() - (pixelStride * width)) / pixelStride) + width, height, Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(buffer);
            Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, width, height);
            image.close();
            if (createBitmap2 != null) {
                try {
                    if (TextUtils.isEmpty(Shotter.this.mLocalUrl)) {
                        Shotter shotter = Shotter.this;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Shotter.this.getContext().getExternalFilesDir("screenshot").getAbsoluteFile());
                        stringBuilder.append("/");
                        stringBuilder.append(SystemClock.currentThreadTimeMillis());
                        stringBuilder.append(".png");
                        shotter.mLocalUrl = stringBuilder.toString();
                    }
                    file = new File(Shotter.this.mLocalUrl);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    OutputStream fileOutputStream = new FileOutputStream(file);
                    createBitmap2.compress(CompressFormat.PNG, 90, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    file = null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    file = null;
                }
            } else {
                file = null;
            }
            if (file != null) {
                return createBitmap2;
            }
            return null;
        }

        @TargetApi(19)
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!(bitmap == null || bitmap.isRecycled())) {
                bitmap.recycle();
            }
            if (Shotter.this.mVirtualDisplay != null) {
                Shotter.this.mVirtualDisplay.release();
            }
            if (Shotter.this.mMediaProjection != null && VERSION.SDK_INT >= 21) {
                Shotter.this.mMediaProjection.stop();
            }
            if (Shotter.this.mOnShotListener != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Shotter.this.mLocalUrl);
                stringBuilder.append("");
                Log.d("Shotter path:", stringBuilder.toString());
                Shotter.this.mOnShotListener.onFinish();
            }
        }
    }

    public Shotter(Context context, int i, Intent intent) {
        this.mRefContext = new SoftReference(context);
        if (VERSION.SDK_INT >= 21) {
            this.mMediaProjection = getMediaProjectionManager().getMediaProjection(i, intent);
            Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getRealMetrics(displayMetrics);
            this.mWidth = displayMetrics.widthPixels;
            this.mHeight = displayMetrics.heightPixels;
            this.mImageReader = ImageReader.newInstance(this.mWidth, this.mHeight, 1, 1);
        }
    }

    @TargetApi(21)
    private void virtualDisplay() {
        this.mVirtualDisplay = this.mMediaProjection.createVirtualDisplay("screen-mirror", this.mWidth, this.mHeight, Resources.getSystem().getDisplayMetrics().densityDpi, 16, this.mImageReader.getSurface(), null, null);
    }

    public void startScreenShot(OnShotListener onShotListener, String str) {
        this.mLocalUrl = str;
        startScreenShot(onShotListener);
    }

    @TargetApi(19)
    public void startScreenShot(OnShotListener onShotListener) {
        this.mOnShotListener = onShotListener;
        if (VERSION.SDK_INT >= 21) {
            virtualDisplay();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Image acquireLatestImage = Shotter.this.mImageReader.acquireLatestImage();
                    new SaveTask().doInBackground(acquireLatestImage);
                }
            }, 800);
        }
    }

    @TargetApi(21)
    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getContext().getSystemService("media_projection");
    }

    private Context getContext() {
        return (Context) this.mRefContext.get();
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
