package m3u8downloader.bean;

import android.support.annotation.NonNull;

import m3u8downloader.utils.MD5Utils;
import m3u8downloader.utils.MUtils;

public class M3U8Ts implements Comparable<M3U8Ts> {
    private long fileSize;
    private float seconds;
    private String url;

    public M3U8Ts(String str, float f) {
        this.url = str;
        this.seconds = f;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public float getSeconds() {
        return this.seconds;
    }

    public void setSeconds(float f) {
        this.seconds = f;
    }

    public String obtainEncodeTsFileName() {
        String str = this.url;
        if (str == null) {
            return "error.ts";
        }
        return MD5Utils.encode(str).concat(".ts");
    }

    public String obtainFullUrl(String str, String str2) {
        String str3 = this.url;
        if (str3 == null) {
            return null;
        }
        if (str3.startsWith("http")) {
            return this.url;
        }
        if (this.url.startsWith("//")) {
            return MUtils.getUrlPrefix(str2).concat(this.url);
        }
        if (this.url.startsWith("/")) {
            return str2.concat(this.url);
        }
        return str.concat(this.url);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.url);
        stringBuilder.append(" (");
        stringBuilder.append(this.seconds);
        stringBuilder.append("sec)");
        return stringBuilder.toString();
    }

    public long getLongDate() {
        try {
            return Long.parseLong(this.url.substring(0, this.url.lastIndexOf(".")));
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public int compareTo(@NonNull M3U8Ts m3U8Ts) {
        return this.url.compareTo(m3U8Ts.url);
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long j) {
        this.fileSize = j;
    }
}
