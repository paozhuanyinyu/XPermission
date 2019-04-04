package com.paozhuanyinyu.runtime.permission.sample;

import android.content.Context;

import com.paozhuanyinyu.runtime.permission.Params;
import com.paozhuanyinyu.runtime.permission.Permission;
import com.paozhuanyinyu.runtime.permission.XPermission;
import io.reactivex.functions.Consumer;

public class XPermissionBehavior implements IPermissionBehavior {
    @Override
    public void requestPermission(Context context, String permissionName, String permissionDesc, final IPermissionResponse response) {
        requestPermission(context,permissionName,permissionDesc,true,response);
    }

    @Override
    public void requestPermission(Context context, String permissionName, String permissionDesc, final boolean isShowGuide, final IPermissionResponse response) {
        XPermission.getInstance().requestEach(context,new Params(permissionName,permissionDesc,isShowGuide))
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if(permission.granted){
                            response.onResponse(IPermissionResponse.SUCCESS);
                        }else if(permission.shouldShowRequestPermissionRationale){
                            response.onResponse(IPermissionResponse.REFUSE);
                        }else{
                            response.onResponse(IPermissionResponse.REFUSE_AND_NOT_HINT);
                        }
                    }
                });
    }

    @Override
    public void init() {

    }
}
