package com.scut.joe.unidesktop.page;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.apps.SearchActivity;
import com.scut.joe.unidesktop.model.DragAdapter;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.FragmentBackHandler;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.List;

/**
 * Created by joe on 17-6-19.
 */

public class IndividualityPage extends Fragment implements FragmentBackHandler{
    //DragGrid mDragGrid;
    RelativeLayout ll;
    private int pageIndex;
    private DragAdapter adapter;
    private DragGrid gridview;
    private Context mContext;
    float mPosX = 0, mPosY = 0 , mCurPosX = 0, mCurPosY = 0;
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

    public static IndividualityPage newInstance(int pageIndex){
        Bundle args = new Bundle();
        args.putSerializable("page_num",pageIndex);
        IndividualityPage fragment = new IndividualityPage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageIndex = (int) getArguments().getSerializable("page_num");
        initDensityDpi();
    }

    @Override
    public boolean onBackPressed() {
        Common.isDrag = false;
        gridview.refresh();
        return true;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_individuality_page, container, false);
        mContext = getActivity();
        ll = (RelativeLayout)view.findViewById(R.id.ll);
        gridview = (DragGrid)ll.findViewById(R.id.gradview);
        //rl_strues = (RelativeLayout)findViewById(R.id.rl_strues);

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
        initData();

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
    private void initData() {
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
                Log.i("test","click");
                startActivity(i);

                if (!dragView.getAppName().equals("更多")) {
                    Toast.makeText(mContext, dragView.getAppName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    

    /**
     * 获取初始化数据
     */
    private List<AppItem> getList() {
        // TODO Auto-generated method stub
        dbManager manager = new dbManager(getActivity());
        Log.i("test","pageNum : "+pageIndex);
        List<AppItem> list = manager.getApps(2,pageIndex);
        return list;
    }

  
}
