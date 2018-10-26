package com.paozhuanyinyu.runtime.permission.sample;

import android.content.Context;

public interface IPermissionBehavior {
    void requestPermission(Context context, String permissionName, String permissionDesc, IPermissionResponse response);
    void init();
}
