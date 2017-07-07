package com.feiliu.flgamesdk.view.popupwindow;

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
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.GuestLoginRequest;
import com.feiliu.flgamesdk.net.request.NormalLoginRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import android.text.TextUtils;

/**
 * 如果有账号历史记录，显示的不需要输入密码的输入框
 * @author 刘传政
 *
 */
public class LoginNoPasswordPopWindow extends BasePopWindow implements  OnDismissListener{
	private FLOnLoginListener loginListener;
	private List<AccountDBBean> accountDBBeans;//从登录一级界面传过来的数据库查询出来的账号集合
	private OnDismissListener lastPopOnDismissListener;//上一个弹出框传过来的。为了配合返回键的功能
	public LoginNoPasswordPopWindow(Context context, List<AccountDBBean> accountDBBeans,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
		this.context = context;
		this.accountDBBeans = accountDBBeans;
		this.lastPopOnDismissListener = onDismissListener;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		currentWindowView = createMyUi();
		showWindow();
	}

	public View createMyUi() {
		final RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				(int) (designWidth * scale), (int) (designHeight * scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TOTALLAYOUTBG);//得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		//..........................................返回按钮............................................................
		View backView = null;
		if (null != lastPopOnDismissListener) {
			backView = new View(context);
			RelativeLayout.LayoutParams backViewParams=new RelativeLayout.LayoutParams((int)(130*scale),(int)(100*scale));
			backViewParams.topMargin=(int) (0*scale);
			backViewParams.leftMargin = (int) (0*scale);
			backView.setLayoutParams(backViewParams);
			Drawable backViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.BACKVIEW);//关闭按钮图片
			backView.setBackgroundDrawable(backViewBg);
			backView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPopupWindow.dismiss();
					if (null != lastPopOnDismissListener) {
						lastPopOnDismissListener.onDismiss();//通知上一个pop，此弹出框关闭了
					}
				}
			});
		}
		//..........................................关闭按钮...............cp可配.............................................
		
		View closeView = new View(context);
		RelativeLayout.LayoutParams viewCloseParams=new RelativeLayout.LayoutParams((int)(80*scale),(int)(70*scale));
		viewCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE); //右对齐 
		viewCloseParams.topMargin=(int) (03*scale);
		viewCloseParams.rightMargin = (int) (10*scale);
		closeView.setLayoutParams(viewCloseParams);
		Drawable closeViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.CLOSEVIEW);//关闭按钮图片
		closeView.setBackgroundDrawable(closeViewBg);
		closeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				loginListener.onLoginCancel();//通知cp，关闭了窗口。表示取消登录
			}
		});
		
		//..........................................飞流账号....................................................................
		
		TextView tipsView = new TextView(context);
		RelativeLayout.LayoutParams tipsViewParams=new RelativeLayout.LayoutParams((int)(670*scale),(int)(110*scale));
		tipsViewParams.leftMargin = (int) (110*scale);
		tipsViewParams.topMargin=(int) (280*scale);
		tipsView.setLayoutParams(tipsViewParams);
		tipsView.setText(accountDBBeans.get(0).getUserName());
		tipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		tipsView.setGravity(Gravity.CENTER);//文字居中
		tipsView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		tipsView.setSingleLine(true);  
		tipsView.setTextColor(Color.BLACK);
		//..............................................登录...................................................................
		TextView loginView = new TextView(context);//登录按钮
		RelativeLayout.LayoutParams loginViewParams=new RelativeLayout.LayoutParams((int)(670*scale),(int)(110*scale));
		loginViewParams.leftMargin = (int) (110*scale);
		loginViewParams.topMargin=(int) (420*scale);
		loginView.setLayoutParams(loginViewParams);
		Drawable loginBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOGINORREGISTER);
		loginView.setBackgroundDrawable(loginBg);
		loginView.setText("登录");
		loginView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		loginView.setTextColor(Color.WHITE);
		loginView.setGravity(Gravity.CENTER);//文字居中
		loginView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					// 联网正常登录
					NormalLoginRequest normalLoginRequest = new NormalLoginRequest(context, loginListener
							,true, accountDBBeans.get(0).getUserName(), accountDBBeans.get(0).getPassword(), LoginNoPasswordPopWindow.this);
					normalLoginRequest.post();
			}
		});
		
		//.............................................切换账号........................................................
		TextView changeAccountView = new TextView(context);
		RelativeLayout.LayoutParams changeAccountViewParams=new RelativeLayout.LayoutParams((int)(300*scale),(int)(100*scale));
		changeAccountViewParams.leftMargin = (int) (122*scale);
		changeAccountViewParams.topMargin=(int) (570*scale);
		changeAccountView.setLayoutParams(changeAccountViewParams);
		changeAccountView.setText("切换账号");
		changeAccountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		changeAccountView.setTextColor(Color.GRAY);
		changeAccountView.setGravity(Gravity.CENTER);//文字居中
		changeAccountView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				new Login2PopWindow(context, accountDBBeans, loginListener, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						showWindow();
					}
				},"LoginNoPasswordPopWindow");
			}
		});
		//...............................................竖线...........................................................
		TextView textWall = new TextView(context);
		RelativeLayout.LayoutParams textWallParams=new RelativeLayout.LayoutParams((int)(5*scale),(int)(40*scale));
		textWallParams.leftMargin = (int) (440*scale);
		textWallParams.topMargin=(int) (600*scale);
		textWall.setLayoutParams(textWallParams);
		Drawable textWallBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTWALL);
		textWall.setBackgroundDrawable(textWallBg);
		//.............................................忘记密码........................................................
		
		TextView forgetView = new TextView(context);
		RelativeLayout.LayoutParams forgetViewParams=new RelativeLayout.LayoutParams((int)(300*scale),(int)(100*scale));
		forgetViewParams.leftMargin = (int) (462*scale);
		forgetViewParams.topMargin=(int) (570*scale);
		forgetView.setLayoutParams(forgetViewParams);
		forgetView.setText("忘记密码?");
		forgetView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		forgetView.setTextColor(Color.GRAY);
		forgetView.setGravity(Gravity.CENTER);//文字居中
		forgetView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//  忘记密码点击事件
				mPopupWindow.dismiss();
				new FindPasswordPopWindow(context, "", "", loginListener, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						showWindow();//下一个pop关闭了。自己显示出来
					}
				});
			}
		});
		
		if (null != lastPopOnDismissListener && null != backView) {
			retAbsoluteLayout.addView(backView);
		}
		
		if (null == lastPopOnDismissListener && SDKConfigure.haveCloseButton) {
			retAbsoluteLayout.addView(closeView);
		}
	
		retAbsoluteLayout.addView(tipsView);
		retAbsoluteLayout.addView(loginView);
		retAbsoluteLayout.addView(changeAccountView);
		retAbsoluteLayout.addView(textWall);
		retAbsoluteLayout.addView(forgetView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}



	public void showWindow() {
		MyLogger.lczLog().i("展示：LoginNoPasswordPopWindow");
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
