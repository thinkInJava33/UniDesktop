package com.scut.joe.unidesktop.apps;

import android.content.ComponentName;
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
import android.widget.Toast;

/**
 * Created by joe on 17-7-12.
 */

public class EmergencyCallActivity extends AppCompatActivity {
    static final int SUCCESS = 1;

    SharedPreferences logPreference;
    SharedPreferences.Editor logEditor;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        logPreference = getSharedPreferences("log", Context.MODE_PRIVATE);
        Boolean isFirst = logPreference.getBoolean("first", true);

        if(isFirst){
            dialog();
        }
        else{
            String number = logPreference.getString("num", null);
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + number));
            startActivity(i);
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
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "未选择紧急联系人", Toast.LENGTH_SHORT).show();
                        backToDesktop();
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
                    String num = getContactPhone(cursor);
                    logEditor = logPreference.edit();
                    logEditor.putString("num", num);
                    logEditor.putBoolean("first", false);
                    logEditor.commit();
                    backToDesktop();
                }
                break;
            default:
                break;
        }
    }

    private String getContactPhone(Cursor cursor) {
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }

    private void backToDesktop(){
        Intent i = new Intent();
        ComponentName componentName = new ComponentName("com.scut.joe.unidesktop", "com.scut.joe.unidesktop.MainActivity");
        i.setComponent(componentName);
        startActivity(i);
    }
}
