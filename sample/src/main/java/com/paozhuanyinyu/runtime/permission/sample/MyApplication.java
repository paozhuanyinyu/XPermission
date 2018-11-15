package com.paozhuanyinyu.runtime.permission.sample;

import android.app.Application;

import java.util.Locale;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PermissionManager.getInstance().init(getApplicationContext(),Locale.SIMPLIFIED_CHINESE);
    }
}
