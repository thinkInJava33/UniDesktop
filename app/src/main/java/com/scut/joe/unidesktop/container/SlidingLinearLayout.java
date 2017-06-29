package com.scut.joe.unidesktop.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by joe on 17-6-29.
 */

public class SlidingLinearLayout extends LinearLayout {
    public SlidingLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return super.onInterceptTouchEvent(ev);
    }
}
