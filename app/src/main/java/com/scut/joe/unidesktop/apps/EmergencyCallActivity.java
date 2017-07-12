package com.scut.joe.unidesktop.apps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joe on 17-7-12.
 */

public class EmergencyCallActivity extends AppCompatActivity {
    static final int SUCCESS = 1;

    SharedPreferences logPreference;
    SharedPreferences.Editor logEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logPreference = getSharedPreferences("log", Context.MODE_PRIVATE);
        Boolean isFirst = logPreference.getBoolean("first", true);

        if(isFirst){
            dialog();
        }
    }

    private void dialog(){
        new AlertDialog.Builder(this)
                .setTitle("开启快速联系")
                .setMessage("请选择发生紧急情况"
                        + "快速联系的一位联系人")
                .setPositiveButton("开启",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent, SUCCESS);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                }).setCancelable(false).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SUCCESS:
                if(resultCode == RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();

                }
                break;
            default:
                break;
        }
    }
}
