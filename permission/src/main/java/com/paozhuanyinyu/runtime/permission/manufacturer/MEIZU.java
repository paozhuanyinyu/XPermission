package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class MEIZU implements PermissionsPage {
    private final String N_MANAGER_OUT_CLS = "com.meizu.safe.permission.PermissionMainActivity";
    private final String L_MANAGER_OUT_CLS = "com.meizu.safe.SecurityMainActivity";
    private final String PKG = "com.meizu.safe";

    public MEIZU() {
    }

    @Override
    public Intent settingIntent(Context context) throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, getCls());
        intent.setComponent(comp);
        return intent;
    }

    private String getCls() {
        if (ManufacturerSupportUtil.isAndroidL()) {
            return L_MANAGER_OUT_CLS;
        } else {
            return N_MANAGER_OUT_CLS;
        }
    }
}
