package com.feiliu.flgamesdk.view.popupwindow;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.ColorContant;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.GuestLoginRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 带有游客登录按钮的登录一级界面
 * @author 刘传政
 *
 */
public class LoginPopWindow extends BasePopWindow implements  OnDismissListener {
	private FLOnLoginListener loginListener;
	private List<AccountDBBean> accountDBBeans = null;//从数据库查询出来的账号集合
	

	public LoginPopWindow(Context context, String userName,
			String password,
			FLOnLoginListener loginListener) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面ui并设置其中的点击事件等
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int) (designWidth * scale), (int) (designHeight * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		Drawable bg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TOTALLAYOUTBG);//得到背景图片
		totalLayout.setBackgroundDrawable(bg);
		//..............................关闭按钮.................cp可配...........................................
		View closeView = new View(context);//关闭按钮
		RelativeLayout.LayoutParams viewCloseParams=new RelativeLayout.LayoutParams((int)(80*scale),(int)(70*scale));
		viewCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE); //右对齐 
		viewCloseParams.topMargin=(int) (0*scale);
		viewCloseParams.rightMargin = (int) (10*scale);
		closeView.setLayoutParams(viewCloseParams);
		Drawable closeViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.CLOSEVIEW);//关闭按钮图片
		closeView.setBackgroundDrawable(closeViewBg);
		closeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyLogger.lczLog().i("点击关闭按钮");
				mPopupWindow.dismiss();
				loginListener.onLoginCancel();//通知cp，关闭了窗口。表示取消登录
			}
		});
		
		//有游客登录按钮有游客登录的ui
		//....................................登录注册按钮......................................................
		TextView loginOrRegisterView = new TextView(context);//登录注册按钮
		RelativeLayout.LayoutParams loginOrRegisterViewParams=new RelativeLayout.LayoutParams((int)(680*scale),(int)(110*scale));
		loginOrRegisterViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //水平居中 
		loginOrRegisterViewParams.topMargin=(int) (338*scale);
		loginOrRegisterView.setLayoutParams(loginOrRegisterViewParams);
		Drawable loginOrRegisterBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOGINORREGISTER);//登录注册按钮图片
		loginOrRegisterView.setBackgroundDrawable(loginOrRegisterBg);
		loginOrRegisterView.setText("注册/登录");
		loginOrRegisterView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		loginOrRegisterView.setTextColor(Color.WHITE);
		loginOrRegisterView.setGravity(Gravity.CENTER);//文字居中
		loginOrRegisterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					
				mPopupWindow.dismiss();
				//  查询数据库有没有最近登录的账号历史。默认显示出最近一个
				 checkAccountDB();
				 if (accountDBBeans.size() != 0) {
					//打开不需要输入密码的登录框
					 new LoginNoPasswordPopWindow(context, accountDBBeans, loginListener,new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								// 登录二级界面关闭回调
								showWindow();
							}
						});
					 return;
				}
					
				//  打开登录二级界面
				new Login2PopWindow(context, null,loginListener, new OnDismissListener() {
						
					@Override
					public void onDismiss() {
						// 登录二级界面关闭回调
						showWindow();
					}
				},"LoginPopWindow");
			}
		});
		//..................................游客登录按钮...................................................
		TextView guestLoginView = new TextView(context);//登录注册按钮
		RelativeLayout.LayoutParams guestLoginViewParams=new RelativeLayout.LayoutParams((int)(680*scale),(int)(110*scale));
		guestLoginViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //水平居中 
		guestLoginViewParams.topMargin=(int) (506*scale);
		guestLoginView.setLayoutParams(guestLoginViewParams);
		Drawable guestLoginViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GUESTLOGINVIEWBG);//登录注册按钮图片
		guestLoginView.setBackgroundDrawable(guestLoginViewBg);
		guestLoginView.setText("游客登录");
		guestLoginView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		guestLoginView.setTextColor(ColorContant.GRAY);
		guestLoginView.setGravity(Gravity.CENTER);//文字居中
		guestLoginView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mPopupWindow.dismiss();
				//guestLoginPost();//联网游客登录
				GuestLoginRequest guestLoginRequest = new GuestLoginRequest(context, loginListener, LoginPopWindow.this);
				guestLoginRequest.post();
			}
		});
			
		totalLayout.addView(loginOrRegisterView);
		totalLayout.addView(guestLoginView);
		if (SDKConfigure.haveCloseButton) {
			totalLayout.addView(closeView);
		}
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		totalLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		totalLayout.setLayoutParams(totalLayoutParams);
		rootView.addView(totalLayout);
		return rootView;
	}

	/**
	 * 查询数据库
	 * @return
	 */
	protected void checkAccountDB() {
		AccountDao accountDao = new AccountDao(context);
		accountDBBeans = accountDao.findAll();
	}


	@Override
	public void onDismiss() {
		
	}

	@Override
	public void showWindow() {
		MyLogger.lczLog().i("展示：LoginPopWindow");
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
		//mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);//pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
	}


	
}
