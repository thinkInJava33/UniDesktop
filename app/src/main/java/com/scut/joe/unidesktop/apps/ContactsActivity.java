package com.scut.joe.unidesktop.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by joe on 17-6-26.
 */

public class ContactsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v("test", "ContactsActivity is onCreate");
        super.onCreate(savedInstanceState);
    }
}
