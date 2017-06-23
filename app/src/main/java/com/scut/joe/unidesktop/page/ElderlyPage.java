package com.scut.joe.unidesktop.page;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.adapter.DragAdapter;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.FragmentBackHandler;


/**
 * Created by joe on 17-6-19.
 */

public class ElderlyPage extends Fragment implements FragmentBackHandler{
    RelativeLayout rl;
    private int pageIndex;
    private DragAdapter adapter;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        mContext = getActivity();
        dragGrid = (DragGrid)view.findViewById(R.id.grad_view);
        rl = (RelativeLayout)view.findViewById(R.id.rl);

        initDensityDpi();
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
    }
}
