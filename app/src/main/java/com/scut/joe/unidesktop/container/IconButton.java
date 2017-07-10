package com.scut.joe.unidesktop.container;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.scut.joe.unidesktop.R;

/**
 * Created by joe on 17-7-10.
 */

public class IconButton extends AppCompatButton {
    private int resourceId = 0;
    private Bitmap bitmap;
    private String text;

    public IconButton(Context context) {
        super(context, null);
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        resourceId = R.drawable.default_icon;
        bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        text = "默认";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (this.getMeasuredWidth() - bitmap.getWidth())/ 2;
        int y = this.getHeight()/5;
        canvas.drawBitmap(bitmap, x, y, null);
        canvas.translate(0, (this.getMeasuredHeight()/2) - (int)this.getTextSize());
        super.onDraw(canvas);
    }

    public void setIcon(Drawable icon){
        BitmapDrawable bd = (BitmapDrawable)icon;
        bitmap = bd.getBitmap();
        invalidate();
    }
}
