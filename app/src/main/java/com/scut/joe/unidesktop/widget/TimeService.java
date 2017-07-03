package com.scut.joe.unidesktop.widget;

/**
 * Created by Idoit on 2017/7/1.
 */

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.scut.joe.unidesktop.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeService extends Service {
    static String TAG = "ClockAppWidgetProvider";
    private RemoteViews views;

    // 月份
    // 星期
    private String[] months = new String[] { "01/", "02/", "03/", "04/", "05/",
            "06/", "07/", "08/", "09/", "10/", "11/", "12/" };
    private String[] weeks = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu",
            "Fri", "Sat" };
    // private String[] weeksid = new String[]{"Minggu", "Senin", "Selasa",
    // "Rabu", "Kamis", "Jumat", "Sabtu"};
    private String[] weeksid = new String[] { "Min", "Sen", "Sel", "Rab",
            "Kam", "Jum", "Sab" };
    private String[] days = new String[] { "0", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28", "29", "29", "30", "31" };


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.i(TAG, "onCreate");
        startClockListener();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(this.timeReceiver);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        updateAppWidget();
        super.onStart(intent, startId);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "onReceive intent.getAction()"+intent.getAction());
            if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())
                    ||"android.intent.action.TIME_TICK".equals(intent.getAction())
                    ||"android.intent.action.TIME_CHANGED".equals(intent.getAction())
                    ||"android.intent.action.TIME_SET".equals(intent.getAction()))
            {
                updateAppWidget();
            }

        }
    };

    public void startClockListener()
    {
        Log.i(TAG, "startClockListener");
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.TIME_TICK");
        localIntentFilter.addAction("android.intent.action.TIME_CHANGED");
        localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        localIntentFilter.addAction("android.intent.action.TIME_SET");
        registerReceiver(this.timeReceiver, localIntentFilter);
    }

    public void updateAppWidget() {
        Log.i(TAG, "updateAppWidget");
        // 构造RemoteViews对象
        views = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.widget);

        // 构造显示时间日期
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());

        // 构造当期显示时间
        StringBuffer currentTime = new StringBuffer("");
        int temp = calendar.get(Calendar.HOUR_OF_DAY);

        // 设置显示当前时间
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            if (temp < 10) {
                currentTime.append("0").append(temp);
            } else {
                currentTime.append(temp);
            }
        } else// 12小时制
        {
            Log.i(TAG, "00000000  temp ==" + temp);
            if (calendar.get(Calendar.AM_PM) == 0 && temp == 0) {
                currentTime.append(temp + 12);
                Log.i(TAG, "1111111111111111");
            } else {

                Log.i(TAG, "2222222222222222222");
                if (temp < 13) {
                    if (temp < 10) {
                        currentTime.append("0").append(temp);
                    } else {
                        currentTime.append(temp);
                    }
                } else {
                    temp -= 12;
                    if (temp < 10) {
                        currentTime.append("0").append(temp);
                    } else {
                        currentTime.append(temp);
                    }
                }
            }

        }

        temp = calendar.get(Calendar.MINUTE);
        if (temp < 10) {
            currentTime.append(":").append("0").append(temp);
        } else {
            currentTime.append(":").append(temp);
        }
        Log.i(TAG, "currentTime.toString()" + currentTime.toString());
        views.setTextViewText(R.id.txt_time, currentTime.toString());

        // 设置显示当前时间 AM PM
        String ampmValues;
        if (DateFormat.is24HourFormat(getApplicationContext())) {
            ampmValues = "";
        } else {
            if (calendar.get(Calendar.AM_PM) == 0) {
                ampmValues = "AM";
            } else {
                ampmValues = "PM";
            }
        }
        views.setTextViewText(R.id.txt_APM, ampmValues);

        // 设置显示当前月份
        views.setTextViewText(R.id.txt_month,
                months[calendar.get(Calendar.MONTH)]);
        // 设置显示当前星期

        // Locale locale = getResources().getConfiguration().locale;
        String language = Locale.getDefault().getLanguage();
        if (language.endsWith("in")) {
            views.setTextViewText(R.id.txt_week,
                    weeksid[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        } else {
            views.setTextViewText(R.id.txt_week,
                    weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        }

        // views.setTextViewText(R.id.txt_week,
        // String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));

        // 设置显示当前日期
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            views.setTextViewText(R.id.txt_day,
                    days[calendar.get(Calendar.DAY_OF_MONTH)]);
        } else {
            views.setTextViewText(R.id.txt_day,
                    String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }

        /*
         * final int N = appWidgetIds.length; for(int i = 0; i < N; i++){ int
         * appWidgetId = appWidgetIds[i];
         * WidgetManager.updateAppWidget(appWidgetIds, views); }
         *
         * final AppWidgetManager gm = AppWidgetManager.getInstance(context); if
         * (appWidgetIds != null) { Log.i(TAG, "appWidgetIds != null");
         * gm.updateAppWidget(appWidgetIds, views); } else { Log.i(TAG,
         * "appWidgetIds == null"); gm.updateAppWidget(new
         * ComponentName(context, this.getClass()), views); }
         */
        // 将当前的ClockAppWidgetProvider包装成ComponentName对象
        // ComponentName componentName = new ComponentName(context,
        // ClockAppWidgetProvider.class);

        // 更新App Widget
        // AppWidgetManager.updateAppWidget(componentName, views);

        // 将该界面显示到插件中
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());
        ComponentName componentName = new ComponentName(getApplicationContext(),
                ClockAppWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, views);

    }

}