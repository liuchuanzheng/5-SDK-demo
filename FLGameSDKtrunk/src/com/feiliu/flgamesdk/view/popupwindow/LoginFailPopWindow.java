package com.feiliu.flgamesdk.view.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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

import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 登录账号失败（密码错误）后弹出的账号安全提示界面
 * 
 * @author 刘传政
 * 
 */
public class LoginFailPopWindow extends BasePopWindow implements OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private OnDismissListener lastPopOnDismissListener;
	private String accountString;
	
	public LoginFailPopWindow(Context context, String userName, String password
			, FLOnLoginListener loginListener,OnDismissListener onDismissListener) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.accountString = userName;
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
		BaseBarView baseBarView = new BaseBarView(context, "账号安全提示", false, false,this);
		RelativeLayout.LayoutParams baseBarViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (100 * scale));
		baseBarViewParams.topMargin = (int) (0 * scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//........................................固定提示部分...........................................................
		TextView changelessTipsView = new TextView(context);
		RelativeLayout.LayoutParams changelessTipsViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (300 * scale));
		changelessTipsViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		changelessTipsViewParams.topMargin = (int) (170 * scale);
		changelessTipsView.setLayoutParams(changelessTipsViewParams);
		changelessTipsView.setText("登录失败！您的密码可能在其他设备上更改了，请重新登录。");
		changelessTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		
		// ....................................确定按钮......................................................
		TextView OKView = new TextView(context);
		RelativeLayout.LayoutParams bindViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (110 * scale));
		bindViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		bindViewParams.topMargin = (int) (470 * scale);
		OKView.setLayoutParams(bindViewParams);
		Drawable bindViewBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.LOGINORREGISTER);
		OKView.setBackgroundDrawable(bindViewBg);
		OKView.setText("确定");
		OKView.setTextColor(Color.WHITE);
		OKView.setGravity(Gravity.CENTER);// 文字居中
		OKView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//模拟一个集合。为了密码错误后回显账号到登录框
				List<AccountDBBean> accountDBBeans = new ArrayList<AccountDBBean>();
				AccountDBBean accountDBBean = new AccountDBBean();
				accountDBBean.setUserName(accountString);
				accountDBBean.setPassword("");
				accountDBBeans.add(accountDBBean);
				mPopupWindow.dismiss();
				new Login2PopWindow(context, accountDBBeans, loginListener, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						//这里不需要任何操作。因为此界面设计不能手动调出来
					}
				},"LoginFailPopWindow");
			}
		});
		
		totalLayout.addView(baseBarView);
		totalLayout.addView(changelessTipsView);
		totalLayout.addView(OKView);
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
		MyLogger.lczLog().i("展示：LoginFailPopWindow");
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
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
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
