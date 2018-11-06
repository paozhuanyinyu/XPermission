package com.paozhuanyinyu.runtime.permission.sample;

public interface IPermissionResponse {
    public static final int SUCCESS = 1;
    public static final int REFUSE = 0;
    public static final int REFUSE_AND_NOT_HINT = -1;
    void onResponse(int code);
}
