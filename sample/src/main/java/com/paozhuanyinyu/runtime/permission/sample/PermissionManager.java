package com.paozhuanyinyu.runtime.permission.sample;

import android.content.Context;

public class PermissionManager implements IPermissionBehavior {
    private static PermissionManager instance;
    private IPermissionBehavior behavior;
    private PermissionManager(){}

    public static PermissionManager getInstance(){
        if(instance==null){
            synchronized (PermissionManager.class){
                if(instance==null){
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void requestPermission(Context context, String permissionName, String permissionDesc, IPermissionResponse response) {
        requestPermission(context,permissionName,permissionDesc,true,response);
    }

    @Override
    public void requestPermission(Context context, String permissionName, String permissionDesc, boolean isShowGuide, IPermissionResponse response) {
        if(behavior==null){
            init();
        }
        behavior.requestPermission(context,permissionName,permissionDesc,isShowGuide,response);
    }

    @Override
    public void init() {
        behavior = new XPermissionBehavior();
        behavior.init();
    }
}
