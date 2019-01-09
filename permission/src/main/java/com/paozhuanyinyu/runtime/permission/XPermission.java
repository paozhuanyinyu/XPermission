/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paozhuanyinyu.runtime.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.paozhuanyinyu.runtime.permission.manufacturer.PermissionsChecker;
import com.paozhuanyinyu.runtime.permission.manufacturer.PermissionsPageManager;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
public class XPermission {

    static final String TAG = "XPermission";
    static final Object TRIGGER = new Object();
    private static XPermission sInstance;
    private XPermission(){
    }
    public static XPermission getInstance(){
        if(sInstance==null){
            synchronized (TRIGGER){
                if(sInstance==null){
                    sInstance = new XPermission();
                }
            }
        }
        return sInstance;
    }

    public void setLogging(boolean logging) {
        XPermissionActivity.setLogging(logging);
    }
    /**
     * Map emitted items from the source observable into {@code true} if permissions in parameters
     * are granted, or {@code false} if not.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Boolean> ensure(final Context context,final Params... permissions) {
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<T> o) {
                return request(context,o, permissions)
                        // Transform Observable<Permission> to Observable<Boolean>
                        .buffer(permissions.length)
                        .flatMap(new Function<List<Permission>, ObservableSource<Boolean>>() {
                            @Override
                            public ObservableSource<Boolean> apply(List<Permission> permissions) throws Exception {
                                if (permissions.isEmpty()) {
                                    // Occurs during orientation change, when the subject receives onComplete.
                                    // In that case we don't want to propagate that empty list to the
                                    // subscriber, only the onComplete.
                                    return Observable.empty();
                                }
                                // Return true if all permissions are granted.
                                for (Permission p : permissions) {
                                    if (!p.granted) {
                                        return Observable.just(false);
                                    }
                                }
                                return Observable.just(true);
                            }
                        });
            }
        };
    }

    /**
     * Map emitted items from the source observable into {@link Permission} objects for each
     * permission in parameters.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Permission> ensureEach(final Context context,final Params... permissions) {
        return new ObservableTransformer<T, Permission>() {
            @Override
            public ObservableSource<Permission> apply(Observable<T> o) {
                return request(context,o, permissions);
            }
        };
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
//    @SuppressWarnings({"WeakerAccess", "unused"})
//    public Observable<Boolean> request(Context context,final Params permission) {
//        return Observable.just(TRIGGER).compose(ensure(context,permission));
//    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Permission> requestEach(Context context,final Params permission) {
        return Observable.just(TRIGGER).compose(ensureEach(context,permission));
    }

    /**
     * whether really have the permission
     * @param context
     * @param permission
     * @param defaultValue
     * @return
     */
    public boolean isGranted(Context context,String permission,boolean defaultValue){
        return PermissionsChecker.isPermissionGranted(context,permission,defaultValue);
    }

    /**
     * return the intent of permission mamange activity
     * @param context
     * @return Intent
     */
    public Intent getSettingsIntent(Context context){
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//            return new Protogenesis().settingIntent(context);
//        }
        return PermissionsPageManager.getIntent(context);
    }
    public Intent getSettingsIntent(Context context,String permission){
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//            return new Protogenesis().settingIntent(context);
//        }
        return PermissionsPageManager.getIntent(context,permission);
    }
    private Observable<Permission> request(final Context context,final Observable<?> trigger, final Params... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("XPermission.request/requestEach requires at least one input permission");
        }
        return oneOf(trigger, pending(permissions))
                .flatMap(new Function<Object, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> apply(Object o) throws Exception {
                        return requestImplementation(context,permissions);
                    }
                });
    }

    private Observable<?> pending(final Params... permissions) {
        for (Params p : permissions) {
            if (!XPermissionActivity.containsByPermission(p)) {
                return Observable.empty();
            }
        }
        return Observable.just(TRIGGER);
    }

    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
        if (trigger == null) {
            return Observable.just(TRIGGER);
        }
        return Observable.merge(trigger, pending);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Observable<Permission> requestImplementation(Context context,final Params... permissions) {
        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (Params permission : permissions) {
            XPermissionActivity.log("Requesting permission " + permission);
            if (isGranted(context,permission.permissionName)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(new Permission(permission.permissionName, true, false)));
                continue;
            }

//            if (isRevoked(context,permission)) {
//                // Revoked by a policy, return a denied Permission object.
//                list.add(Observable.just(new Permission(permission, false, false)));
//                continue;
//            }

            unrequestedPermissions.add(permission.permissionName);
            PublishSubject<Permission> subject = PublishSubject.create();
            XPermissionActivity.setSubjectForPermission(permission.permissionName, subject);
            XPermissionActivity.setParams(permission);


            list.add(subject);
        }
        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            requestPermissionsFromActivity(context,unrequestedPermissionsArray);
        }
        return Observable.concat(Observable.fromIterable(list));
    }
    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromActivity(Context context, String[] permissions) {
        XPermissionActivity.log("requestPermissionsFromActivity " + TextUtils.join(", ", permissions));
        Intent intent = new Intent(context,XPermissionActivity.class);
        intent.putExtra("permissions",permissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    boolean isGranted(Context context,String permission) {
        return !isMarshmallow() || isPermissionGranted(context,permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    boolean isRevoked(Context context,String permission) {
        return isMarshmallow() && isPermissionRevoked(context,permission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isPermissionGranted(Context context,String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isPermissionRevoked(Context context,String permission) {
        return context.getPackageManager().isPermissionRevokedByPolicy(permission, context.getPackageName());
    }

    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


}
