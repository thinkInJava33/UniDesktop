package com.scut.joe.unidesktop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.scut.joe.unidesktop.desktop.ElderlyDesktop;
import com.scut.joe.unidesktop.desktop.GuardianshipDesktop;
import com.scut.joe.unidesktop.desktop.IndividualityDesktop;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context mContext;
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
        modePreferences = getSharedPreferences("mode", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        chooseMode = modePreferences.getInt("choose", -1);

        if(chooseMode == -1){
            loadIndividualityInfo();
            dialog();
        }else{
            initDesktop(chooseMode);
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
                initDesktop(chooseMode);
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
        }
    }



    private  void loadIndividualityInfo() {
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

        dbManager manager = new dbManager(mContext);
        for (int i = 0; i < activities.size(); i++) {
            int pageNum = i / 15;
            int index = i % 15;
            ResolveInfo appInfo = activities.get(i);
            manager.addItem(2, i, appInfo.loadLabel(pm).toString(), appInfo.loadIcon(pm), appInfo.activityInfo.packageName,
                    appInfo.activityInfo.name, pageNum, index);
        }
        mode2Info = getSharedPreferences("mode2Info",MODE_PRIVATE);
        mode2Editor= modePreferences.edit();
        mode2Editor.putInt("page_num", activities.size()/15);
        mode2Editor.putInt("page_row", 5);
        mode2Editor.putInt("page_col", 3);
        mode2Editor.commit();
    }
}

