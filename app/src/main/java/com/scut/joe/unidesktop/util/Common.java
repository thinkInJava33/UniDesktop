package com.scut.joe.unidesktop.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.view.View;

public class Common {

	public static final int MORE = 99999;
	
	public static int Width;
	public static int Height;
	
	/**
	 * 是否显示删除按钮
	 */
	public static boolean isDrag = false;
	/**
	 * 动画是否结束
	 */
	public static boolean isAnimaEnd = true;

	public static void changeAppBackground(Bitmap bitmap, final View view){
		Palette.Builder builder = Palette.from(bitmap);
		builder.maximumColorCount(24);
		AsyncTask<Bitmap, Void, Palette>generate = builder.generate(new Palette.PaletteAsyncListener() {
			@Override
			public void onGenerated(Palette palette) {
				//异步加载必须处理Swatch为null的情况
				Palette.Swatch vibrant = palette.getVibrantSwatch();
				if(vibrant != null){
					view.setBackgroundColor(vibrant.getRgb());
				}else{
					vibrant = palette.getLightVibrantSwatch();
					if(vibrant != null){
						view.setBackgroundColor(vibrant.getRgb());
					}else{
						vibrant = palette.getDarkVibrantSwatch();
						if(vibrant != null){
							view.setBackgroundColor(vibrant.getRgb());
						}else{
							vibrant = palette.getDominantSwatch();
							if(vibrant != null){
								view.setBackgroundColor(vibrant.getRgb());
							}
						}
					}
				}
			}
		});
	}
}
