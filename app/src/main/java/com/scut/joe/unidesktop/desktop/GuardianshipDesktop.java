package com.scut.joe.unidesktop.desktop;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by joe on 17-6-16.
 */

public class GuardianshipDesktop extends Desktop {
    private static Context mContext;

    public static GuardianshipDesktop newInstance(Context context){
        Bundle args = new Bundle();
        mContext = context;
        GuardianshipDesktop guardianshipDesktop = new GuardianshipDesktop();
        guardianshipDesktop.setArguments(args);
        return guardianshipDesktop;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("test", "this is guardianshipDesktop");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void startApp() {

    }

    @Override
    public void arrangeApp() {

    }

    @Override
    public void hideApp() {

    }

    @Override
    public void addTool() {

    }

    @Override
    public void setWallpaper() {

    }

    @Override
    public void searchApp() {

    }

    @Override
    public void moveApp() {

    }
}
