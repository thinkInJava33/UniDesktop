package com.scut.joe.unidesktop.adapter;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.container.DragGrid;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.dbManager;

public class DragAdapter extends BaseAdapter {
	/** TAG*/
	private final static String TAG = "DragAdapter";
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的position */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表） */
	public List<AppItem> channelList;
	/** TextView 频道内容 */
	class HolderView
	{
		private TextView item_text;
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
	
	public DragAdapter(Context context, List<AppItem> channelList,DragGrid grid) {
		this.context = context;
		this.channelList = channelList;
		this.grid = grid;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public AppItem getItem(int position) {
		// TODO Auto-generated method stub
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
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
			view = LayoutInflater.from(context).inflate(R.layout.item, parent,false);
			holderView.iv_icon = (ImageView) view.findViewById(R.id.app_icon);
			holderView.item_text = (TextView) view.findViewById(R.id.app_name);
			holderView.iv_delete = (ImageView) view.findViewById(R.id.delet_iv);

			LayoutParams mLayoutParams = holderView.iv_icon.getLayoutParams();
			mLayoutParams.width = (int) (Common.Width / 7);
			mLayoutParams.height = (int) (Common.Width / 7);
			holderView.iv_icon.setLayoutParams(mLayoutParams);

			view.setTag(holderView);
		}
		holderView = (HolderView)view.getTag();
		AppItem iconInfo = getItem(position);
		if(iconInfo.getIsEmpty() == 0) {
			Drawable drawable = iconInfo.getAppIcon();
			Bitmap srcBmp = ((BitmapDrawable) drawable).getBitmap();
			Bitmap destBmp = srcBmp.copy(srcBmp.getConfig(), true);
			Drawable darkDrawable = new BitmapDrawable(destBmp);
			darkDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
			StateListDrawable stateListDrawable = new StateListDrawable();
			stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, darkDrawable);
			stateListDrawable.addState(new int[]{}, drawable);
			holderView.iv_icon.setImageDrawable(stateListDrawable);
			notifyDataSetChanged();
			holderView.item_text.setText(iconInfo.getAppName());
		}else{
			holderView.item_text.setText("");
			holderView.iv_icon.setImageAlpha(0);
			notifyDataSetChanged();
		}
		holderView.iv_delete.setOnClickListener(new OnClickListener() {

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

//		if (position == getCount()-1){
//			if (convertView == null) {
//				convertView = view;
//			}
//			convertView.setEnabled(false);
//			convertView.setFocusable(false);
//			convertView.setClickable(false);
//			Log.i("test","htere");
//		}
		//		DragView channel = getItem(position);
		//		
		//		item_text.setText(channel.getName());
		if (isChanged && (position == holdPosition) && !isItemShow) {
			holderView.item_text.setText("");
			holderView.item_text.setSelected(true);
			holderView.item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + channelList.size())) {

			holderView.item_text.setText("");
			holderView.item_text.setSelected(true);
			holderView.item_text.setEnabled(true);
		}
		if(remove_position == position){
			deletInfo(position);
		}
		if (!isDelete || iconInfo.getIsEmpty() == 1) {
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
		channelList.remove(position);
		dbManager db = new dbManager(context);
		SharedPreferences modePreferences = context.getSharedPreferences("mode",Context.MODE_PRIVATE);
		int currentMode = modePreferences.getInt("choose", -1);
		db.hide(currentMode,id);
		hideposition = -1;
		notifyDataSetChanged();
	}


	/** 添加频道列表 */
	public void addItem(AppItem channel) {
		channelList.add(channel);
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
		if(dropItem.getIsEmpty() == 1){
			int tempIndex = dropItem.getIndex();
			dropItem.setIndex(dragItem.getIndex());
			dragItem.setIndex(tempIndex);
			channelList.remove(dropPostion);
			channelList.add(dropPostion,dragItem);
			channelList.remove(dragPostion);
			channelList.add(dragPostion,dropItem);
			db.updateApp(currentMode,dragItem);
			db.updateApp(currentMode, dropItem);
		}else {
			int dropIndex = getItem(dropPostion).getIndex();
			dragItem.setIndex(dropIndex);
			Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
			if (dragPostion < dropPostion) {
				channelList.add(dropPostion + 1, dragItem);
				channelList.remove(dragPostion);
			} else {
				channelList.add(dropPostion, dragItem);
				channelList.remove(dragPostion + 1);
			}
			if (moveForward) {
				for (int i = dropPostion + 1; i <= dragPostion; i++) {
					AppItem tempItem = getItem(i);
					tempItem.setIndex(tempItem.getIndex() + 1);
				}
				db.exchange(currentMode, dropPostion, dragPostion, channelList);
			} else {
				for (int i = dragPostion; i < dropPostion; i++) {
					AppItem tempItem = getItem(i);
					tempItem.setIndex(tempItem.getIndex() - 1);
				}
				db.exchange(currentMode, dragPostion, dropPostion, channelList);
			}
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