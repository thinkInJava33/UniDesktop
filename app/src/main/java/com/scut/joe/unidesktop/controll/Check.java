package com.scut.joe.unidesktop.controll;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by joe on 17-6-27.
 */

public class Check {

    /**
     *
     * @param info　应用信息类
     * @return 是否为系统应用
     */
    public static boolean isSystemApplication(ApplicationInfo info){
        if(info != null){
            return ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)!= 0
            || (info.flags & ApplicationInfo.FLAG_SYSTEM)!= 0)? true:false;
        }else return false;
    }

    public static boolean isSystemClock(ResolveInfo info){
        String packageName = info.activityInfo.packageName;
        if(packageName.contains("com") && packageName.contains("android")){
            if(packageName.contains("clock")){
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemCalendar(ResolveInfo info){
        String packageName = info.activityInfo.packageName;
        if(packageName.contains("com") && packageName.contains("android")){
            if(packageName.contains("calendar")){
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemGallery(ResolveInfo info){
        String packageName = info.activityInfo.packageName;
        if(packageName.contains("com") && packageName.contains("android")){
            if(packageName.contains("gallery")||packageName.contains("photo")){
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemCamera(ResolveInfo info){
        String packageName = info.activityInfo.packageName;
        if(packageName.contains("com") && packageName.contains("android")){
            if(packageName.contains("camera")){
                return true;
            }
        }
        return false;
    }

    public static boolean isBrowser(ResolveInfo info, Context context){
        String packageName = info.activityInfo.packageName;
        String className = info.activityInfo.name;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.BROWSABLE");
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        if(packageName.equals(list.get(0).activityInfo.packageName)
                && className.equals(list.get(0).activityInfo.name)){
            return true;
        }
        return false;
    }
}
