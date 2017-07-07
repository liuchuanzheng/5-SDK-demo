package com.feiliu.flgamesdk.view.activity.toolbar;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * @author 刘传政
 * 供飞流球菜单显示时使用的全屏透明窗口。避免点击飞流球菜单外部飞流球不关闭。
 *
 */
public class TranslatePopWindow extends BasePopWindow implements  OnDismissListener{
	private OnDismissListener lastPopOnDismissListener;//上一个弹出框传过来的。为了配合返回键的功能
	public TranslatePopWindow(Context context, OnDismissListener onDismissListener) {
		this.context = context;
		this.lastPopOnDismissListener = onDismissListener;
		scale = UiSizeUtil.getScale(this.context);
		currentWindowView = createMyUi();
		showWindow();
	}

	public View createMyUi() {
		final RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				(int) (150 * scale), (int) (150 * scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		retAbsoluteLayout.setBackgroundColor(Color.TRANSPARENT);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				if (lastPopOnDismissListener != null) {
					lastPopOnDismissListener.onDismiss();
				}
			}
		});
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}



	public void showWindow() {
		new Runnable() {
			@Override
			public void run() {
				TextView tv;
				tv = new TextView(context);
				createPopWindow(tv);
			}
		}.run();
	}
	/**
	 * 创建出popupwindow对象，并设置相应参数
	 * @param parent
	 */
	public void createPopWindow(View parent) {
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(currentWindowView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // 创建一个PopuWidow对象
		}
		mPopupWindow.setFocusable(true);// 使其聚集
		mPopupWindow.setOutsideTouchable(false);// 设置是否允许在外点击消失
		// mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//设置弹出窗体需要软键盘
		mPopupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 再设置模式，和Activity的一样，覆盖，调整大小。
		// mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//
		// 响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);//pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
	}

	@Override
	public void onDismiss() {
		
	}

	
}
