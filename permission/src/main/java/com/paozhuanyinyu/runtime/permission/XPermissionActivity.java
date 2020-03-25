package com.paozhuanyinyu.runtime.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.paozhuanyinyu.runtime.permission.dialog.MyDialog;
import com.paozhuanyinyu.runtime.permission.manufacturer.PermissionsChecker;
import com.paozhuanyinyu.rxpermissions.R;
import java.util.Map;
import io.reactivex.subjects.PublishSubject;
import static com.paozhuanyinyu.runtime.permission.XPermission.TAG;

/**
 * Created by Administrator on 2017/11/3.
 */

public class XPermissionActivity extends Activity {
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int GO_TO_SETTINGS_REQUEST_CODE = 43;
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private static boolean mLogging;
    private static String permissionName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置透明沉浸状态栏
        if (Build.VERSION.SDK_INT>=21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE); //使背景图与状态栏融合到一起，这里需要在setcontentview前执行
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
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
        try{
            if(requestCode == GO_TO_SETTINGS_REQUEST_CODE) {
                PublishSubject<Permission> subject = XPermission.getInstance().getSubjects().get(permissionName);
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            finish();
        }
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            Map<String, PublishSubject<Permission>> map = XPermission.getInstance().getSubjects();
            PublishSubject<Permission> subject = map.get(permissions[i]);
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
            if(!granted && !showRequestPermissionRationale && (XPermission.getInstance().getParams().get(permissions[i])!=null && XPermission.getInstance().getParams().get(permissions[i]).isShowGuide)){
                showReadPhoneStateHintDialog(subject,permissions[i]);
            }else{
                try{
                    subject.onNext(new Permission(permissions[i], granted, showRequestPermissionRationale));
                    subject.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }
        }
    }
    private void showReadPhoneStateHintDialog(final PublishSubject<Permission> subject,final String name) {
        permissionName = name;
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.setTitle(getString(R.string.hint));
        builder.setMessage(String.format(getString(R.string.message),XPermission.getInstance().getParams().get(name).permissionDesc));
        builder.setPositiveButton(getString(R.string.go_to_set), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = XPermission.getInstance().getSettingsIntent(XPermissionActivity.this,name);
                startActivityForResult(intent,GO_TO_SETTINGS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                try{
                    subject.onNext(new Permission(name, false, false));
                    subject.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    finish();
                }

            }
        });
        MyDialog dialog = builder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try{
                    subject.onNext(new Permission(name, false, false));
                    subject.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    finish();
                }

            }
        });
        dialog.show();
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
