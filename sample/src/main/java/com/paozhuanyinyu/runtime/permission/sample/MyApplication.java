package com.paozhuanyinyu.runtime.permission.sample;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PermissionManager.getInstance().init();
    }
}
