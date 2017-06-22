package com.scut.joe.unidesktop;

import android.app.AlertDialog;
import android.app.Dialog;
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

public class MainActivity extends AppCompatActivity {
    Context mContext;
    SharedPreferences modePreferences; //保存用户的桌面模式
    SharedPreferences.Editor modeEditor;
    int chooseMode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        modePreferences = getSharedPreferences("mode", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        chooseMode = modePreferences.getInt("choose", -1);

        if(chooseMode == -1){
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
}
