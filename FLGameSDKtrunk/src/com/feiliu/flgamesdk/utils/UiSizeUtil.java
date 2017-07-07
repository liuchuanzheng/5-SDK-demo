package com.feiliu.flgamesdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;
/**
 * 获取一个尺寸的比例值，便于屏幕适配
 * @author 刘传政
 *
 */
public class UiSizeUtil {
	/**
	 * 将屏幕窄边的宽度平均分成窄边像素分之一，这样利于调用者拿到一个比例数值，更换屏幕后还能保持此比例
	 * </br>由于开发时的设备是1080*1920，故方法中数值选择对应
	 * @param context
	 * @return
	 */
	public static float getScale(Context context){
		float scale;
		int src_w=UiSizeUtil.getScreenWidth(context);
		int src_h=UiSizeUtil.getScreenHeight(context);
		if (src_w<src_h) scale=src_w/1080f;
		else scale=src_h/1080f;
		return scale;
	}
	public static float getScaleY(Context context){
		float scale;
		int src_w=UiSizeUtil.getScreenWidth(context);
		int src_h=UiSizeUtil.getScreenHeight(context);
		if (src_w>src_h) scale=src_w/1920f;
		else scale=src_h/1920f;
		return scale;
	}
	
	

	public static int getScreenWidth(Context context) {   
		DisplayMetrics dm = new DisplayMetrics();   
        dm = context.getApplicationContext().getResources().getDisplayMetrics();   
        int width=dm.widthPixels;
        return width;
    }   
	public static int getScreenHeight(Context context) {   
		DisplayMetrics dm = new DisplayMetrics();   
        dm = context.getApplicationContext().getResources().getDisplayMetrics();   
        int height=dm.heightPixels;
        return height;
    }	
	
	
	
	
	  
	
}
