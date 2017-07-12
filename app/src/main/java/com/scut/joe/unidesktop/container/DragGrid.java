package com.scut.joe.unidesktop.container;


import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;
import com.scut.joe.unidesktop.apps.SearchActivity;
import com.scut.joe.unidesktop.adapter.DragAdapter;
import com.scut.joe.unidesktop.model.AppItem;
import com.scut.joe.unidesktop.util.Common;
import com.scut.joe.unidesktop.util.DataTools;

/**
 * 首页HomeFragment中的GridView
 * @author Administrator 
 *
 */
public class DragGrid extends GridView {
	/** 是否在拖动*/
	public boolean isDrag = false;
	/** 点击时候的X位置 */
	public int downX;
	/** 点击时候的Y位置 */
	public int downY;
	/** 点击时候对应整个界面的X位置 */
	public int windowX;
	/** 点击时候对应整个界面的Y位置 */
	public int windowY;
	/** 下划处理 */
	float mPosX = 0, mPosY = 0 , mCurPosX = 0, mCurPosY = 0;
	/** 屏幕上的X */
	private int win_view_x;
	/** 屏幕上的Y*/
	private int win_view_y;
	/** 拖动的里x的距离  */
	int dragOffsetX;
	/** 拖动的里Y的距离  */
	int dragOffsetY;
	/** 长按时候对应postion */
	public int dragPosition;
	/** Up后对应的ITEM的Position */
	private int dropPosition;
	/** 开始拖动的ITEM的Position*/
	private int startPosition;
	/** item高 */
	private int itemHeight;
	/** item宽 */
	private int itemWidth;
	/** 拖动的时候对应ITEM的VIEW */
	private View dragImageView = null;
	/** 长按的时候ITEM的VIEW*/
	private ViewGroup dragItemView = null;
	/** WindowManager管理器 */
	private WindowManager windowManager = null;
	/** */
	private WindowManager.LayoutParams windowParams = null;
	/** item总量*/
	private int itemTotalCount;
	/** 一行的ITEM数量*/
	private int nColumns = 3;
	/** 行数 */
	private int nRows;
	/** 剩余部分 */
	private int Remainder;
	/** 是否在移动 */
	private boolean isMoving = false;
	/** 之前是否在移动 */
	private boolean wasMoving = false;
	/** */
	private int holdPosition;
	/** 拖动的时候放大的倍数 */
	private double dragScale = 1.2D;
	/** 震动器  */
	private Vibrator mVibrator;
	/** 每个ITEM之间的水平间距 */
	private int mHorizontalSpacing = 15;
	/** 每个ITEM之间的竖直间距 */
	private int mVerticalSpacing = 15;
	/* 移动时候最后个动画的ID */
	private String LastAnimationID;
	/**
	 * 执行动画的布局
	 */
	private RelativeLayout rootLayout;

	final String settings[] = {"更换壁纸", "更改模式", "系统设置","桌面排列"};

	int currentRow;
	int currentCol;

	private Context mContext;
	public DragGrid(Context context) {
		super(context);
		init(context);
	}

