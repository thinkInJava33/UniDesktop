package com.scut.joe.unidesktop.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joe on 17-7-6.
 */

public class PermissionManager {
    private static final int REQUEST_CODE = 0X0001;
    private HashMap<String, Integer>hashMap;

    private static class Holder{
        public static final PermissionManager MANAGER = new PermissionManager();
    }

    private static Context mContext;

    private PermissionManager(){}

    /**
     * 单例获取对象
     * @param context
     * @return
     */
    public static PermissionManager getInstance(Context context){
        mContext = context.getApplicationContext();
        return Holder.MANAGER;
    }

    /**
     * 执行请求多个权限
     * @param activity
     * @param permissions
     */
    public void execute(@NonNull Activity activity, String... permissions){
        List<String> lists = new ArrayList<>();
        for(String permission:permissions){
            if(!isGranted(permission) && !isRevoked(permission)){
                lists.add(permission);
            }
        }
        if(lists.size() == 0) return;
        String[] p = new String[lists.size()];
        ActivityCompat.requestPermissions(activity, lists.toArray(p), REQUEST_CODE);
    }

    public void execute(@NonNull Activity activity, String permission){
        if(!isGranted(permission)){
            requestPeris(activity, permission);
        }
    }

    private void requestPeris(@NonNull Activity activity, String... permissions){
        if(isM()){
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }

    /**
     * 判断是不是授权
     * @param permission
     * @return
     */
    private boolean isGranted(@NonNull String permission){
        return isM() && ContextCompat.checkSelfPermission(mContext,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断是不是在包中申明
     * @param permission
     * @return
     */
    private boolean isRevoked(@NonNull String permission){
        return isM() && mContext.getPackageManager().
                isPermissionRevokedByPolicy(permission, mContext.getPackageName());
    }

    /**
     * 判断是不是M及以上版本
     * @return
     */
    private boolean isM(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
