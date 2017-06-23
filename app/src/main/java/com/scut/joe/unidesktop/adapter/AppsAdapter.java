package com.scut.joe.unidesktop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scut.joe.unidesktop.model.AppItem;

import java.util.List;

/**
 * Created by joe on 17-6-16.
 */

public class AppsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    int i =0;

    private List<AppItem> mList;

    public AppsAdapter(Context mContext, List<AppItem> mList) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO 提高性能

        return null;
    }
}
