package com.scut.joe.unidesktop.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.List;

/**
 * Created by joe on 17-6-23.
 */

public class BackHandlerHelper {
    /**
     * 分发back事件给FragmentManager管理的Fragment
     */
    public static boolean handleBackPress(FragmentManager fragmentManager){
        List<Fragment> fragments = fragmentManager.getFragments();
        boolean isHandle = false;
        if(fragments == null) return isHandle;
        for(int i = fragments.size() - 1; i>=0; i--){
            Fragment child = fragments.get(i);

            if(isFragmentBackHandled(child)){
                Log.v("test", "count: "+ i+ "  " + child.toString());
                isHandle = true;
            }
        }
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
            isHandle = true;
        }
        return isHandle;
    }
    public static boolean handleBackPress(Fragment fragment) {
        return handleBackPress(fragment.getChildFragmentManager());
    }

    public static boolean handleBackPress(FragmentActivity fragmentActivity) {
        return handleBackPress(fragmentActivity.getSupportFragmentManager());
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 <b>true</b>
     */
    public static boolean isFragmentBackHandled(Fragment fragment) {
        return fragment != null
                && fragment.isVisible()
                && fragment.getUserVisibleHint() //for ViewPager
                && fragment instanceof FragmentBackHandler
                && ((FragmentBackHandler) fragment).onBackPressed();
    }
}
