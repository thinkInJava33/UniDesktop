package com.scut.joe.unidesktop;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Contacts;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.scut.joe.unidesktop.controll.Check;
import com.scut.joe.unidesktop.desktop.ElderlyDesktop;
import com.scut.joe.unidesktop.desktop.GuardianshipDesktop;
import com.scut.joe.unidesktop.desktop.IndividualityDesktop;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.BackHandlerHelper;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int NOT_CHOOSE_MODE = -1;
    public static final int ELDERLY_MODE = 0;
    public static final int GUARDIANSHIP_MODE = 1;
    public static final int INDIVIDUALITY_MODE = 2;
    Context mContext;
    dbManager manager;
    SharedPreferences modePreferences; //保存用户的桌面模式
    SharedPreferences mode0Info;
    SharedPreferences mode1Info;
    SharedPreferences mode2Info;
    SharedPreferences.Editor modeEditor;
    SharedPreferences.Editor mode0Editor;
    SharedPreferences.Editor mode1Editor;
    SharedPreferences.Editor mode2Editor;
    int chooseMode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        manager = new dbManager(mContext);
        modePreferences = getSharedPreferences("mode", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        chooseMode = modePreferences.getInt("choose", NOT_CHOOSE_MODE);

        if(chooseMode == NOT_CHOOSE_MODE){
            dialog();
            Log.v("test", "dialog加载");
        }else{
            //判断模式对应的表是否有数据，没有则加载
            if(!manager.tableHasData(chooseMode)){
                initInfo(chooseMode);
                Log.v("test", "二次initInfo加载");
            }
            initDesktop(chooseMode);
            Log.v("test", "initDesktop加载");
        }
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    private void dialog(){
        chooseMode = ELDERLY_MODE;
        final String modes[] = {"老人模式", "监护模式", "个性模式"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择桌面模式");
        builder.setSingleChoiceItems(modes, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseMode = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modeEditor = modePreferences.edit();
                modeEditor.putInt("choose", chooseMode);
                modeEditor.commit();
                long startTime = System.nanoTime();
                initInfo(chooseMode);
                long costTime = System.nanoTime() - startTime;
                Log.v("timeTest", "initInfo加载耗时： "+ costTime/1000000 + "毫秒");
                startTime = System.nanoTime();
                initDesktop(chooseMode);
                costTime = System.nanoTime() - startTime;
                Log.v("timeTest", "initDesktop加载耗时: "+ costTime/1000000 + "毫秒");
            }
        });
        builder.show();
    }

    private void initDesktop(int chooseMode){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (chooseMode){
            case ELDERLY_MODE:
                transaction.replace(R.id.desktop, ElderlyDesktop.newInstance(mContext));
                transaction.commit();
                break;
            case GUARDIANSHIP_MODE:
                transaction.replace(R.id.desktop, GuardianshipDesktop.newInstance(mContext));
                transaction.commit();
                break;
            case INDIVIDUALITY_MODE:
                transaction.replace(R.id.desktop, IndividualityDesktop.newInstance(mContext));
                transaction.commit();
                break;
            default:
                break;
        }
    }

    private void initInfo(int mode){
        switch (mode){
            case ELDERLY_MODE:
                initElderlyInfo();
                break;
            case GUARDIANSHIP_MODE:
                initGuardianshipInfo();
                break;
            case INDIVIDUALITY_MODE:
                initIndividualityInfo();
                break;
            default:
                break;
        }
    }

    private void initElderlyInfo(){
        String tempPackageName = null;
        String tempClassName = null;
        int id = 0;
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(startupIntent, 0);
        for(ResolveInfo resolveInfo: resolveInfos){
            tempPackageName = resolveInfo.activityInfo.packageName;
            tempClassName = resolveInfo.activityInfo.name;
            if(Check.isSystemClock(resolveInfo)){
                manager.addItem(ELDERLY_MODE, id++, resolveInfo.loadLabel(pm).toString(),
                        this.getResources().getDrawable(R.drawable.clock_icon),
                        tempPackageName, tempClassName, 0, 0);
            }
            else if(Check.isSystemCalendar(resolveInfo)){
                manager.addItem(ELDERLY_MODE, id++, resolveInfo.loadLabel(pm).toString(),
                        this.getResources().getDrawable(R.drawable.calendar_icon),
                        tempPackageName, tempClassName, 0, 1);
            }
            else if(Check.isSystemGallery(resolveInfo)){
                manager.addItem(ELDERLY_MODE, id++, resolveInfo.loadLabel(pm).toString(),
                        this.getResources().getDrawable(R.drawable.photo_icon),
                        tempPackageName, tempClassName, 0, 2);
            }
            else if(Check.isSystemCamera(resolveInfo)){
                manager.addItem(ELDERLY_MODE, id++, resolveInfo.loadLabel(pm).toString(),
                        this.getResources().getDrawable(R.drawable.camera_icon),
                        tempPackageName, tempClassName, 0, 3);
            }
            else if(Check.isBrowser(resolveInfo, mContext)){
                manager.addItem(ELDERLY_MODE, id++, resolveInfo.loadLabel(pm).toString(),
                        this.getResources().getDrawable(R.drawable.web_icon),
                        tempPackageName, tempClassName, 0, 4);
            }
        }
        manager.addItem(ELDERLY_MODE, id++, "weather", this.getResources().getDrawable(R.drawable.weather_icon),
                "com.scut.joe.unidesktop", "com.scut.joe.unidesktop.apps.WeatherActivity", 0, 5);
        manager.addItem(ELDERLY_MODE, id++, "message", this.getResources().getDrawable(R.drawable.weather_icon),
                "com.scut.joe.unidesktop", "com.scut.joe.unidesktop.apps.MessageActivity", 0, 6);
        manager.addItem(ELDERLY_MODE, id++, "phone", this.getResources().getDrawable(R.drawable.weather_icon),
                "com.scut.joe.unidesktop", "com.scut.joe.unidesktop.apps.PhoneActivity", 0, 7);
        manager.addItem(ELDERLY_MODE, id++, "contacts", this.getResources().getDrawable(R.drawable.weather_icon),
                "com.scut.joe.unidesktop", "com.scut.joe.unidesktop.apps.ContactsActivity", 0, 8);
        mode0Info = getSharedPreferences("mode0Info",MODE_PRIVATE);
        mode0Editor= mode0Info.edit();
        mode0Editor.putInt("page_num", 1);
        mode0Editor.putInt("page_row", 4);
        mode0Editor.putInt("page_col", 2);
        mode0Editor.commit();
    }

    private void initGuardianshipInfo(){
        //TODO
    }

    private void initIndividualityInfo() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });
        List<AppItem> list = new ArrayList<>();

        //manager = new dbManager(mContext);
        for (int i = 0; i < activities.size(); i++) {
            int pageNum = i / 12;
            int index = i % 12;
            int row = -1;
            int isEmety = 0;
            ResolveInfo appInfo = activities.get(i);
            if(appInfo.activityInfo.packageName.equals("com.android.dialer")) {
                manager.addEmptyItem(INDIVIDUALITY_MODE, i + activities.size(), pageNum,index);
                pageNum = -1;
                index = 0;
            }
            if(appInfo.activityInfo.packageName.equals("com.android.contacts")){
                manager.addEmptyItem(INDIVIDUALITY_MODE, i + activities.size(), pageNum,index);
                pageNum = -1;
                index = 1;
            }
            if(appInfo.activityInfo.packageName.equals("com.android.mms")){
                manager.addItem(INDIVIDUALITY_MODE, i + activities.size(), appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                        appInfo.activityInfo.name, pageNum, index,row,1);
                pageNum = -1;
                index = 2;
            }
            if(appInfo.activityInfo.packageName.equals("com.android.browser")){
                manager.addItem(INDIVIDUALITY_MODE, i + activities.size(), appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                        appInfo.activityInfo.name, pageNum, index,row,1);
                pageNum = -1;
                index = 3;
            }
            AppItem item = new AppItem(i, appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                    appInfo.activityInfo.name, pageNum, index);
            //manager.addItem(INDIVIDUALITY_MODE, i, appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                    //appInfo.activityInfo.name, pageNum, index);
            list.add(item);
        }
        manager.addItems(list, INDIVIDUALITY_MODE);
        mode2Info = getSharedPreferences("mode2Info",MODE_PRIVATE);
        mode2Editor= mode2Info.edit();
        mode2Editor.putInt("page_num", activities.size()/15 + 1);
        mode2Editor.putInt("page_row", 5);
        mode2Editor.putInt("page_col", 3);
        mode2Editor.commit();
    }
}

