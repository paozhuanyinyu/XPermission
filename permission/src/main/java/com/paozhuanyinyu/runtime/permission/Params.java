package com.paozhuanyinyu.runtime.permission;

import android.os.Parcel;
import android.os.Parcelable;
public class Params implements Parcelable {
    String permissionName;//权限名称
    String permissionDesc;//权限描述
    boolean isShowGuide;//是否展示拒绝授权后的引导提示框
    public Params(String permissionName,String permissionDesc){
        this(permissionName,permissionDesc,true);
    }
    public Params(String permissionName,String permissionDesc,boolean isShowGuide){
        this.permissionName = permissionName;
        this.permissionDesc = permissionDesc;
        this.isShowGuide = isShowGuide;
    }

    protected Params(Parcel in) {
        permissionName = in.readString();
        permissionDesc = in.readString();
        isShowGuide = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(permissionName);
        dest.writeString(permissionDesc);
        dest.writeByte((byte) (isShowGuide ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Params> CREATOR = new Creator<Params>() {
        @Override
        public Params createFromParcel(Parcel in) {
            return new Params(in);
        }

        @Override
        public Params[] newArray(int size) {
            return new Params[size];
        }
    };
}
