package com.scut.joe.unidesktop.desktop;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.page.ElderlyPage;
import com.scut.joe.unidesktop.page.IndividualityPage;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by joe on 17-6-16.
 */

public class IndividualityDesktop extends Desktop {
    private static Context mContext;
    ViewPager viewPager;

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
        View view = inflater.inflate(R.layout.fragment_desktop, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.container);
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

        return view;
    }

    @Override
    public void startApp() {

    }

    @Override
    public void arrangeApp() {

    }

    @Override
    public void hideApp() {

    }

    @Override
    public void addTool() {

    }

    @Override
    public void setWallpaper() {

    }

    @Override
    public void searchApp() {

    }

    @Override
    public void moveApp() {

    }
}
