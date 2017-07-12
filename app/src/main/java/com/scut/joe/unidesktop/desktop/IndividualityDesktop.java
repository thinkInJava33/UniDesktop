package com.scut.joe.unidesktop.desktop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.adapter.DragAdapter;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.page.IndividualityPage;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.FragmentBackHandler;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by joe on 17-6-16.
 */

public class IndividualityDesktop extends Desktop implements FragmentBackHandler{
    private static Context mContext;
    private View v;
    ViewPager viewPager;
    private RelativeLayout rootll;
    private DragGrid mToolBar;
    private DragAdapter adapter;
    /**
     * 获取gridview测量后的高度
     */
    int height;
    /**
     * 判断是否正在移动
     */
    private boolean isMove = false;

    private static final String[] PERMISSION_EXTERNAL_STORAGE = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 100;

    public static IndividualityDesktop newInstance(Context context){
        Bundle args = new Bundle();
        mContext = context;
        IndividualityDesktop individualityDesktop = new IndividualityDesktop();
        individualityDesktop.setArguments(args);
        return individualityDesktop;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "this is IndividualityDesktop");
        v = inflater.inflate(R.layout.fragment_individuality_desktop, container, false);
        setWallpaper();
        viewPager = (ViewPager) v.findViewById(R.id.container);
        rootll = (RelativeLayout) v.findViewById(R.id.lll);
        mToolBar = (DragGrid) rootll.findViewById(R.id.toolBar);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Log.i("test","current position" + position);
                return IndividualityPage.newInstance(position);
            }

            @Override
            public int getCount() {
                int count = getActivity().getSharedPreferences("mode2Info",MODE_PRIVATE).getInt("page_num",2);
                return count;
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setWallpaper();
        Log.i("test","onstart");
    }

    @Override
    public boolean onBackPressed() {
        Common.isDrag = false;
        mToolBar.refresh();
        Log.v("test", "desktop onBackPressed");
        return true;
    }

    @Override
    public void startApp() {
        //complete
    }

    @Override
    public void arrangeApp() {
        //complete
    }

    @Override
    public void hideApp() {
        //complete
    }

    @Override
    public void addTool() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(mContext);
        // 获取当前壁纸
        String wallPaper = mContext.getSharedPreferences("wallPaper",MODE_PRIVATE).getString("imagePath","");
       if(wallPaper.equals("")) {
           Drawable wallpaperDrawable = wallpaperManager.getDrawable();
           v.setBackgroundDrawable(wallpaperDrawable);
       }else{
           verifyStoragePermissions(getActivity());
           Bitmap wallPaperBitmap = BitmapFactory.decodeFile(wallPaper);
           Drawable wallPaperDrawable = new BitmapDrawable(wallPaperBitmap);
           v.setBackgroundDrawable(wallPaperDrawable);
       }
    }

    @Override
    public void searchApp() {

    }

    @Override
    public void moveApp() {
        //completed
    }

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

        ViewTreeObserver vto = mToolBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                mToolBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height = mToolBar.getHeight();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        // TODO Auto-generated method stub
        adapter = new DragAdapter(mContext, getList(), mToolBar);
        mToolBar.setRelativeLayout(rootll);
        mToolBar.setAdapter(adapter);
        mToolBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
     */
    private List<AppItem> getList() {
        // TODO Auto-generated method stub
        dbManager manager = new dbManager(getActivity());
        //Log.i("test","pageNum : "+pageIndex);
        List<AppItem> list = manager.getApps(2,-1);
        for(AppItem app:list){
            app.setAppName("");
        }
        return list;
    }

    private void verifyStoragePermissions(Activity activity) {
        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_EXTERNAL_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
