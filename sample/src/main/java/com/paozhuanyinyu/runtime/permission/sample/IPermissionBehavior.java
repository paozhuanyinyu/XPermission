package com.paozhuanyinyu.runtime.permission.sample;

import android.content.Context;

import java.util.Locale;

public interface IPermissionBehavior {
    void requestPermission(Context context, String permissionName, String permissionDesc, IPermissionResponse response);
    void requestPermission(Context context, String permissionName, String permissionDesc, boolean isShowGuide,IPermissionResponse response);
    void init();
    void init(Context context,Locale locale);
}
