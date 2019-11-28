package com.priv.sepp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.File;

public class InstallUtil {
    public static int UNKNOWN_CODE = 2018;
    private Context mAct;
    private String mPath;

    public InstallUtil(Context context, String str) {
        this.mAct = context;
        this.mPath = str;
    }

    public void install() {
        try {
            if (VERSION.SDK_INT >= 26) {
                startInstallO();
            } else if (VERSION.SDK_INT >= 24) {
                startInstallN();
            } else {
                startInstall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startInstall() throws Exception {
        Intent intent = new Intent("android.intent.action.VIEW");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("file://");
        stringBuilder.append(this.mPath);
        intent.setDataAndType(Uri.parse(stringBuilder.toString()), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mAct.startActivity(intent);
    }

    private void startInstallN() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("startInstallN... ");
        stringBuilder.append(this.mPath);
        Log.e("startInstallO", stringBuilder.toString());
        Context context = this.mAct;
        Uri uriForFile = FileProvider.getUriForFile(context, getAuthority(context, ".provider"), new File(this.mPath));
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
        this.mAct.startActivity(intent);
    }

    @RequiresApi(api = 26)
    private void startInstallO() throws Exception {
        Log.e("startInstallO", "startInstallO");
        if (this.mAct.getPackageManager().canRequestPackageInstalls()) {
            try {
                startInstallN();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        new Builder(this.mAct).setCancelable(false).setTitle("安装应用需要打开未知来源权限，请去设置中开启权限").setPositiveButton("确定", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(InstallUtil.this.mAct.getPackageName());
                InstallUtil.this.mAct.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse(stringBuilder.toString())));
                try {
                    InstallUtil.this.startInstallN();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).show();
    }

    private String getAuthority(Context context, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getAppProcessName(context));
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    private String getAppProcessName(Context context) {
        int myPid = Process.myPid();
        for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.processName;
            }
        }
        return "";
    }
}
