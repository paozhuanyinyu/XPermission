package com.paozhuanyinyu.runtime.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.paozhuanyinyu.runtime.permission.manufacturer.PermissionsChecker;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Administrator on 2017/11/3.
 */

public class XPermissionActivity extends Activity{
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private static Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();
    private static boolean mLogging;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = getIntent().getStringArrayExtra("permissions");
        requestPermissions(permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
        finish();
    }
    void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
            if (subject == null) {
                // No subject found
                log("XPermission.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            mSubjects.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            boolean showRequestPermissionRationale = shouldShowRequestPermissionRationale[i];
            log("granted: " + granted + "; showRequestPermissionRationale: " + showRequestPermissionRationale);
            if(granted){
                if(PermissionsChecker.isPermissionGranted(this,permissions[i],true)){
                    granted = true;
                }else{
                    granted = false;
                    showRequestPermissionRationale = false;
                }
            }else if(showRequestPermissionRationale){
                if(PermissionsChecker.isPermissionGranted(this,permissions[i],false)){
                    granted = true;
                }else{
                    granted = false;
                }
            }
            subject.onNext(new Permission(permissions[i], granted, showRequestPermissionRationale));
            subject.onComplete();
        }
    }

    public static PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public static boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public static PublishSubject<Permission> setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        return mSubjects.put(permission, subject);
    }

    public static void setLogging(boolean logging) {
        mLogging = logging;
    }
    static void log(String message) {
        if (mLogging) {
            Log.d(XPermission.TAG, message);
        }
    }
}
