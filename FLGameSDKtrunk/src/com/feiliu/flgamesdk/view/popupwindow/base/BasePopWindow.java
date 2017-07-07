package com.feiliu.flgamesdk.view.popupwindow.base;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.feiliu.flgamesdk.utils.UiSizeUtil;

public abstract class BasePopWindow {
	
	public Context context;
	public float scale;//尺寸百分比
	public View currentWindowView;//popuWindow的根布局view
	public PopupWindow mPopupWindow;
	//图片的原宽高是710*560   此处基本等比例放大。保证图案形状不走形
	public int designWidth = 884;//相当于是1080分之890    
	public int designHeight = 697;
	/**
	 * 创建popupwindow的View
	 * @return
	 */
	public abstract View createMyUi();
	/**
	 * 展示popwindow
	 */
	public  abstract void showWindow();
	/**
	 * 创建popwindow对象，进行一些设置
	 */
	public abstract void createPopWindow(View parent);
	/**
	   * 创建一个透明的view，防止触摸popwindow边缘，pop消失
	   * @return
	   */
	public RelativeLayout creatRootTranslateView() {
		RelativeLayout rootTranslateView = new RelativeLayout(context);
		RelativeLayout.LayoutParams rootTranslateViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		rootTranslateView.setLayoutParams(rootTranslateViewParams);
		rootTranslateView.setBackgroundColor(Color.TRANSPARENT);
		return rootTranslateView;
			
		}
}
