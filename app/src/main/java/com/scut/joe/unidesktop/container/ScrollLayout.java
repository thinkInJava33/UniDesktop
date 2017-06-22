package com.scut.joe.unidesktop.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by joe on 17-6-16.
 */

public class ScrollLayout extends ViewGroup {
    private Context mContext;

    public ScrollLayout(Context context, Context mContext) {
        super(context);
        this.mContext = mContext;
    }

    public ScrollLayout(Context context, AttributeSet attrs, Context mContext) {
        super(context, attrs);
        this.mContext = mContext;
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, Context mContext) {
        super(context, attrs, defStyleAttr);
        this.mContext = mContext;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
