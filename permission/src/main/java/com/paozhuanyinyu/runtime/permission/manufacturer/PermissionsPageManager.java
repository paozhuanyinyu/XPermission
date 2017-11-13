package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class PermissionsPageManager {
    /**
     * Build.MANUFACTURER
     */
    static final String MANUFACTURER_HUAWEI = "HUAWEI";
    static final String MANUFACTURER_XIAOMI = "XIAOMI";
    static final String MANUFACTURER_OPPO = "OPPO";
    static final String MANUFACTURER_VIVO = "vivo";
    static final String MANUFACTURER_MEIZU = "meizu";
    static final String MANUFACTURER_GIONEE = "GIONEE";
    static final String manufacturer = Build.MANUFACTURER;

    public static String getManufacturer() {
        return manufacturer;
    }

    public static Intent getIntent(Context context) {
        //金立权限管理界面：com.mediatek.security/.ui.PermissionControlPageActivity;GIONEE

        PermissionsPage permissionsPage = new Protogenesis();
        try {
            if (MANUFACTURER_HUAWEI.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new HUAWEI();
            } else if (MANUFACTURER_OPPO.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new OPPO();
            } else if (MANUFACTURER_VIVO.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new VIVO();
            } else if (MANUFACTURER_XIAOMI.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new XIAOMI();
            } else if (MANUFACTURER_MEIZU.equalsIgnoreCase(manufacturer)) {
                permissionsPage = new MEIZU();
            } else if(MANUFACTURER_GIONEE.equalsIgnoreCase(manufacturer)){
                permissionsPage = new GIONEE();
            }
            return permissionsPage.settingIntent(context);
        } catch (Exception e) {
            Log.e("Permissions4M", "手机品牌为：" + manufacturer + "异常抛出，：" + e.getMessage());
            permissionsPage = new Protogenesis();
            return ((Protogenesis) permissionsPage).settingIntent(context);
        }
    }

    public static Intent getSettingIntent(Context context) {
        return new Protogenesis().settingIntent(context);
    }

    public static boolean isXIAOMI() {
        return getManufacturer().equalsIgnoreCase(MANUFACTURER_XIAOMI);
    }

    public static boolean isOPPO() {
        return getManufacturer().equalsIgnoreCase(MANUFACTURER_OPPO);
    }

    public static boolean isMEIZU() {
        return getManufacturer().equalsIgnoreCase(MANUFACTURER_MEIZU);
    }
}
