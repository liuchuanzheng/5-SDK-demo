package com.feiliu.flgamesdk.view.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 飞流球条形菜单window
 * 
 * @author 刘传政
 * 
 */
public class toolBarMenuPopWindow extends BasePopWindow implements
		OnDismissListener {
	private OnDismissListener lastPopOnDismissListener;
	private int x;
	private int y;
	private boolean isleft;

	public toolBarMenuPopWindow(Context context,int x,int y ,boolean isleft, OnDismissListener onDismissListener) {
		this.context = context;
		this.x = x;
		this.y = y;
		this.isleft = isleft;
		scale = UiSizeUtil.getScale(this.context);
		lastPopOnDismissListener = onDismissListener;
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面ui并设置其中的点击事件等
	 * 
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int) (800 * scale), (int) (160 * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		/*Drawable bg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.WINDOWBG);// 得到背景图片
		totalLayout.setBackgroundDrawable(bg);*/
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(80*scale);
		shape.setColor(Color.WHITE);
		totalLayout.setBackgroundDrawable(shape);
		//........................................logo...........................................................
		ImageView logoView = new ImageView(context);
		RelativeLayout.LayoutParams logoViewParams = new RelativeLayout.LayoutParams((int) (150 * scale), (int) (150* scale));
		logoViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		logoViewParams.topMargin = (int) (0 * scale);
		logoViewParams.leftMargin = (int) (0 * scale);
		logoView.setLayoutParams(logoViewParams);
		Drawable logoViewBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ROUNDLOGO);// 得到背景图片
		logoView.setBackgroundDrawable(logoViewBg);
		//..........................................账户..............................................................
		ImageView accountView = new ImageView(context);
		RelativeLayout.LayoutParams accountViewParams = new RelativeLayout.LayoutParams((int) (150 * scale), (int) (150* scale));
		accountViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		accountViewParams.topMargin = (int) (0 * scale);
		accountViewParams.leftMargin = (int) (160 * scale);
		accountView.setLayoutParams(accountViewParams);
		Drawable accountViewBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT);// 得到背景图片
		accountView.setBackgroundDrawable(accountViewBg);
		
		totalLayout.addView(logoView);
		totalLayout.addView(accountView);
		return totalLayout;
	}

	@Override
	public void onDismiss() {
		lastPopOnDismissListener.onDismiss();
	}

	@Override
	public void showWindow() {
		MyLogger.lczLog().i("展示：toolBarMenuPopWindow");
		new Runnable() {
			@Override
			public void run() {
				TextView tv;
				tv = new TextView(context);
				createPopWindow(tv);
			}
		}.run();
	}
	@Override
	public void createPopWindow(View parent) {
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(currentWindowView,
					(int) (800 * scale), (int) (160 * scale)); // 创建一个PopuWidow对象
		}
		mPopupWindow.setFocusable(true);// 使其聚集
		mPopupWindow.setOutsideTouchable(true);// 设置是否允许在外点击消失
		// mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//设置弹出窗体需要软键盘
		mPopupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 再设置模式，和Activity的一样，覆盖，调整大小。
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
		//mPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);// pop弹出动画 
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,x,y);
	}


}
