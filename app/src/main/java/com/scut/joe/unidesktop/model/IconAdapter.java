package com.scut.joe.unidesktop.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.dbManager;

import java.util.List;

/**
 * Created by joe on 17-6-26.
 */

public class IconAdapter extends BaseAdapter {
    /** TAG*/
    private final static String TAG = "IconAdapter";
    /** 是否显示底部的ITEM */
    private boolean isItemShow = false;
    private Context context;
    /** 控制的postion */
    private int holdPosition;
    /** 是否改变 */
    private boolean isChanged = false;
    /** 是否可见 */
    boolean isVisible = true;
    /** 可以拖动的列表（即用户选择的频道列表） */
    public List<AppItem> iconList;
    /** TextView 频道内容 */
    class HolderView
    {
        //private TextView item_text;
        private ImageView iv_icon;
        private ImageView iv_delete;
    }
    private boolean isDelete = false;
    /** 要删除的position */
    public int remove_position = -1;

    private Handler mHandler = new Handler();

    private DragGrid grid;

    /**
     * 指定隐藏的position
     */
    private int hideposition = -1;

    public IconAdapter(Context context, List<AppItem> iconList,DragGrid grid) {
        this.context = context;
        this.iconList = iconList;
        this.grid = grid;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return iconList == null ? 0 : iconList.size();
    }

    @Override
    public AppItem getItem(int position) {
        // TODO Auto-generated method stub
        if (iconList != null && iconList.size() != 0) {
            return iconList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        View view = null;
        if (view == null) {
            holderView = new HolderView();
            view = LayoutInflater.from(context).inflate(R.layout.icon_item, parent,false);
            holderView.iv_icon = (ImageView) view.findViewById(R.id.view_icon);
            //holderView.item_text = (TextView) view.findViewById(R.id.app_name);
            holderView.iv_delete = (ImageView) view.findViewById(R.id.delete_iv);

            ViewGroup.LayoutParams mLayoutParams = holderView.iv_icon.getLayoutParams();
            mLayoutParams.width = (int) (Common.Width / 2);
            mLayoutParams.height = (int) ((Common.Height-90) / 4);
            //holderView.iv_icon.setLayoutParams(mLayoutParams);

            view.setTag(holderView);
            view.setLayoutParams(mLayoutParams);
        }
        holderView = (HolderView)view.getTag();
        final AppItem iconInfo = getItem(position);
        holderView.iv_icon.setImageDrawable(iconInfo.getAppIcon());
        //holderView.item_text.setText(iconInfo.getAppName());
        holderView.iv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (!Common.isAnimaEnd) {
                            return;
                        }
                        notifyDataSetChanged();
                        grid.deleteInfo(position);
                    }
                });
            }
        });

        if (position == getCount()-1){
            if (convertView == null) {
                convertView = view;
            }
            convertView.setEnabled(false);
            convertView.setFocusable(false);
            convertView.setClickable(false);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            //holderView.item_text.setText("");
            //holderView.item_text.setSelected(true);
            //holderView.item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + iconList.size())) {

            //holderView.item_text.setText("");
            //holderView.item_text.setSelected(true);
            //holderView.item_text.setEnabled(true);
        }
        if(remove_position == position){
            deletInfo(position);
        }
        if (!isDelete) {
            holderView.iv_delete.setVisibility(View.GONE);
        }
        else
        {
            if (!iconInfo.getAppName().equals("更多")) {
                holderView.iv_delete.setVisibility(View.VISIBLE);
            }
        }
        if (hideposition == position) {
            view.setVisibility(View.INVISIBLE);
        }else {
            view.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void setisDelete(boolean isDelete)
    {
        this.isDelete = isDelete;
    }

    /**
     * 删除某个position
     * @param position
     */
    public void deletInfo(int position)
    {
        int id = getItem(position).getId();
        iconList.remove(position);
        dbManager db = new dbManager(context);
        SharedPreferences modePreferences = context.getSharedPreferences("mode",Context.MODE_PRIVATE);
        int currentMode = modePreferences.getInt("choose", -1);
        db.hide(currentMode,id);
        hideposition = -1;
        notifyDataSetChanged();
    }


    /** 添加频道列表 */
    public void addItem(AppItem channel) {
        iconList.add(channel);
        notifyDataSetChanged();
    }

    /** 拖动变更频道排序 */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        AppItem dragItem = getItem(dragPostion);
        AppItem dropItem = getItem(dropPostion);
        Log.i("test","dragPosiotion"+dragPostion);
        Log.i("test","dropPosiotion"+dropPostion);
        dbManager db = new dbManager(context);
        SharedPreferences modePreferences = context.getSharedPreferences("mode",Context.MODE_PRIVATE);
        int currentMode = modePreferences.getInt("choose", -1);
        boolean moveForward = false;
        //判断移动是否是往前
        if(dragItem.getIndex()> dropItem.getIndex()){
            moveForward = true;
        }

        int dropIndex = getItem(dropPostion).getIndex();
        dragItem.setIndex(dropIndex);

        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            iconList.add(dropPostion + 1, dragItem);
            iconList.remove(dragPostion);
        } else {
            iconList.add(dropPostion, dragItem);
            iconList.remove(dragPostion + 1);
        }
        if(moveForward) {
            for (int i = dropPostion + 1; i <= dragPostion; i++) {
                AppItem tempItem = getItem(i);
                tempItem.setIndex(tempItem.getIndex() + 1);
            }
            db.exchange(currentMode, dropPostion, dragPostion,iconList);
        }else{
            for (int i = dragPostion ; i < dropPostion; i++) {
                AppItem tempItem = getItem(i);
                tempItem.setIndex(tempItem.getIndex() - 1);
            }
            db.exchange(currentMode, dragPostion, dropPostion,iconList);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /** 设置删除的position */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /** 获取是否可见 */
    public boolean isVisible() {
        return isVisible;
    }

    /** 设置是否可见 */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
    /** 显示放下的ITEM */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }

    public void setHidePosition(int position) {
        // TODO Auto-generated method stub
        this.hideposition = position;
        notifyDataSetChanged();
    }
}