package com.feiliu.flgamesdk.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.ColorContant;
import com.feiliu.flgamesdk.global.FlToolBarControler;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 实名认证提示框window
 * 
 * @author 刘传政
 * 
 */
public class RealNameTipsPopWindow extends BasePopWindow implements
		OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private OnDismissListener lastPopOnDismissListener;

	public RealNameTipsPopWindow(Context context,
			FLOnLoginListener loginListener,OnDismissListener onDismissListener) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
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
				(int) (designWidth * scale), (int) (designHeight * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		Drawable bg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.WINDOWBG);// 得到背景图片
		totalLayout.setBackgroundDrawable(bg);
		// ..........................................title............................................................
		BaseBarView baseBarView = new BaseBarView(context, "实名认证", false, false,this);
		RelativeLayout.LayoutParams baseBarViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (100 * scale));
		baseBarViewParams.topMargin = (int) (0 * scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//........................................提示部分...........................................................
		TextView tipsView = new TextView(context);
		RelativeLayout.LayoutParams tipsViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (400 * scale));
		tipsViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		tipsViewParams.topMargin = (int) (185 * scale);
		tipsView.setLayoutParams(tipsViewParams);
		tipsView.setText("根据文化部要求，游戏用户需使用有效身份信息进行实名认证。");
		tipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		//changelessTipsView.setGravity(Gravity.CENTER);// 文字居中
		
		// ....................................立即认证......................................................
		TextView bindView = new TextView(context);
		RelativeLayout.LayoutParams bindViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (110 * scale));
		bindViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		bindViewParams.topMargin = (int) (426 * scale);
		bindView.setLayoutParams(bindViewParams);
		Drawable bindViewBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.LOGINORREGISTER);
		bindView.setBackgroundDrawable(bindViewBg);
		bindView.setText("立即认证");
		bindView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		bindView.setTextColor(Color.WHITE);
		bindView.setGravity(Gravity.CENTER);// 文字居中
		bindView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				new RealNamePopWindow(context, null, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						RealNameTipsPopWindow.this.showWindow();
					}
				},true,false);
			}
		});
		// ..................................以后再说按钮...................................................
		TextView laterView = new TextView(context);
		RelativeLayout.LayoutParams laterViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (110 * scale));
		laterViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		laterViewParams.topMargin = (int) (556 * scale);
		laterView.setLayoutParams(laterViewParams);
		Drawable laterViewBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.GUESTLOGINVIEWBG);
		laterView.setBackgroundDrawable(laterViewBg);
		laterView.setText("以后再说");
		laterView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		laterView.setTextColor(ColorContant.GRAY);
		laterView.setGravity(Gravity.CENTER);// 文字居中
		laterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				FlToolBarControler.show();//显示飞流球
			}

		});
		totalLayout.addView(baseBarView);
		totalLayout.addView(tipsView);
		totalLayout.addView(bindView);
		totalLayout.addView(laterView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		totalLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		totalLayout.setLayoutParams(totalLayoutParams);
		rootView.addView(totalLayout);
		return rootView;
	}

	@Override
	public void onDismiss() {

	}

	@Override
	public void showWindow() {
		
		MyLogger.lczLog().i("展示:"+this.getClass().getName());
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
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // 创建一个PopuWidow对象
		}
		mPopupWindow.setFocusable(true);// 使其聚集
		mPopupWindow.setOutsideTouchable(false);// 设置是否允许在外点击消失
		// mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//设置弹出窗体需要软键盘
		mPopupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 再设置模式，和Activity的一样，覆盖，调整大小。
		// mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//
		// 响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);// pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,0);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				MyLogger.lczLog().i(this.getClass().getName()+"dismiss");
				
			}
		});
	}

	@Override
	public void backbuttonclick() {
		mPopupWindow.dismiss();
		lastPopOnDismissListener.onDismiss();//通知上一个pop
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
	}

}
