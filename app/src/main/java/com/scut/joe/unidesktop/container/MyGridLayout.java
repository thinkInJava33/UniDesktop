package com.scut.joe.unidesktop.container;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.joe.unidesktop.R;

import java.util.zip.Inflater;

/**
 * Created by Idoit on 2017/6/19.
 */
public class MyGridLayout extends GridLayout {
    Rect[]  rects ;

    public MyGridLayout(Context context) {
        //super(context);
        this(context,null);
    }
    public MyGridLayout(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,-1);
    }
    public MyGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 设置 GridLayout 中的条目增加过渡动画***********************
        this.setLayoutTransition(new LayoutTransition());
    }



    public void addApp(String app_name, int  app_icon, LayoutInflater inflater) {
       // LayoutInflater inflater = LayoutInflater.from(context);
        View app = inflater.inflate(R.layout.item,null);
        TextView textView= (TextView)app.findViewById(R.id.app_name);
        textView.setText(app_name);
        ImageView appIcon = (ImageView)app.findViewById(R.id.app_icon);
        //appIcon.setImageDrawable(app_icon);
        appIcon.setImageResource(app_icon);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.setMargins(10, 10, 10, 10);

        // 设置 params 给 TextView, 使用 margins 效果生效
        app.setLayoutParams(params);// 将 textview 添加 Gridlayout 中展示

        app.setOnLongClickListener(longClickListener);
        // 将 textview 添加 Gridlayout 中展示
        app.setOnDragListener(dragListener);
        this.addView(app);
    }

    private OnLongClickListener longClickListener = new OnLongClickListener() {
        // 长按控件调用的方法
        // 参数：被长按的控件
        @Override
        public boolean onLongClick(View v) {
            // 设置控件开始拖拽
            // 参数 1 ：拖拽要显示的数据，一般 null
            // 参数 2 ：拖拽显示的阴影样式 ,DragShadowBuilder(v): 根据拖拽的控件，设置拖拽阴影的样式效果
            // 参数 3 ：拖拽的控件的状态，一般 null
            // 参数 4 ：拖拽的其他设置的标示，一般 0
            v.startDrag(null, new DragShadowBuilder(v), null, 0);
            // 返回 true 表示处理长按事件， false 就是不处理
            return true;
        }
    };

    /** 拖拽监听操作 **/
    private OnDragListener dragListener = new OnDragListener() {
        // 当拖拽操作执行的时候调用的方法
        // 参数 1 ：拖拽的控件
        // 参数 2 ：拖拽的事件
        @Override
        public boolean onDrag(View currentView, DragEvent event) {
            Log.i("test","Ondrag");
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:// 开始拖拽
                    System.out.println(" 开始拖拽 ");
                    createRect();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:// 开始拖拽控件后，进入拖拽控件范围事件
                    System.out.println(" 进入拖拽控件范围 ");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:// 开始拖拽控件后，离开拖拽控件范围事件
                    System.out.println(" 离开拖拽控件范围 ");
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:// 开始拖拽控件后，移动控件或者是拖拽控件执行的操作
                    System.out.println(" 拖拽控件移动 ");
                    // 根据拖拽的按下的坐标，获取拖拽控件移动的位置
                    int index = getIndex(event);
                    //MyGridLayout.this.getChildAt(index) != currentView :  判断移动到位置的 textview 和拖拽的 textview 是否一致，一致不进行移动，不一致进行移动操作
                    if (index != -1 && currentView != null && MyGridLayout.this.getChildAt(index) != currentView) {
                        // 实现 Gridlayout 的移动操作
                        // 将原来位置的拖拽的特性 tview 删除
                        MyGridLayout.this.removeView(currentView);
                        // 将拖拽的 textview 添加到移动的位置
                        MyGridLayout.this.addView(currentView, index);// 将 view 对象，添加到那个位置
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:// 结束拖拽
                    System.out.println(" 结束拖拽 ");
                    // 设置拖拽的 textview 背景改为黑色边框的背景
                    if (currentView != null) {
                        currentView.setEnabled(true);
                    }
                    break;
                case DragEvent.ACTION_DROP:// 结束拖拽，在控件范围内，松开手指，在控件范围外不执行操作
                    System.out.println(" 松开手指 ");
                    break;
            }
            return true;
        }
    };
    /**
     *  根据 textview 创建矩形
     *
     * 2016 年 12 月 30 日 上午 11:44:08
     */
    protected void createRect() {
        //getChildCount() :  获取 Gridlayout 的子控件的个数
          rects = new Rect[this.getChildCount()];
        // 根据孩子的个数，创建相应个数的矩形
        for (int i = 0; i < this.getChildCount(); i++) {
            // 根据子控件的索引，获取子控件的 view 对象
            View view = this.getChildAt(i);
            // 创建一个矩形
            // 参数 1,2 ：左上角的 x 和 y 的坐标
            // 参数 3,4 ：右下角的 x 和 y 的坐标
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            // 保存到就行到数组中
            rects[i] = rect;
        }
    }
    /**
     *  根据拖拽按下的坐标，获取拖拽控件移动的位置
     *
     * 2016 年 12 月 30 日 上午 11:50:20
     */
    protected int getIndex(DragEvent event) {
        for (int i = 0; i < rects.length; i++) {
            // 判断按下的坐标是否包含在矩形的坐标范围内容
            if (rects[i].contains((int)event.getX(), (int)event.getY())) {
                return i;
            }
        }
        return -1;
    }
}
