package com.paozhuanyinyu.runtime.permission;

import android.os.Parcel;
import android.os.Parcelable;

public class Params implements Parcelable {
    public String permissionName;
    public String permissionDesc;
    public Params(String permissionName,String permissionDesc){
        this.permissionName = permissionName;
        this.permissionDesc = permissionDesc;
    }

    protected Params(Parcel in) {
        permissionName = in.readString();
        permissionDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(permissionName);
        dest.writeString(permissionDesc);
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
