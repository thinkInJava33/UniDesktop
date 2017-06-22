package com.scut.joe.unidesktop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scut.joe.unidesktop.model.IconItem;

import java.util.List;

/**
 * Created by joe on 17-6-16.
 */

public class IconsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    private List<IconItem> mList;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
