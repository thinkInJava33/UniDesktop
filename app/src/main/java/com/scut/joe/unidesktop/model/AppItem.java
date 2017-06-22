package com.scut.joe.unidesktop.model;

import android.graphics.drawable.Drawable;

/**
 * Created by joe on 17-6-16.
 */

public class AppItem {
    //private static int appCount = 0;
    private int id;
    private String appName;
    private Drawable appIcon;
    private String packageName;
    private String className;
    private int pageNum;
    private int rowIndex;
    private int colIndex;


    public AppItem(){}

    public AppItem(int id,String appName, Drawable appIcon,String packageName,String className){
        setAppIcon(appIcon);
        setAppName(appName);
        setPackageName(packageName);
        setClassName(className);
        setId(id);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
