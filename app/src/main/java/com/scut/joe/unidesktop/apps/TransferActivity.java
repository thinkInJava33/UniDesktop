package com.scut.joe.unidesktop.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.scut.joe.unidesktop.R;

/**
 * Created by joe on 17-7-12.
 */

public class TransferActivity extends AppCompatActivity {
    SharedPreferences modePreference;
    SharedPreferences.Editor modeEditor;
    static final int NOT_CHOOSE_MODE = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_view);

        modePreference = getSharedPreferences("mode", Context.MODE_PRIVATE);
        modeEditor = modePreference.edit();
        modeEditor.putInt("choose", NOT_CHOOSE_MODE);
        modeEditor.commit();

        Intent i = new Intent();
        ComponentName componentName = new ComponentName("com.scut.joe.unidesktop", "com.scut.joe.unidesktop.MainActivity");
        i.setComponent(componentName);
        startActivity(i);
    }
}
