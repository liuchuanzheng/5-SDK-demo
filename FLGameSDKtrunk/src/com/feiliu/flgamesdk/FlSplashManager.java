package com.feiliu.flgamesdk;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.feiliu.flgamesdk.listener.FLOnSplashFinishListenter;
import com.feiliu.flgamesdk.utils.AssetsUtil;
import com.feiliu.flgamesdk.utils.BitmapUtil;

/**
 * 飞流闪屏的管理类
 * @author liuchuanzheng，modify by programdolt.
 *
 */
public class FlSplashManager {
//	private  Dialog splashDialog;
	private  Bitmap mSplashBitmap;
	private  FLOnSplashFinishListenter mFinishListenter = null;
	private  Activity context;
	/**
	 * 展示飞流的闪屏界面 调用之后并不一定会闪屏,只会在放入闪屏图片后才能显示,不放入闪屏图片时 不会显示,也不会影响程序运行
	 * 此方法不能为static的.否则第二次闪屏时会报错
	 */
	public void showFLSplash(Activity context,String imageName, int splashDuration,
			FLOnSplashFinishListenter finishListenter) {
		mFinishListenter = finishListenter;
		this.context = context;
		if(imageName == null || imageName.trim().length()==0){
			mFinishListenter.doAction(this.context);
			return;
		}
		
		mSplashBitmap = AssetsUtil.getBitmapFromAssets(context,
				imageName);
		if (null == mSplashBitmap) {
			//GLogUtils.i("liuchuanzheng", "FlSplashManager类中未检测到闪屏图片");
			mFinishListenter.doAction(this.context);
			return;
		}
		
		View view = createView(context);
		context.setContentView(view);
		appendAnimation(view, splashDuration);

	}

	/**
	 * 创建dialog的布局
	 * 
	 * @param context
	 * @return
	 */
	private  View createView(Context context) {
		RelativeLayout layout = new RelativeLayout(context);
		mSplashBitmap = BitmapUtil.changeImage(mSplashBitmap,
				(Activity) context);// 使图片按等比里的方式延展
		Drawable mSplashDrawable = bitmap2Drawable(mSplashBitmap);
		// 设置图片为背景,当作闪屏
		layout.setBackgroundDrawable(mSplashDrawable);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(retlayoutParams);
		return layout;
	}

	/**
	 * bitmap转drawable
	 * 
	 * @param bitmap
	 *            bitmap对象
	 * @return drawable对象
	 */
	private  Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	private  void appendAnimation(View v, int splashDuration) {
		// 实际并没有渐变效果.直接显示图片3s
		AlphaAnimation ani = new AlphaAnimation(1.0f, 1.0f);
		ani.setRepeatMode(Animation.REVERSE);
		ani.setRepeatCount(0);
		if(splashDuration == 0) {splashDuration = 2000;}
		ani.setDuration(splashDuration);

		v.setAnimation(ani);
		ani.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
//				splashDialog.dismiss();
				if (mFinishListenter != null) {
					mFinishListenter.doAction(context);
				}
			}
		});
	}
}
