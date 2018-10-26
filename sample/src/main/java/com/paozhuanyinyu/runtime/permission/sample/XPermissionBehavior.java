package com.paozhuanyinyu.runtime.permission.sample;

import android.content.Context;

import com.paozhuanyinyu.runtime.permission.Params;
import com.paozhuanyinyu.runtime.permission.Permission;
import com.paozhuanyinyu.runtime.permission.XPermission;

public class XPermissionBehavior implements IPermissionBehavior {
    @Override
    public void requestPermission(Context context, String permissionName, String permissionDesc, final IPermissionResponse response) {
        XPermission.getInstance().requestEach(context,new Params(permissionName,permissionDesc))
                .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if(permission.granted){
                            response.onResponse(IPermissionResponse.SUCCESS);
                        }else{
                            response.onResponse(IPermissionResponse.REFUSE);
                        }
                    }
                });
    }

    @Override
    public void init() {

    }
}
