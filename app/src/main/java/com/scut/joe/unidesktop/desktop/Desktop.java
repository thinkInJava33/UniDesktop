package com.scut.joe.unidesktop.desktop;


import android.support.v4.app.Fragment;

/**
 * Created by joe on 17-6-19.
 */

public  abstract class Desktop extends Fragment {
    public abstract void startApp();
    public abstract void arrangeApp();
    public abstract void hideApp();
    public abstract void addTool();
    public abstract void setWallpaper();
    public abstract void searchApp();
    public abstract void moveApp();
}
