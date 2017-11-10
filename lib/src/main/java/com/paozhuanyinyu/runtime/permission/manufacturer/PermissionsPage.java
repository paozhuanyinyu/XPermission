package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.Context;
import android.content.Intent;

public interface PermissionsPage {
    String PACK_TAG = "package";

    // normally, ActivityNotFoundException
    Intent settingIntent(Context context) throws Exception;
}
