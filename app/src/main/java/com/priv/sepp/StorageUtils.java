package com.priv.sepp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

public final class StorageUtils {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";

    private StorageUtils() {
    }

    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    public static File getCacheDirectory(Context context, boolean z) {
        Object externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException unused) {
            externalStorageState = "";
        }
        File externalCacheDir = (z && "mounted".equals(externalStorageState) && hasExternalStoragePermission(context)) ? getExternalCacheDir(context) : null;
        if (externalCacheDir == null) {
            externalCacheDir = context.getCacheDir();
        }
        if (externalCacheDir != null) {
            return externalCacheDir;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/data/data/");
        stringBuilder.append(context.getPackageName());
        stringBuilder.append("/cache/");
        return new File(stringBuilder.toString());
    }

    public static File getIndividualCacheDirectory(Context context) {
        File cacheDirectory = getCacheDirectory(context);
        File file = new File(cacheDirectory, INDIVIDUAL_DIR_NAME);
        return (file.exists() || file.mkdir()) ? file : cacheDirectory;
    }

    public static File getOwnCacheDirectory(Context context, String str) {
        File file = ("mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) ? new File(Environment.getExternalStorageDirectory(), str) : null;
        return (file == null || !(file.exists() || file.mkdirs())) ? context.getCacheDir() : file;
    }

    private static File getExternalCacheDir(Context context) {
        File file = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data"), context.getPackageName()), "cache");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException unused) {
                return file;
            }
        }
        return file;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        return context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean makeDir(String str) {
        File file = new File(str);
        if (!file.exists()) {
            try {
                file.mkdirs();
                return true;
            } catch (Exception unused) {
                return false;
            }
        }
        return false;
    }

    public static boolean fileIsExists(String str) {
        return new File(str).exists();
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals("mounted");
    }
}
