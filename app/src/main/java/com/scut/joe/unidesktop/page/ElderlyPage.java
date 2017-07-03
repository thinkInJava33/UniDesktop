package com.scut.joe.unidesktop.page;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.adapter.IconAdapter;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.FragmentBackHandler;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.List;


/**
 * Created by joe on 17-6-19.
 */

public class ElderlyPage extends Fragment implements FragmentBackHandler{
    RelativeLayout rl;
    private int pageIndex;
    private IconAdapter adapter;
    private DragGrid dragGrid;
    private Context mContext;

    /**
     * 获取gridview测量后的高度
     */
    int height;
    /**
     * 判断是否正在移动
     */
    private boolean isMove = false;

    public static ElderlyPage newInstance(int pageIndex){
        Bundle args = new Bundle();
        args.putSerializable("page_num",pageIndex);
        ElderlyPage fragment = new ElderlyPage();
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
    public void onResume() {
        super.onResume();
        initData();

        ViewTreeObserver vto = dragGrid.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                dragGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = dragGrid.getHeight();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elderly_page, container, false);
        mContext = getActivity();
        rl = (RelativeLayout)view.findViewById(R.id.rl);
        dragGrid = (DragGrid)rl.findViewById(R.id.grad_view);

        return view;
    }

    @Override
    public boolean onBackPressed() {
        Common.isDrag = false;
        dragGrid.refresh();
        return true;
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
        Log.v("test", "width: "+ Common.Width+" height: " + Common.Height);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        // TODO Auto-generated method stub
        adapter = new IconAdapter(mContext, getList(), dragGrid);
        dragGrid.setRelativeLayout(rl);
        dragGrid.setAdapter(adapter);
        dragGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (isMove) {
                    return;
                }
                AppItem dragView = (AppItem)parent.getItemAtPosition(position);
                ComponentName componentName = new ComponentName(dragView.getPackageName(),dragView.getClassName());
                Intent i = new Intent().setComponent(componentName);

                startActivity(i);
            }
        });
    }

    private List<AppItem> getList() {
        // TODO Auto-generated method stub
        dbManager manager = new dbManager(getActivity());
        Log.i("test","pageNum : "+pageIndex);
        List<AppItem> list = manager.getApps(0,pageIndex);
        for(AppItem appItem:list){
            Log.v("test", " "+ appItem.getAppName());
        }
        return list;
    }
}
