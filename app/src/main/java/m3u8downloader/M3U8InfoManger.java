package m3u8downloader;

import com.m3u8downloader.bean.M3U8;
import com.m3u8downloader.utils.MUtils;

public class M3U8InfoManger {
    private static M3U8InfoManger mM3U8InfoManger;
    private OnM3U8InfoListener onM3U8InfoListener;

    private M3U8InfoManger() {
    }

    public static M3U8InfoManger getInstance() {
        synchronized (M3U8InfoManger.class) {
            if (mM3U8InfoManger == null) {
                mM3U8InfoManger = new M3U8InfoManger();
            }
        }
        return mM3U8InfoManger;
    }

    public synchronized void getM3U8Info(final String str, OnM3U8InfoListener onM3U8InfoListener) {
        this.onM3U8InfoListener = onM3U8InfoListener;
        onM3U8InfoListener.onStart();
        new Thread() {
            public void run() {
                try {
                    M3U8InfoManger.this.handlerSuccess(MUtils.parseIndex(str));
                } catch (Throwable e) {
                    M3U8InfoManger.this.handlerError(e);
                }
            }
        }.start();
    }

    private void handlerError(Throwable th) {
        this.onM3U8InfoListener.onError(th);
    }

    private void handlerSuccess(M3U8 m3u8) {
        this.onM3U8InfoListener.onSuccess(m3u8);
    }
}
