package com.scut.joe.unidesktop.container;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by joe on 17-7-8.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint testPaint;
    private float maxTextSize;
    private float minTextSize = 0f;

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){
        testPaint = new Paint();
        testPaint.set(this.getPaint());

        maxTextSize = this.getTextSize();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

    /**
     * Re size the font so the specified text fits in the text box
     * @param text
     * @param textWidth
     */
    private void refitText(String text, int textWidth){
        if(textWidth > 0){
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight();
            float trySize = maxTextSize;
            while ((trySize > minTextSize)
                    && (testPaint.measureText(text) > availableWidth)){
                trySize -= 1;
                if(trySize <= minTextSize){
                    trySize = minTextSize;
                    break;
                }
            }
        }
    }
}
