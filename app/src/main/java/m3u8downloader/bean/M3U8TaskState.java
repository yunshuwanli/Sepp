package m3u8downloader.bean;

public class M3U8TaskState {
    public static final int DEFAULT = 0;
    public static final int DOWNLOADING = 2;
    public static final int ENOSPC = 6;
    public static final int ERROR = 4;
    public static final int PAUSE = 5;
    public static final int PENDING = -1;
    public static final int PREPARE = 1;
    public static final int SUCCESS = 3;
}
