package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;


public class Protogenesis implements PermissionsPage {

    public Protogenesis() {
    }

    // system details setting page
    @Override
    public Intent settingIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }
}
