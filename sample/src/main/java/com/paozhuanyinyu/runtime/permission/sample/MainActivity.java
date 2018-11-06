package com.paozhuanyinyu.runtime.permission.sample;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.paozhuanyinyu.runtime.permission.XPermission;
import com.paozhuanyinyu.runtime.sample.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RxPermissionsSample";
    ListView ll_permissions;
    private ArrayMap map;
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_SMS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.WRITE_SETTINGS,
    };
    private String[] permissionDiscription = {
            "拍照",
            "读取联系人",
            "写入联系人",
            "读取手机状态",
            "读取通话记录",
            "写入通话记录",
            "读取日历",
            "传感器",
            "GPS定位",
            "基站定位",
            "读取存储",
            "写入存储",
            "录音",
            "短信",
            "系统弹窗",
            "修改设置",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll_permissions = (ListView) findViewById(R.id.ll_permissions);
        Button bt_collect = (Button) findViewById(R.id.bt_collect);
        bt_collect.setText("跳转权限管理界面(厂商： " + Build.MANUFACTURER + ")");
        bt_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(XPermission.getInstance().getSettingsIntent(MainActivity.this));
            }
        });

        Button bt_alert = (Button) findViewById(R.id.bt_alert);
        bt_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(XPermission.getInstance().getSettingsIntent(MainActivity.this,Manifest.permission.SYSTEM_ALERT_WINDOW));
            }
        });

        Button bt_write_settings = (Button) findViewById(R.id.bt_write_settings);
        bt_write_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(XPermission.getInstance().getSettingsIntent(MainActivity.this,Manifest.permission.WRITE_SETTINGS));
            }
        });

        MyAdapter myAdapter = new MyAdapter(this,permissionDiscription);
        ll_permissions.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyAdapter.onItemClickListener() {
            @Override
            public void onClick(int i) {
                Log.e("MainActivity","onItemClick");
                PermissionManager.getInstance().requestPermission(MainActivity.this, permissions[i], permissionDiscription[i],false, new IPermissionResponse() {
                    @Override
                    public void onResponse(int code) {
                        if(code==IPermissionResponse.SUCCESS){
                            Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                        }else if(code == IPermissionResponse.REFUSE){
                            Toast.makeText(MainActivity.this,"拒绝授权",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"拒绝授权, 请去设置中开启授权",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(int i) {
                Toast.makeText(MainActivity.this,permissionDiscription[i]+ ": " + XPermission.getInstance().isGranted(MainActivity.this,permissions[i],true),Toast.LENGTH_SHORT).show();
            }
        });
    }
}