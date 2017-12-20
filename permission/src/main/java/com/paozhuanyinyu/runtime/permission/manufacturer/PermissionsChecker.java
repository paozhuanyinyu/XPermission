package com.paozhuanyinyu.runtime.permission.manufacturer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.paozhuanyinyu.rxpermissions.R;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
public class PermissionsChecker {
    private static final String TAG = "permissions4m";
    private static final String TAG_NUMBER = "1";
    private static boolean granted = false;

    /**
     * ensure whether permission granted
     *
     * @param activity
     * @param permission
     * @return true if granted else denied
     */
    public static boolean isPermissionGranted(Context activity, String permission, boolean defaultValue) {
        try {
            switch (permission) {
                case Manifest.permission.READ_CONTACTS:
                    return checkReadContacts(activity);
                case Manifest.permission.WRITE_CONTACTS:
                    return checkWriteContacts(activity);
                case Manifest.permission.GET_ACCOUNTS:
                    return defaultValue;
                case Manifest.permission.READ_CALL_LOG:
                    return checkReadCallLog(activity);
                case Manifest.permission.READ_PHONE_STATE:
                    boolean havePermission = checkReadPhoneState(activity);
                    return havePermission;
                case Manifest.permission.CALL_PHONE:
                    return defaultValue;
                case Manifest.permission.WRITE_CALL_LOG:
                    return checkWriteCallLog(activity);
                case Manifest.permission.USE_SIP:
                    return defaultValue;
                case Manifest.permission.PROCESS_OUTGOING_CALLS:
                    return defaultValue;
                case Manifest.permission.ADD_VOICEMAIL:
                    return defaultValue;

                case Manifest.permission.READ_CALENDAR:
                    return checkReadCalendar(activity);
                case Manifest.permission.WRITE_CALENDAR:
                    return defaultValue;

                case Manifest.permission.BODY_SENSORS:
                    return checkBodySensors(activity);

                case Manifest.permission.CAMERA:
                    return checkCamera(activity);

                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    return checkLocation(activity);

                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    return checkReadStorage(activity);
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    boolean haveWriteStorage = checkWriteStorage(activity);
                    return haveWriteStorage;

                case Manifest.permission.RECORD_AUDIO:
                    return checkRecordAudio(activity);

                case Manifest.permission.READ_SMS:
                    return checkReadSms(activity);
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS:
                case Manifest.permission.RECEIVE_SMS:
                    return defaultValue;
                case Manifest.permission.SYSTEM_ALERT_WINDOW:
                    boolean isCanDrawOverlays = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        isCanDrawOverlays = Settings.canDrawOverlays(activity);
                    }
                    return isCanDrawOverlays;
                case Manifest.permission.WRITE_SETTINGS:
                    boolean isCanWrite = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        isCanWrite = Settings.System.canWrite(activity);
                    }
                    return isCanWrite;
                default:
                    return defaultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "throwing exception in PermissionChecker:  ", e);
        }
        return false;
    }
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private static void show(Context mContext) {
//        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        wmParams.gravity = Gravity.CENTER;
//        wmParams.format = PixelFormat.RGBA_8888;
//        wmParams.x = mContext.getResources().getDisplayMetrics().widthPixels;
//        wmParams.y = 0;
//
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        View mView = LayoutInflater.from(mContext).inflate(R.layout.layout_log_float_window, null);
//        mWindowManager.addView(mView, wmParams);
//
//        Log.d(TAG,String.valueOf(mView.isShown()));
//        Log.d(TAG,String.valueOf(mWindowManager.getDefaultDisplay().getDisplayId()));
//        Rect rect = new Rect();
//        Log.d(TAG,String.valueOf(mView.getGlobalVisibleRect(rect)));
//        Log.d(TAG,String.valueOf(rect.top));
//        Log.d(TAG,String.valueOf(rect.bottom));
//        Log.d(TAG,String.valueOf(rect.left));
//        Log.d(TAG,String.valueOf(rect.right));
//        Rect rect1 = new Rect();
//        Log.d(TAG,String.valueOf(mView.getLocalVisibleRect(rect1)));
//        Log.d(TAG,String.valueOf(rect1.top));
//        Log.d(TAG,String.valueOf(rect1.bottom));
//        Log.d(TAG,String.valueOf(rect1.left));
//        Log.d(TAG,String.valueOf(rect1.right));
//    }
    private static boolean checkCamera(Context context){
        Camera mCamera = null;
        try{
            mCamera = Camera.open(0);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(mCamera==null){
            return false;
        }
        return true;
    }
    /**
     * record audio, {@link Manifest.permission#RECORD_AUDIO},
     * it will consume some resource!!
     *
     * @param activity
     * @return true if success
     */
    private static boolean checkRecordAudio(Context activity) throws Exception {
        AudioRecordManager recordManager = new AudioRecordManager();

        recordManager.startRecord(activity.getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + "/" +
                TAG + ".3gp");
        recordManager.stopRecord();

        return recordManager.getSuccess();
    }

    /**
     * read calendar, {@link Manifest.permission#READ_CALENDAR}
     *
     * @param activity
     * @return true if success
     */
    private static boolean checkReadCalendar(Context activity) throws Exception {
        Cursor cursor = activity.getContentResolver().query(Uri.parse("content://com" +
                ".android.calendar/calendars"), null, null, null, null);
        if (cursor != null) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write or delete call log, {@link Manifest.permission#WRITE_CALL_LOG}
     *
     * @param activity
     * @return true if success
     */
    private static boolean checkWriteCallLog(Context activity) throws Exception {
        ContentResolver contentResolver = activity.getContentResolver();
        ContentValues content = new ContentValues();
        content.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
        content.put(CallLog.Calls.NUMBER, TAG_NUMBER);
        content.put(CallLog.Calls.DATE, 20140808);
        content.put(CallLog.Calls.NEW, "0");
        contentResolver.insert(Uri.parse("content://call_log/calls"), content);

        contentResolver.delete(Uri.parse("content://call_log/calls"), "number = ?", new
                String[]{TAG_NUMBER});

        return true;
    }

    /**
     * read sms, {@link Manifest.permission#READ_SMS}
     * in MEIZU 5.0~6.0, just according normal phone request
     * in XIAOMI 6.0~, need force judge
     * in XIAOMI 5.0~6.0, not test!!!
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkReadSms(Context activity) throws Exception {
        Cursor cursor = activity.getContentResolver().query(Uri.parse("content://sms/"), null, null,
                null, null);
        if (cursor != null) {
            if (ManufacturerSupportUtil.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor, cursor.getColumnIndex(Telephony.Sms.DATE))) {
                    cursor.close();
                    return false;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write storage, {@link Manifest.permission#WRITE_EXTERNAL_STORAGE}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkWriteStorage(Context activity) throws Exception {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath(), TAG);
        if (!file.exists()) {
            boolean newFile;
            try {
                newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return newFile;
        } else {
            return file.delete();
        }
    }

    /**
     * read storage, {@link Manifest.permission#READ_EXTERNAL_STORAGE}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkReadStorage(Context activity) throws Exception {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath());
        File[] files = file.listFiles();
        return files != null;
    }

    /**
     * use location, {@link Manifest.permission#ACCESS_FINE_LOCATION},
     * {@link Manifest.permission#ACCESS_COARSE_LOCATION}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkLocation(Context activity) throws Exception {
        granted = false;
        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context
                .LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            if (!locationManager.isProviderEnabled("gps")) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, new
                            LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    locationManager.removeUpdates(this);
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                    locationManager.removeUpdates(this);
                                    granted = true;
                                }

                                @Override
                                public void onProviderEnabled(String provider) {
                                    locationManager.removeUpdates(this);
                                }

                                @Override
                                public void onProviderDisabled(String provider) {
                                    locationManager.removeUpdates(this);
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            return granted;
        }
    }

    /**
     * use sensors, {@link Manifest.permission#BODY_SENSORS}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkBodySensors(Context activity) throws Exception {
        SensorManager sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.unregisterListener(listener, sensor);

        return true;
    }

    /**
     * read phone state, {@link Manifest.permission#READ_PHONE_STATE}
     * <p>
     * in {@link com.paozhuanyinyu.runtime.permission.manufacturer.XIAOMI} or
     * {@link com.paozhuanyinyu.runtime.permission.manufacturer.OPPO}          :
     * -> {@link TelephonyManager#getDeviceId()} will be null if deny permission
     * <p>
     * in {@link com.paozhuanyinyu.runtime.permission.manufacturer.MEIZU}      :
     * -> {@link TelephonyManager#getSubscriberId()} will be null if deny permission
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    @SuppressLint("HardwareIds")
    private static boolean checkReadPhoneState(Context activity) throws Exception {
        TelephonyManager service = (TelephonyManager) activity.getSystemService
                (TELEPHONY_SERVICE);
        boolean havePermission = false;
        try {
            if (PermissionsPageManager.isMEIZU()) {
                havePermission = !TextUtils.isEmpty(service.getSubscriberId());
            } else if (PermissionsPageManager.isXIAOMI() || PermissionsPageManager.isOPPO()) {
                havePermission = !TextUtils.isEmpty(service.getDeviceId());
            } else {
                havePermission = !TextUtils.isEmpty(service.getDeviceId()) || !TextUtils.isEmpty(service.getSubscriberId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return havePermission;
    }

    /**
     * read call log, {@link Manifest.permission#READ_CALL_LOG}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkReadCallLog(Context activity) throws Exception {
        Cursor cursor = activity.getContentResolver().query(Uri.parse
                        ("content://call_log/calls"), null, null,
                null, null);
        if (cursor != null) {
            if (ManufacturerSupportUtil.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor, cursor.getColumnIndex(CallLog.Calls.NUMBER))) {
                    cursor.close();
                    return false;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * write and delete contacts info, {@link Manifest.permission#WRITE_CONTACTS}
     * and we should get read contacts permission first.
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkWriteContacts(Context activity) throws Exception {
        if (checkReadContacts(activity)) {
            // write some info
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = activity.getContentResolver();
            Uri rawContactUri = contentResolver.insert(ContactsContract.RawContacts
                    .CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds
                    .StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, TAG);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, TAG_NUMBER);
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

            // delete info
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID},
                    "display_name=?", new String[]{TAG}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(0);
                    resolver.delete(uri, "display_name=?", new String[]{TAG});
                    uri = Uri.parse("content://com.android.contacts/data");
                    resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
                }
                cursor.close();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * read contacts, {@link Manifest.permission#READ_CONTACTS}
     *
     * @param activity
     * @return true if success
     * @throws Exception
     */
    private static boolean checkReadContacts(Context activity) throws Exception {
        Cursor cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone
                .CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            if (ManufacturerSupportUtil.isForceManufacturer()) {
                if (isNumberIndexInfoIsNull(cursor, cursor.getColumnIndex(ContactsContract.CommonDataKinds
                        .Phone.NUMBER))) {
                    cursor.close();
                    return false;
                }
            }
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * in {@link com.paozhuanyinyu.runtime.permission.manufacturer.XIAOMI}
     * 1.denied {@link Manifest.permission#READ_CONTACTS} permission
     * ---->cursor.getCount == 0
     * 2.granted {@link Manifest.permission#READ_CONTACTS} permission
     * ---->cursor.getCount return real count in contacts
     * <p>
     * so when there are no user or permission denied, it will return 0
     *
     * @param cursor
     * @param numberIndex
     * @return true if can not get info
     */
    private static boolean isNumberIndexInfoIsNull(Cursor cursor, int numberIndex) {
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                return TextUtils.isEmpty(cursor.getString(numberIndex));
            }
            return false;
        } else {
            return true;
        }
    }
}
