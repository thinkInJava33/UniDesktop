package com.scut.joe.unidesktop.widget;

/**
 * Created by Idoit on 2017/6/30.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockAppWidgetProvider extends AppWidgetProvider {
    static String TAG = "ClockAppWidgetProvider";
    private RemoteViews views;
    int[] appWidgetIds;

    private final String broadCastString = "com.wd.appWidgetUpdate";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 当判断到是该事件发过来时， 我们就获取插件的界面， 然后将index自加后传入到textview中
        // System.out.println("onReceive");
        Log.i(TAG, "onReceive");
        Log.i(TAG, "intent.getAction()" + intent.getAction());
        if (intent.getAction().equals(broadCastString)
                || intent.getAction().equals(
                "android.intent.action.TIME_CHANGED")
                || intent.getAction().equals("android.intent.action.TIME_TICK")
                || intent.getAction().equals("android.intent.action.TIME_SET")) {
            {
                //  updateAppWidget(context);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        context.startService(new Intent(context, TimeService.class));
    }


    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        context.stopService(new Intent(context, TimeService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        context.startService(new Intent(context, TimeService.class));
        // 创建定时器
        this.appWidgetIds = appWidgetIds;

    }

}