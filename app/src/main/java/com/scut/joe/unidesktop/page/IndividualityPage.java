package com.scut.joe.unidesktop.page;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.adapter.DragAdapter;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by joe on 17-6-19.
 */

public class IndividualityPage extends Fragment{
    //DragGrid mDragGrid;
    RelativeLayout ll;

    private DragAdapter adapter;
    private DragGrid gridview;
    private Context mContext;
    //顶部导航的高度
    //public static int StruesHeight;
    /**
     * 获取gridview测量后的高度
     */
    int height;
    /**
     * 判断是否正在移动
     */
    private boolean isMove = false;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individuality_page, container, false);
        mContext = getActivity();
        gridview = (DragGrid)view.findViewById(R.id.gradview);
        //rl_strues = (RelativeLayout)findViewById(R.id.rl_strues);
        ll = (RelativeLayout)view.findViewById(R.id.ll);

        initDensityDpi();
        return view;
    }


    /**
     * 获取手机的屏幕密度DPI
     */
    private void initDensityDpi() {
        // TODO Auto-generated method stub
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Common.Width = metrics.widthPixels;
        Common.Height = metrics.heightPixels;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initDate();

        ViewTreeObserver vto = gridview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                gridview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = gridview.getHeight();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initDate() {
        // TODO Auto-generated method stub
        adapter = new DragAdapter(mContext, getList(), gridview);
        gridview.setRelativeLayout(ll);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (isMove) {
                    return;
                }
                AppItem dragView = (AppItem)parent.getItemAtPosition(position);
                ComponentName componentName = new ComponentName(dragView.getPackageName(),dragView.getClassName());
                Intent i = new Intent(Intent.ACTION_MAIN)
                        .setComponent(componentName)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);

                if (!dragView.getAppName().equals("更多")) {
                    Toast.makeText(mContext, dragView.getAppName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    

    /**
     * 获取初始化数据
     * @param list
     * @return
     */
    private List<AppItem> getList() {
        // TODO Auto-generated method stub
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities  = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });
        List<AppItem> list = new ArrayList<AppItem>();
        AppItem jcxx = null;
        for(int i = 0; i < 15; i++){
            ResolveInfo appInfo = activities.get(i);
            jcxx = new AppItem(i, appInfo.loadLabel(pm).toString(),appInfo.loadIcon(pm),appInfo.activityInfo.packageName,
                    appInfo.activityInfo.name);
            list.add(jcxx);
        }
        return list;
    }

  
}
