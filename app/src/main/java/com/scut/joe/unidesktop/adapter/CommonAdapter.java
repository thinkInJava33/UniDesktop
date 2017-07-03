package com.scut.joe.unidesktop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scut.joe.unidesktop.model.CommonViewHolder;

import java.util.List;

/**
 * Created by joe on 17-6-30.
 */

/**
 * 通用Adapter抽象类
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> list;
    private int layoutId;

    public CommonAdapter(Context context, List<T> list, int layoutId) {
        this.mContext = context;
        this.list = list;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.get(mContext, convertView, parent, layoutId, position);
        setViewContent(viewHolder, (T)getItem(position));
        return viewHolder.getConvertView();
    }

    /**
     * 提供抽象方法，来设置控件内容
     * @param viewHolder
     * @param t
     */
    public abstract void setViewContent(CommonViewHolder viewHolder, T t);
}
