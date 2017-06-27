package com.scut.joe.unidesktop.apps;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by joe on 17-6-19.
 */

public class PhoneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v("test", "PhoneActivity is onCreate");
        super.onCreate(savedInstanceState);
    }
}
