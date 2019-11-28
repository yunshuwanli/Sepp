package m3u8downloader.bean;

import java.util.ArrayList;
import java.util.List;

import m3u8downloader.utils.MUtils;

public class M3U8 {
    private String basePath;
    private String dirFilePath;
    private String domain;
    private long fileSize;
    private String m3u8FilePath;
    private long totalTime;
    private List<M3U8Ts> tsList = new ArrayList();

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String str) {
        this.domain = str;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String str) {
        this.basePath = str;
    }

    public String getM3u8FilePath() {
        return this.m3u8FilePath;
    }

    public void setM3u8FilePath(String str) {
        this.m3u8FilePath = str;
    }

    public String getDirFilePath() {
        return this.dirFilePath;
    }

    public void setDirFilePath(String str) {
        this.dirFilePath = str;
    }

    public long getFileSize() {
        this.fileSize = 0;
        for (M3U8Ts fileSize : this.tsList) {
            this.fileSize += fileSize.getFileSize();
        }
        return this.fileSize;
    }

    public String getFormatFileSize() {
        this.fileSize = getFileSize();
        long j = this.fileSize;
        if (j == 0) {
            return "";
        }
        return MUtils.formatFileSize(j);
    }

    public void setFileSize(long j) {
        this.fileSize = j;
    }

    public List<M3U8Ts> getTsList() {
        return this.tsList;
    }

    public void setTsList(List<M3U8Ts> list) {
        this.tsList = list;
    }

    public void addTs(M3U8Ts m3U8Ts) {
        this.tsList.add(m3U8Ts);
    }

    public long getTotalTime() {
        this.totalTime = 0;
        for (M3U8Ts seconds : this.tsList) {
            this.totalTime += (long) ((int) (seconds.getSeconds() * 1000.0f));
        }
        return this.totalTime;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("basePath: ");
        stringBuilder2.append(this.basePath);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("\nm3u8FilePath: ");
        stringBuilder2.append(this.m3u8FilePath);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("\ndirFilePath: ");
        stringBuilder2.append(this.dirFilePath);
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("\nfileSize: ");
        stringBuilder2.append(getFileSize());
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("\nfileFormatSize: ");
        stringBuilder2.append(MUtils.formatFileSize(this.fileSize));
        stringBuilder.append(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("\ntotalTime: ");
        stringBuilder2.append(this.totalTime);
        stringBuilder.append(stringBuilder2.toString());
        for (M3U8Ts m3U8Ts : this.tsList) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("\nts: ");
            stringBuilder3.append(m3U8Ts);
            stringBuilder.append(stringBuilder3.toString());
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof M3U8) {
            M3U8 m3u8 = (M3U8) obj;
            String str = this.basePath;
            if (str != null && str.equals(m3u8.basePath)) {
                return true;
            }
        }
        return false;
    }
}
