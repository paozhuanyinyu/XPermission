package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * support:
 * 1.Y55A androi:6.0.1/Funtouch 2.6
 * 2.Xplay5A android: 5.1.1/Funtouch 3
 *
 * manager home page, or {@link Protogenesis#settingIntent(Context context)}
 *
 */

public class VIVO implements PermissionsPage {
    private final String MAIN_CLS = "com.iqoo.secure.MainActivity";
    private final String PKG = "com.iqoo.secure";

    public VIVO() {
    }

    @Override
    public Intent settingIntent(Context context) throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, MAIN_CLS);

        // starting Intent { flg=0x10000000 cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has
        // extras) } from ProcessRecord
//        ComponentName comp = new ComponentName(PKG, "com.iqoo.secure.safeguard.PurviewTabActivity");

        // can enter, but blank
//        try {
//            PackageInfo pi = context.getPackageManager().getPackageInfo(PKG,
//                    PackageManager.GET_ACTIVITIES);
//            for (ActivityInfo activityInfo : pi.activities) {
//                Log.e("TAG", "settingIntent:  " + activityInfo.name);
//                if (activityInfo.name.contains(IN_CLS)) {
//                    comp = new ComponentName(PKG, "com.iqoo.secure.safeguard
// .SoftPermissionDetailActivity");
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        intent.setComponent(comp);

        return intent;
    }
}