	public DragGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DragGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context){
		this.mContext = context;
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		//将布局文件中设置的间距dip转为px
		mHorizontalSpacing = DataTools.dip2px(context, mHorizontalSpacing);
	}

	/**
	 * 获取状态栏的高度
	 * @return
	 */
	private int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 判断是否点击在指定view中
	 * @param view
	 * @param ev
	 * @return
	 */
	private boolean inRangeOfView(View view, MotionEvent ev){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int downX = (int)ev.getX();
		int downY = (int)ev.getY();
		int x = location[0];
		int y = location[1] - getStatusBarHeight();
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();

		if(downX < x || downX > (x + viewWidth) || downY < y || downY > (y + viewHeight)){
			return false;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			//当前手指所点击的position
			int position = pointToPosition(downX, downY);
			if (position!=AdapterView.INVALID_POSITION) {
				//获取当前点击的view
				View view = getChildAt(position - getFirstVisiblePosition());
				//得到该item中的删除按钮
				ImageView iView = (ImageView)view.findViewById(R.id.delet_iv);

				if (isDrag) {
					if (!inRangeOfView(iView, ev)) {
						int x = (int) ev.getX();// 长安事件的X位置
						int y = (int) ev.getY();// 长安事件的y位置
						isDrag = true;
						startPosition = pointToPosition(x, y);// 第一次点击的postion
						dragPosition = pointToPosition(x, y);
						if (dragPosition != AdapterView.INVALID_POSITION) {

							//						if (startPosition == getCount() - 1) {
							//							return false;
							//						}
							ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
							TextView dragTextView = (TextView)dragViewGroup.findViewById(R.id.app_name);
							dragTextView.setSelected(true);
							dragTextView.setEnabled(false);
							itemHeight = dragViewGroup.getHeight();
							itemWidth = dragViewGroup.getWidth();
							itemTotalCount = DragGrid.this.getCount();
							int row = itemTotalCount / nColumns;// 算出行数
							Remainder = (itemTotalCount % nColumns);// 算出最后一行多余的数量
							if (Remainder != 0) {
								nRows = row + 1;
							} else {
								nRows = row;
							}
							// 如果特殊的这个不等于拖动的那个,并且不等于-1
							if (dragPosition != AdapterView.INVALID_POSITION) {
								// 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
								win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
								win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
								dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
								dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
								dragItemView = dragViewGroup;
								dragViewGroup.destroyDrawingCache();
								dragViewGroup.setDrawingCacheEnabled(true);
								Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
								//							mVibrator.vibrate(50);//设置震动时间
								startDrag(dragBitmap, (int)ev.getRawX(),  (int)ev.getRawY());
								hideDropItem();
								dragViewGroup.setVisibility(View.INVISIBLE);
								isMoving = false;
								wasMoving = false;
								requestDisallowInterceptTouchEvent(true);
								return true;
							}
						}
					}
					else {
						Log.i("DragGrid---->>>>>>", "已点击");
					}
				}
				else {
					setOnClickListener(ev);
				}
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 删除动画
	 * @param position
	 */
	public void deleteInfo(int position)
	{
		DeleteAnimation(position);
	}


	public void setRelativeLayout(RelativeLayout layout)
	{
		this.rootLayout = layout;
	}

	/**
	 * 删除View动画
	 * @param position
	 */
	public void DeleteAnimation(final int position)
	{

		final View view = getChildAt(position);
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.destroyDrawingCache();

		final ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(bitmap);
		imageView.setLayoutParams(new RelativeLayout.LayoutParams((int) (Common.Width / 7),
				(int) (Common.Width / 7)));

		RelativeLayout.LayoutParams ivlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		
		final int aimPosit = getCount() - 1;
		
		rootLayout.addView(imageView, ivlp);
		AnimatorSet animatorSet = createTranslationAnim(aimPosit, view, imageView);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setDuration(500);
		animatorSet.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				DragAdapter adapter = (DragAdapter)getAdapter();
				adapter.setHidePosition(position);
				Common.isAnimaEnd = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				imageView.setVisibility(View.GONE);
				imageView.clearAnimation();
				rootLayout.removeView(imageView);
				DragAdapter adapter = (DragAdapter)getAdapter();
				adapter.deletInfo(position);
				final ViewTreeObserver vto = getViewTreeObserver();
				vto.addOnPreDrawListener(new OnPreDrawListener() {
					
					@Override
					public boolean onPreDraw() {
						// TODO Auto-generated method stub
						vto.removeOnPreDrawListener(this);
						animateReorder(position, aimPosit);
						return false;
					}
				});
			}
		});
		animatorSet.start();
	}

	/**
	 * 平移动画
	 * @param view 需要移动的view
	 * @param startX 开始的X坐标
	 * @param endX 结束的X坐标
	 * @param startY 开始的Y坐标
	 * @param endY 结束的Y坐标
	 * @return
	 */
	private AnimatorSet createAnimator(View view, float startX,
			float endX, float startY, float endY) {
		ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
				startX, endX);
		ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
				startY, endY);
		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playTogether(animX, animY);
		return animSetXY;
	}
	/**
	 * 互相交换的item
	 * @param deletePosition
	 * @param itemCount
	 */
	private void animateReorder(int deletePosition, int itemCount)
	{
		boolean isForward = itemCount > deletePosition;
		List<Animator> list = new ArrayList<Animator>();
		if (isForward) {
			
			for (int pos = deletePosition; pos < itemCount; pos++) {
				View view = getChildAt(pos - getFirstVisiblePosition());
				if ((pos + 1) % nColumns == 0) {
					list.add(createAnimator(view, -view.getWidth() * (nColumns - 1), 0, view.getHeight(), 0));
				}
				else {
					list.add(createAnimator(view, view.getWidth(), 0, 0, 0));
				}
			}
			
		}else {
			
			for (int pos = deletePosition; pos > itemCount; pos--) {
				View view = getChildAt(pos - getFirstVisiblePosition());
				if ((pos + nColumns) % nColumns == 0) {
					list.add(createAnimator(view,view.getWidth() * (nColumns - 1), 0, -view.getHeight(), 0));
				} else {
					list.add(createAnimator(view, -view.getWidth(), 0, 0, 0));
				}
			}
		}
		AnimatorSet set = new AnimatorSet();
		set.playTogether(list);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setDuration(300);
		set.addListener(new AnimatorListenerAdapter() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				Common.isAnimaEnd = true;
			}
			
		});
		set.start();
	}
	
	/**
	 * 
	 * 方法: createTranslationAnim
	 * <p>
	 * 描述: TODO
	 * <p>
	 * 参数: @param position 参数: @param aimPosit 参数: @param view 参数: @param
	 * animView 参数: @return
	 * <p>
	 * 返回: AnimatorSet
	 * <p>
	 * 异常
	 * <p>
	 * 作者: wedcel wedcel@gmail.com
	 * <p>
	 * 时间: 2015年8月25日 下午4:49:23
	 */
	private AnimatorSet createTranslationAnim(int aimPosit,	View view, ImageView animView) {
		int startx = view.getLeft();
		int starty = view.getTop();
		View aimView = getChildAt(aimPosit);
		int endx = aimView.getLeft();
		int endy = aimView.getTop();

		ObjectAnimator animX = ObjectAnimator.ofFloat(animView, "translationX",
				startx, endx);
		ObjectAnimator animY = ObjectAnimator.ofFloat(animView, "translationY",
				starty, endy);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(animView, "scaleX", 1f,
				0.5f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(animView, "scaleY", 1f,
				0.5f);
		ObjectAnimator alpaAnim = ObjectAnimator.ofFloat(animView, "alpha", 1f,
				0.0f);

		AnimatorSet animSetXY = new AnimatorSet();
		animSetXY.playTogether(animX,animY,scaleX, scaleY, alpaAnim);
		return animSetXY;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPosX = ev.getX();
				mPosY = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				mCurPosX = ev.getX();
				mCurPosY = ev.getY();
				break;
			case MotionEvent.ACTION_UP:
				if (mCurPosY - mPosY > 0
						&& (Math.abs(mCurPosY - mPosY) > 25)
						&& (Math.abs(mCurPosX - mPosX) < 200)) {
					//向下滑動
					if(!wasMoving) {
						Intent searchIntent = new Intent(mContext, SearchActivity.class);
						mContext.startActivity(searchIntent);
					}
				} else if (mCurPosY - mPosY < 0
						&& (Math.abs(mCurPosY - mPosY) > 25)) {
					//向上滑动

				}
				wasMoving = false;
				break;
		}

		if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
			// 移动时候的对应x,y位置
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();
				windowX = (int) ev.getX();
				downY = (int) ev.getY();
				windowY = (int) ev.getY();
				Log.i("test","grid down");
				break;
			case MotionEvent.ACTION_MOVE:
				onDrag(x, y ,(int) ev.getRawX() , (int) ev.getRawY());
				if (!isMoving){
					OnMove(x, y);
				}
				if (pointToPosition(x, y) != AdapterView.INVALID_POSITION){
					break;
				}
				Log.i("test","grid move");
				break;
			case MotionEvent.ACTION_UP:
				stopDrag();
				onDrop(x, y);
				Log.i("test","grid up");
				requestDisallowInterceptTouchEvent(false);
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	public void refresh()
	{
		stopDrag();
		isDrag = false;
		DragAdapter mDragAdapter = (DragAdapter) getAdapter();
		mDragAdapter.setisDelete(false);
		mDragAdapter.notifyDataSetChanged();
	}

	/** 在拖动的情况 */
	private void onDrag(int x, int y , int rawx , int rawy) {
		if (dragImageView != null) {
			windowParams.alpha = 0.6f;
			//			windowParams.x = rawx - itemWidth / 2;
			//			windowParams.y = rawy - itemHeight / 2;
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}

	/** 在松手下放的情况 */
	private void onDrop(int x, int y) {
		// 根据拖动到的x,y坐标获取拖动位置下方的ITEM对应的POSTION
		int tempPostion = pointToPosition(x, y);
		//		if (tempPostion != AdapterView.INVALID_POSITION) {
		dropPosition = tempPostion;
		DragAdapter mDragAdapter = (DragAdapter) getAdapter();
		//显示刚拖动的ITEM
		mDragAdapter.setShowDropItem(true);
		//刷新适配器，让对应的ITEM显示
		mDragAdapter.notifyDataSetChanged();
		//		}
	}
	/**
	 * 长按点击监听
	 * @param ev
	 */
	public void setOnClickListener(final MotionEvent ev) {
		setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
										   int position, long id) {
				AppItem dragView = (AppItem)parent.getItemAtPosition(position);
				if(dragView.getIsEmpty() == 1){
					int currentMode = mContext.getSharedPreferences("mode",Context.MODE_PRIVATE).getInt("choose",0);
					if(currentMode == 2){
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setTitle("Setting Dialog");
						builder.setItems(settings, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which){
									case 0:
										Log.i("test","壁纸");
										break;
									case 1:
										Log.i("test","模式");
										Intent i = new Intent();
										ComponentName componentName = new ComponentName("com.scut.joe.unidesktop",
												"com.scut.joe.unidesktop.apps.TransferActivity");
										i.setComponent(componentName);
										mContext.startActivity(i);
										break;
									case 2:
										Log.i("test","系统设置");
										Intent intent =  new Intent(Settings.ACTION_SETTINGS);
										mContext.startActivity(intent);
										break;
									case 3:
										Log.i("test","排列");
										AlertDialog.Builder arrangeBuilder = new AlertDialog.Builder(mContext);
										arrangeBuilder.setTitle("桌面排列");
										View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_arrange,null);
										final TextView tv1 = (TextView) v.findViewById(R.id.id_tv1);
										final TextView tv2 = (TextView) v.findViewById(R.id.id_tv2);
										final SharedPreferences mode2Info = mContext.getSharedPreferences("mode2Info",Context.MODE_PRIVATE);
										currentCol = mode2Info.getInt("page_col",4);
										currentRow = mode2Info.getInt("page_row",4);
										String arrange = currentRow + " X " + currentCol;
										tv2.setText("当前布局:" + arrange);
										SeekBar rowBar =(SeekBar) v.findViewById(R.id.id_rowBar);
										rowBar.setProgress(currentRow - 3);
										rowBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
											@Override
											public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
												progress += 3;
												currentRow = progress;
												String arrange = currentRow + " X " + currentCol;
												tv1.setText("正在拖动");
												tv2.setText("当前布局:" + arrange);
											}

											@Override
											public void onStartTrackingTouch(SeekBar seekBar) {
												tv1.setText("开始拖动");
											}

											@Override
											public void onStopTrackingTouch(SeekBar seekBar) {
												tv1.setText("停止拖动");
											}
										});
										SeekBar colBar =(SeekBar) v.findViewById(R.id.id_colBar);
										colBar.setProgress(currentCol - 3);
										colBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
											@Override
											public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
												progress += 3;
												currentCol = progress;
												String arrange = currentRow + " X " + currentCol;
												tv1.setText("正在拖动");
												tv2.setText("当前布局:" + arrange);
											}

											@Override
											public void onStartTrackingTouch(SeekBar seekBar) {
												tv1.setText("开始拖动");
											}

											@Override
											public void onStopTrackingTouch(SeekBar seekBar) {
												tv1.setText("停止拖动");
											}
										});

										arrangeBuilder.setView(v);
										arrangeBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												SharedPreferences.Editor confirm = mode2Info.edit();
												confirm.putInt("page_row",currentRow);
												confirm.putInt("page_col",currentCol);
												confirm.commit();
												setNumColumns(currentCol);
											}
										});
										arrangeBuilder.show();

										break;
								}
							}
						});
						builder.show();
					}

					return true;
				}
				int x = (int) ev.getX();// 长安事件的X位置
				int y = (int) ev.getY();// 长安事件的y位置
				isDrag = true;
				DragAdapter mDragAdapter = (DragAdapter) getAdapter();
				mDragAdapter.setisDelete(true);
				mDragAdapter.notifyDataSetChanged();
				startPosition = position;// 第一次点击的postion
				dragPosition = position;
				//如果是最后一个更多，则不能进行长按
				//				if (startPosition == getCount() - 1) {
				//					return false;
				//				}
				ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
				TextView dragTextView = (TextView)dragViewGroup.findViewById(R.id.app_name);


				dragTextView.setSelected(true);
				dragTextView.setEnabled(false);
				itemHeight = dragViewGroup.getHeight();
				itemWidth = dragViewGroup.getWidth();
				itemTotalCount = DragGrid.this.getCount();
				int row = itemTotalCount / nColumns;// 算出行数
				Remainder = (itemTotalCount % nColumns);// 算出最后一行多余的数量
				if (Remainder != 0) {
					nRows = row + 1;
				} else {
					nRows = row;
				}
				// 如果特殊的这个不等于拖动的那个,并且不等于-1
				if (dragPosition != AdapterView.INVALID_POSITION) {
					// 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
					win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
					win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
					dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
					dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
					dragItemView = dragViewGroup;
					dragViewGroup.destroyDrawingCache();
					dragViewGroup.setDrawingCacheEnabled(true);
					Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
					mVibrator.vibrate(50);//设置震动时间
					startDrag(dragBitmap, (int)ev.getRawX(),  (int)ev.getRawY());
					hideDropItem();
					dragViewGroup.setVisibility(View.INVISIBLE);
					isMoving = false;
					wasMoving = false;
					requestDisallowInterceptTouchEvent(true);
					return true;
				}
				return false;
			}
		});
	}

	public void startDrag(Bitmap dragBitmap, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();// 获取WINDOW界面的
		//Gravity.TOP|Gravity.LEFT;这个必须加  
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		//		windowParams.x = x - (int)((itemWidth / 2) * dragScale);
		//		windowParams.y = y - (int) ((itemHeight / 2) * dragScale);
		//得到preview左上角相对于屏幕的坐标   
		windowParams.x = x - win_view_x;
		windowParams.y = y  - win_view_y; 
		//		this.windowParams.x = (x - this.win_view_x + this.viewX);//位置的x值
		//		this.windowParams.y = (y - this.win_view_y + this.viewY);//位置的y值
		//设置拖拽item的宽和高  
		windowParams.width = (int) (dragScale * dragBitmap.getWidth());// 放大dragScale倍，可以设置拖动后的倍数
		windowParams.height = (int) (dragScale * dragBitmap.getHeight());// 放大dragScale倍，可以设置拖动后的倍数
		this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE                           
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE                           
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON                           
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		this.windowParams.format = PixelFormat.TRANSLUCENT;
		this.windowParams.windowAnimations = 0;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	/** 停止拖动 ，释放并初始化 */
	private void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	/** 在ScrollView内，所以要进行计算高度 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	/** 隐藏 放下 的ITEM*/
	private void hideDropItem() {
		((DragAdapter) getAdapter()).setShowDropItem(false);
	}

	/** 获取移动动画 */
	public Animation getMoveAnimation(float toXValue, float toYValue) {
		TranslateAnimation mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF,toXValue, 
				Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, toYValue);// 当前位置移动到指定位置
		mTranslateAnimation.setFillAfter(true);// 设置一个动画效果执行完毕后，View对象保留在终止的位置。
		mTranslateAnimation.setDuration(300L);
		return mTranslateAnimation;
	}

	/** 移动的时候触发*/
	public void OnMove(int x, int y) {
		// 拖动的VIEW下方的POSTION
		int dPosition = pointToPosition(x, y);
		// 判断下方的POSTION是否是最开始2个不能拖动的
		if (dPosition <= getCount() - 1) {
			if ((dPosition == -1) || (dPosition == dragPosition)){
				return;
			}
			dropPosition = dPosition;
			if (dragPosition != startPosition){
				dragPosition = startPosition;
			}
			int movecount;
			//拖动的=开始拖的，并且 拖动的 不等于放下的
			if ((dragPosition == startPosition) || (dragPosition != dropPosition)){
				//移需要移动的动ITEM数量
				movecount = dropPosition - dragPosition;
			}else{
				//移需要移动的动ITEM数量为0
				movecount = 0;
			}
			if(movecount == 0){
				return;
			}

			int movecount_abs = Math.abs(movecount);

			if (dPosition != dragPosition) {
				//dragGroup设置为不可见
				ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
				dragGroup.setVisibility(View.INVISIBLE);
				float to_x = 1;// 当前下方positon
				float to_y;// 当前下方右边positon
				//x_vlaue移动的距离百分比（相对于自己长度的百分比）
				float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
				//y_vlaue移动的距离百分比（相对于自己宽度的百分比）
				float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
				Log.d("x_vlaue", "x_vlaue = " + x_vlaue);
				for (int i = 0; i < movecount_abs; i++) {
					to_x = x_vlaue;
					to_y = y_vlaue;
					//像左
					if (movecount > 0) {
						// 判断是不是同一行的
						holdPosition = dragPosition + i + 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = - x_vlaue;
							to_y = 0;
						} else if (holdPosition % 3 == 0) {
							to_x = 2 * x_vlaue;
							to_y = - y_vlaue;
						} else {
							to_x = - x_vlaue;
							to_y = 0;
						}
					}else{
						//向右,下移到上，右移到左
						holdPosition = dragPosition - i - 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = x_vlaue;
							to_y = 0;
						} else if((holdPosition + 1) % 3 == 0){
							to_x = -2 * x_vlaue;
							to_y = y_vlaue;
						}else{
							to_x = x_vlaue;
							to_y = 0;
						}
					}
					ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);
					Animation moveAnimation = getMoveAnimation(to_x, to_y);
					moveViewGroup.startAnimation(moveAnimation);
					//如果是最后一个移动的，那么设置他的最后个动画ID为LastAnimationID
					if (holdPosition == dropPosition) {
						LastAnimationID = moveAnimation.toString();
					}
					moveAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							isMoving = true;
							wasMoving = true;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							// 如果为最后个动画结束，那执行下面的方法
							if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
								DragAdapter mDragAdapter = (DragAdapter) getAdapter();
								mDragAdapter.exchange(startPosition,dropPosition);
								startPosition = dropPosition;
								dragPosition = dropPosition;
								isMoving = false;
							}
						}
					});
				}
			}
		}
	}
}