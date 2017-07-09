package com.scut.joe.unidesktop.widget;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.content.Context;

import com.scut.joe.unidesktop.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Idoit on 2017/7/8.
 */

public class ClockWidget{
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

    private View views;
    private TextView time;
    private TextView apm;
    private TextView month;
    private TextView day;
    private TextView week;
    private Context mContext;

    public ClockWidget(Context context){


    }

}
