package com.scut.joe.unidesktop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

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
        chooseMode = modePreferences.getInt("choose", -1);

        if(chooseMode == -1){
            dialog();
            initInfo(chooseMode);
        }else{
            //判断模式对应的表是否有数据，没有则加载
            if(!manager.tableHasData(chooseMode)){
                initInfo(chooseMode);
            }
        }
        initDesktop(chooseMode);
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    private void dialog(){
        chooseMode = 0;
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
            }
        });
        builder.show();
    }

    private void initDesktop(int chooseMode){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (chooseMode){
            case 0:
                transaction.replace(R.id.desktop, ElderlyDesktop.newInstance(mContext));
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.desktop, GuardianshipDesktop.newInstance(mContext));
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.desktop, IndividualityDesktop.newInstance(mContext));
                transaction.commit();
                break;
            default:
                break;
        }
    }

    private void initInfo(int mode){
        switch (mode){
            case 0:
                initElderlyInfo();
                break;
            case 1:
                initGuardianshipInfo();
                break;
            case 2:
                initIndividualityInfo();
                break;
            default:
                break;
        }
    }

    private void initElderlyInfo(){
        PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        //List<PackageInfo>
        //for()
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
        //List<AppItem> list = new ArrayList<AppItem>();
        // AppItem jcxx = null;

        //manager = new dbManager(mContext);
        for (int i = 0; i < activities.size(); i++) {
            int pageNum = i / 12;
            int index = i % 12;
            ResolveInfo appInfo = activities.get(i);
            if(appInfo.activityInfo.packageName.equals("com.android.dialer")) {
                pageNum = -1;
                index = 0;
            }
            if(appInfo.activityInfo.packageName.equals("com.android.contacts")){
                pageNum = -1;
                index = 1;
            }
            if(appInfo.activityInfo.packageName.equals("com.android.mms")){
                pageNum = -1;
                index = 2;
            }
            if(appInfo.activityInfo.packageName .equals("com.android.browser")){
                pageNum = -1;
                index = 3;
            }
            manager.addItem(2, i, appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                    appInfo.activityInfo.name, pageNum, index);
        }
        mode2Info = getSharedPreferences("mode2Info",MODE_PRIVATE);
        mode2Editor= mode2Info.edit();
        mode2Editor.putInt("page_num", activities.size()/15 + 1);
        mode2Editor.putInt("page_row", 5);
        mode2Editor.putInt("page_col", 3);
        mode2Editor.commit();
    }
}

