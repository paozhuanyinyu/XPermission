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

import com.paozhuanyinyu.runtime.permission.Permission;
import com.paozhuanyinyu.runtime.permission.XPermission;
import com.paozhuanyinyu.runtime.sample.R;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RxPermissionsSample";
    ListView ll_permissions;
    private ArrayMap map;
    private String[] permissions = {
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
    };
    private String[] permissionDiscription = {
            "读取联系人(READ_CONTACTS)",
            "写入联系人(WRITE_CONTACTS)",
            "读取手机状态(READ_PHONE_STATE)",
            "读取通话记录(READ_CALL_LOG)",
            "写入通话记录(WRITE_CALL_LOG)",
            "读取日历(READ_CALENDAR)",
            "传感器(BODY_SENSORS)",
            "GPS定位(ACCESS_COARSE_LOCATION)",
            "基站定位(ACCESS_FINE_LOCATION)",
            "读取存储(READ_EXTERNAL_STORAGE)",
            "写入存储(WRITE_EXTERNAL_STORAGE)",
            "录音(RECORD_AUDIO)",
            "短信(READ_SMS)",

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
        MyAdapter myAdapter = new MyAdapter(this,permissionDiscription);
        ll_permissions.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyAdapter.onItemClickListener() {
            @Override
            public void onClick(int i) {
                Log.e("MainActivity","onItemClick");
                XPermission.getInstance()
                        .requestEach(MainActivity.this,permissions[i])
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if(permission.granted){
                                    Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                                }else if(permission.shouldShowRequestPermissionRationale){
                                    Toast.makeText(MainActivity.this,"拒绝授权",Toast.LENGTH_SHORT).show();
                                }else{
                                    //这里可以弹框提示去设置/手机管家设置，点击按钮跳转到设置/手机管家，使用接口getSettingsIntent(Context context)
                                    Toast.makeText(MainActivity.this,"请去设置里面开启授权",Toast.LENGTH_SHORT).show();
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