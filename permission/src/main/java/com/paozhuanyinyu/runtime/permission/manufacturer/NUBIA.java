package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/11/13.
 */

public class NUBIA implements PermissionsPage{
    //cn.nubia.security2.cn.nubia.security.appopssummary.ui.AppOpsDetail
    private  String MANAGER_OUT_CLS = "cn.nubia.security2.cn.nubia.security.common.tab.CommonTabHost";
    private  String PKG = "cn.nubia.security2";
    public NUBIA(){

    }
    @Override
    public Intent settingIntent(Context context) throws Exception {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PACK_TAG, context.getPackageName());
        ComponentName comp = new ComponentName(PKG, MANAGER_OUT_CLS);
        intent.setComponent(comp);
        return intent;
    }
}
