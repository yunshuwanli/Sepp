package com.priv.sepp;

import android.app.Application;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    private static List<SwitchVideoModel> mUrlList = new ArrayList();

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static List<SwitchVideoModel> getUrlList() {
        return mUrlList;
    }
}
