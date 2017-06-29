package com.scut.joe.unidesktop.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.joe.unidesktop.util.Common;

/**
 * Created by joe on 17-6-28.
 */

public class CommonViewHolder {
    //所有控件的集合
    private SparseArray<View> mViews;
    //记录位置
    private int mPosition;
    //复用的View
    private View mConvertView;

    /**
     *
     * @param context 上下文对象
     * @param parent 父类容器
     * @param layoutId 布局的ID
     * @param position item的位置
     */
    public CommonViewHolder(Context context, ViewGroup parent, int layoutId,
                            int position){
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        //构造方法中指定布局
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context context, View convertView, ViewGroup parent,
                                       int layoutId, int position){
        //如果为空，直接新建一个ViewHolder
        if(convertView == null){
            return new CommonViewHolder(context, parent, layoutId, position);
        }else{
            //否则返回一个已经存在的ViewHolder
            CommonViewHolder viewHolder = (CommonViewHolder) convertView.getTag();
            //更新条目位置
            viewHolder.mPosition = position;
            return viewHolder;
        }
    }

    public View getConvertView(){
        return mConvertView;
    }

    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置文本
     * @param viewId
     * @param text
     * @return
     */
    public CommonViewHolder setText(int viewId, String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图像
     * @param viewId
     * @param image
     * @return
     */
    public CommonViewHolder setImage(int viewId, Drawable image){
        ImageView iv = getView(viewId);
        iv.setImageDrawable(image);
        return this;
    }
}
