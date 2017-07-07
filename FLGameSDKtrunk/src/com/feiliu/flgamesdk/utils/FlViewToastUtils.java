package com.feiliu.flgamesdk.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.PictureNameConstant;

/**
 * @author 刘传政
 * 自定义飞流SDK主题风格的toast
 * <br>目前登录成功，注册成功，绑定成功 提示语会用到。
 *
 */
public class FlViewToastUtils {
	private static float scale;
	public static Context context;
	/**
	 * toast的底部布局已经封装好。不需要关心
	 * @param mcontext
	 * @param tips 要显示的文字
	 * @param duration 显示时间，单位秒
	 */
	public static void show(Context mcontext,String tips,int duration){
		context = mcontext;
		scale = UiSizeUtil.getScale(context);
		View view = createMyUi(tips);
		ExToast toast = new ExToast(context);
		//toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL |Gravity.FILL_HORIZONTAL, 0, 0);//这种写法可以使toast填充整个屏幕
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setDuration(duration);  
		toast.setView(view);  
		toast.show();  
	}
	public static View createMyUi(String tipsText) {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,(int)(100*scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		//......................................提示语.............................................................
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(50*scale);
		shape.setColor(Color.WHITE);
		TextView tips = new TextView(context);
		RelativeLayout.LayoutParams tipsParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,(int)(100*scale));
		tipsParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);  
		tips.setLayoutParams(tipsParams);
		Drawable logoIvBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ROUNDLOGO);
		tips.setBackgroundDrawable(shape);
		logoIvBg.setBounds(0, 0, (int)(100*scale), (int)(100*scale));
		tips.setCompoundDrawables(logoIvBg, null, null, null);
		tips.setText(tipsText);
		tips.setPadding((int)(0*scale), (int)(0*scale), (int)(50*scale), (int)(0*scale));
		tips.setTextColor(Color.BLACK);
		tips.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		tips.setSingleLine(true);
		tips.setGravity(Gravity.CENTER);//文字居中
		totalLayout.addView(tips);
		return totalLayout;
	}
}
