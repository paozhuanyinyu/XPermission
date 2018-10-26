package com.paozhuanyinyu.runtime.permission.sample;

public interface IPermissionResponse {
    public static final int SUCCESS = 1;
    public static final int REFUSE = 0;
    void onResponse(int code);
}
