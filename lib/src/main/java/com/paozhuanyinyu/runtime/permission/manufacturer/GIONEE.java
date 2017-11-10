package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * support:
 * 1.金立M6 android:6.0/amigo3.5.11
 * 2.金立M5，金立F103 android:5.1，5.0/amigo3.0.5,amigo3.0.19
 */
public class GIONEE implements PermissionsPage {
    private  String MANAGER_OUT_CLS = "com.mediatek.security.ui.PermissionControlPageActivity";
    private  String PKG = "com.mediatek.security";

    public GIONEE() {
    }

    @Override
    public Intent settingIntent(Context context) throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            PKG = "com.android.settings";
            MANAGER_OUT_CLS = "com.android.settings.permission.PermissionAppDetail";
            intent.putExtra("packagename", context.getPackageName());
            intent.putExtra("title", getApplicationName(context));
            ComponentName comp = new ComponentName(PKG, MANAGER_OUT_CLS);
            intent.setComponent(comp);
            return intent;
        }
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, MANAGER_OUT_CLS);
        intent.setComponent(comp);
        return intent;
    }
    public String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }
}
