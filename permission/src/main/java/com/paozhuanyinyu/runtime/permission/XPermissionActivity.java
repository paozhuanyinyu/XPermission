package com.paozhuanyinyu.runtime.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.paozhuanyinyu.runtime.permission.dialog.MyDialog;
import com.paozhuanyinyu.runtime.permission.manufacturer.PermissionsChecker;
import com.paozhuanyinyu.rxpermissions.R;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

import static com.paozhuanyinyu.runtime.permission.XPermission.TAG;

/**
 * Created by Administrator on 2017/11/3.
 */

public class XPermissionActivity extends Activity{
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int GO_TO_SETTINGS_REQUEST_CODE = 43;
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private static Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();
    private static Map<String, Params> mParams = new HashMap<String, Params>();
    private static boolean mLogging;
    private static String permissionName;
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GO_TO_SETTINGS_REQUEST_CODE) {
            PublishSubject<Permission> subject = mSubjects.get(permissionName);
            if (Manifest.permission.WRITE_SETTINGS.equals(permissionName) || Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permissionName)) {
                if(PermissionsChecker.isPermissionGranted(this, permissionName, false)){
                    subject.onNext(new Permission(permissionName, true, true));
                    subject.onComplete();
                }else{
                    subject.onNext(new Permission(permissionName, false, false));
                    subject.onComplete();
                }
            } else {
                if (PermissionsChecker.isPermissionGranted(this, permissionName, true)) {
                    subject.onNext(new Permission(permissionName, true, true));
                    subject.onComplete();
                }else{
                    subject.onNext(new Permission(permissionName, false, false));
                    subject.onComplete();
                }
            }
        }
        mSubjects.remove(permissionName);
        mParams.remove(permissionName);
        finish();
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
//            mSubjects.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            boolean showRequestPermissionRationale = shouldShowRequestPermissionRationale[i];
            log("granted: " + granted + "; showRequestPermissionRationale: " + showRequestPermissionRationale);
            if(Manifest.permission.WRITE_SETTINGS.equals(permissions[i]) || Manifest.permission.SYSTEM_ALERT_WINDOW.equals(permissions[i])){
                granted = PermissionsChecker.isPermissionGranted(this,permissions[i],false);
            }else{
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
            }
            if(!granted && !showRequestPermissionRationale && (mParams.get(permissions[i])!=null && mParams.get(permissions[i]).isShowGuide)){
                showReadPhoneStateHintDialog(subject,permissions[i]);
            }else{
                subject.onNext(new Permission(permissions[i], granted, showRequestPermissionRationale));
                subject.onComplete();
                mSubjects.remove(permissions[i]);
                mParams.remove(permissions[i]);
                finish();
            }
        }
    }
    private void showReadPhoneStateHintDialog(final PublishSubject<Permission> subject,final String name) {
        permissionName = name;
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle(getString(R.string.hint));
        builder.setMessage(String.format(getString(R.string.message),mParams.get(name).permissionDesc));
        builder.setPositiveButton(getString(R.string.go_to_set), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = XPermission.getInstance().getSettingsIntent(XPermissionActivity.this,name);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent,GO_TO_SETTINGS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                subject.onNext(new Permission(name, false, false));
                subject.onComplete();
                mSubjects.remove(name);
                finish();
            }
        });
        MyDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                subject.onNext(new Permission(name, false, false));
                subject.onComplete();
                mSubjects.remove(name);
                finish();
            }
        });
        dialog.show();
    }

    public static PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public static boolean containsByPermission(@NonNull Params permission) {
        return mSubjects.containsKey(permission);
    }

    public static PublishSubject<Permission> setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        return mSubjects.put(permission, subject);
    }

    public static void setParams(Params params){
        mParams.put(params.permissionName,params);
    }

    public static void setLogging(boolean logging) {
        mLogging = logging;
    }
    static void log(String message) {
        if (mLogging) {
            Log.d(TAG, message);
        }
    }
}
