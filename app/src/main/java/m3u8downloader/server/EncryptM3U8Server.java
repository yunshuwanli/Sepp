package m3u8downloader.server;

import android.text.TextUtils;

import java.io.File;

import m3u8downloader.M3U8Downloader;
import m3u8downloader.M3U8EncryptHelper;
import m3u8downloader.utils.M3U8Log;

public class EncryptM3U8Server extends M3U8HttpServer {
    public void encrypt() {
        if (!TextUtils.isEmpty(this.filesDir) && !isEncrypt(this.filesDir)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        M3U8EncryptHelper.encryptTsFilesName(M3U8Downloader.getInstance().getEncryptKey(), EncryptM3U8Server.this.filesDir);
                    } catch (Exception e) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("M3u8Server encrypt: ");
                        stringBuilder.append(e.getMessage());
                        M3U8Log.e(stringBuilder.toString());
                    }
                }
            }).start();
        }
    }

    public void decrypt() {
        if (!TextUtils.isEmpty(this.filesDir) && isEncrypt(this.filesDir)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        M3U8EncryptHelper.decryptTsFilesName(M3U8Downloader.getInstance().getEncryptKey(), EncryptM3U8Server.this.filesDir);
                    } catch (Exception e) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("M3u8Server decrypt: ");
                        stringBuilder.append(e.getMessage());
                        M3U8Log.e(stringBuilder.toString());
                    }
                }
            }).start();
        }
    }

    private boolean isEncrypt(String str) {
        try {
            File file = new File(str);
            if (file.exists() && file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (File name : listFiles) {
                    if (name.getName().contains(".ts")) {
                        return false;
                    }
                }
            }
        } catch (Exception unused) {
            return true;
        }
        return true;
    }
}
