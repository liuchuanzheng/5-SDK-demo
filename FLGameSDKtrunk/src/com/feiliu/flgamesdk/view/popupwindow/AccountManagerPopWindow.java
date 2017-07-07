package com.feiliu.flgamesdk.view.popupwindow;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
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

import com.feiliu.flgamesdk.bean.SdkInfo;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.LoginStatus;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnAccountManagerListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 账号管理界面 此界面只有登录成功后才能调出来。所以不用加一些不必要的非空判断
 * @author 刘传政
 *
 */
public class AccountManagerPopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private FLOnAccountManagerListener accountManagerListener = null;
	private OnDismissListener lastPopOnDismissListener;
	private AccountDBBean accountDBBean;//当前登录账号的信息
	private Boolean haveGuestLogin;
	private BindStatusReceiver bindStatusReceiver;//绑定状态的广播接收者
	private AccountDao accountDao;
	long[] mHits = new long[10];
	private boolean isRealName;
	class BindStatusReceiver extends BroadcastReceiver {

		public void onReceive(Context arg0, Intent intent) {
			unregisteMe(bindStatusReceiver);
			MyLogger.lczLog().i("取消注册广播"+bindStatusReceiver);
			String bindStatus = intent.getStringExtra("bindstatus");
			switch (bindStatus) {
			case "cancle":
				//发送退出通知
				if (accountManagerListener != null) {
					LoginStatus.status = false;//内存标记当前账户登录状态
					accountManagerListener.onLogout();
				}
				//数据库标记一下
				accountDao.updateQuitState(accountDBBean.getUserId(), "quit");
				//sp标记一下实名制相关信息
				SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
				spUtils.putBoolean("realName", false);//初始状态未实名制
				SPUtils spUtils1 = new SPUtils(context, SPConstant.USERINFO);
				spUtils1.putString("ageStatus", "0");//初始状态是否成年未知
				break;
			case "finish":
				//发送退出通知
				if (accountManagerListener != null) {
					LoginStatus.status = false;//内存标记当前账户登录状态
					accountManagerListener.onLogout();
				}
				//数据库标记一下
				accountDao.updateQuitState(accountDBBean.getUserId(), "quit");
				//sp标记一下实名制相关信息
				SPUtils spUtils3 = new SPUtils(context, SPConstant.USERINFO);
				spUtils3.putBoolean("realName", false);//初始状态未实名制
				SPUtils spUtils4 = new SPUtils(context, SPConstant.USERINFO);
				spUtils4.putString("ageStatus", "0");//初始状态是否成年未知
				break;
			default:
				break;
			}
			MyLogger.lczLog().i("接收到了绑定状态广播"+bindStatus);
		}

		public void registeMe(BindStatusReceiver receiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.feiliu.flgamesdk.BindStatusReceiver");
			context.registerReceiver(receiver, filter);
		}

		public void unregisteMe(BindStatusReceiver receiver) {
			context.unregisterReceiver(receiver);
		}


	}
	public AccountManagerPopWindow(Context context, Boolean haveGuestLogin,
			String password,
			FLOnLoginListener loginListener,FLOnAccountManagerListener accountManagerListener, OnDismissListener onDismissListener) {
		this.context = context;
		this.haveGuestLogin = haveGuestLogin;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		this.accountManagerListener = accountManagerListener;
		lastPopOnDismissListener = onDismissListener;
		//查询数据库中当前登录的账号。显示到界面上
		accountDao = new AccountDao(context);
		List<AccountDBBean> findAllIncludeGuest = accountDao.findAllIncludeGuest();
		accountDBBean = findAllIncludeGuest.get(0);
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				(int)(designWidth*scale), (int)(designHeight*scale + 103*scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		//...........................................隐藏功能............................................................
		View SDKVersionView = new View(context);//这是一个隐藏的功能，便于qa人员使用。连续点击10下可以弹出sdk的版本号
		RelativeLayout.LayoutParams SDKVersionViewParams=new RelativeLayout.LayoutParams((int)(300*scale),(int)(100*scale));
		SDKVersionViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		SDKVersionViewParams.topMargin=(int) (0*scale);
		SDKVersionView.setLayoutParams(SDKVersionViewParams);
		SDKVersionView.setBackgroundColor(Color.TRANSPARENT);
		SDKVersionView.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (mHits[mHits.length - 1] - 3000)) {
					SdkInfo sdkInfo = new SdkInfo(context);
					String sdkVersion = sdkInfo.getSdkVersion();
					ToastUtil.showLong(context, "SDK版本号："+sdkVersion);
				}
			}
		});
		
		//..........................................title............................................................
		BaseBarView baseBarView = new BaseBarView(context, "账号管理", false,true, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(105*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		//.............................................账号........................................................
		RelativeLayout accountRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams accountRlParams = new RelativeLayout.LayoutParams((int)(825*scale), (int)(105*scale));
		accountRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		accountRlParams.topMargin=(int) (130*scale);
		accountRl.setLayoutParams(accountRlParams);
		Drawable accountRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		accountRl.setBackgroundDrawable(accountRlBg);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。账号左。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView accountTvLeft = new TextView(context);
		RelativeLayout.LayoutParams accountTvLeftParams=new RelativeLayout.LayoutParams((int)(200*scale),(int)(105*scale));
		accountTvLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		accountTvLeft.setLayoutParams(accountTvLeftParams);
		accountTvLeft.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		accountTvLeft.setPadding((int)(15*scale), 0, 0, 0);
		accountTvLeft.setText("账号");
		accountTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。账号右。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView accountTvRight = new TextView(context);
		RelativeLayout.LayoutParams accountTvRightParams=new RelativeLayout.LayoutParams((int)(625*scale),(int)(105*scale));
		accountTvRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		accountTvRight.setLayoutParams(accountTvRightParams);
		accountTvRight.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);//文字右对齐竖直居中
		accountTvRight.setPadding(0, 0, (int)(50*scale), 0);
		accountTvRight.setText(accountDBBean.getUserName());
		accountTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		accountTvRight.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		accountTvRight.setSingleLine(true); 
		//.............................................密码........................................................
		
		RelativeLayout passwordRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams passwordRlParams = new RelativeLayout.LayoutParams((int)(825*scale), (int)(105*scale));
		passwordRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		passwordRlParams.topMargin=(int) (233*scale);
		passwordRl.setLayoutParams(passwordRlParams);
		Drawable passwordRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		passwordRl.setBackgroundDrawable(passwordRlBg);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。密码左。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView passwordTvLeft = new TextView(context);
		RelativeLayout.LayoutParams passwordTvLeftParams=new RelativeLayout.LayoutParams((int)(200*scale),(int)(105*scale));
		accountTvLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		passwordTvLeft.setLayoutParams(passwordTvLeftParams);
		passwordTvLeft.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		passwordTvLeft.setPadding((int)(15*scale), 0, 0, 0);
		passwordTvLeft.setText("密码");
		passwordTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。密码右。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView passwordTvRight = new TextView(context);
		RelativeLayout.LayoutParams passwordTvRightParams=new RelativeLayout.LayoutParams((int)(625*scale),(int)(105*scale));
		passwordTvRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		passwordTvRight.setLayoutParams(passwordTvRightParams);
		passwordTvRight.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);//文字右对齐竖直居中
		passwordTvRight.setPadding(0, 0, (int)(50*scale), 0);
		passwordTvRight.setText("修改");
		passwordTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13); 
		passwordTvRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ("0".equals(accountDBBean.getUserType())) {
					ToastUtil.showShort(context,"请先绑定手机或邮箱成为正式账号");
				}else if ("1".equals(accountDBBean.getUserType())) {
					mPopupWindow.dismiss();
					//打开修改密码界面
					new ChangePasswordPopWindow(context,accountDBBean,loginListener, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							showWindow();
						}
					});
				}
			}
		});
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。向右箭头。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView passwordArrow = new TextView(context);
		RelativeLayout.LayoutParams passwordArrowParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(30*scale));
		passwordArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		passwordArrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
		passwordArrowParams.rightMargin = (int) (20*scale); 
		passwordArrow.setLayoutParams(passwordArrowParams);
		Drawable passwordArrowBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWRIGHT);//向右箭头
		passwordArrow.setBackgroundDrawable(passwordArrowBg);
		//............................................手机..........................................................
		
		RelativeLayout phoneRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams phoneRlParams = new RelativeLayout.LayoutParams((int)(825*scale), (int)(105*scale));
		phoneRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		phoneRlParams.topMargin=(int) (336*scale);
		phoneRl.setLayoutParams(phoneRlParams);
		Drawable phoneRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		phoneRl.setBackgroundDrawable(phoneRlBg);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。手机左。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView phoneTvLeft = new TextView(context);
		RelativeLayout.LayoutParams phoneTvLeftParams=new RelativeLayout.LayoutParams((int)(200*scale),(int)(105*scale));
		phoneTvLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		phoneTvLeft.setLayoutParams(phoneTvLeftParams);
		phoneTvLeft.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		phoneTvLeft.setPadding((int)(15*scale), 0, 0, 0);
		phoneTvLeft.setText("手机");
		phoneTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		//添加小红点
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。手机右。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView phoneTvRight = new TextView(context);
		RelativeLayout.LayoutParams phoneTvRightParams=new RelativeLayout.LayoutParams((int)(625*scale),(int)(105*scale));
		phoneTvRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		phoneTvRight.setLayoutParams(phoneTvRightParams);
		phoneTvRight.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);//文字右对齐竖直居中
		phoneTvRight.setPadding(0, 0, (int)(50*scale), 0);
		phoneTvRight.setText("");
		phoneTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		phoneTvRight.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		phoneTvRight.setSingleLine(true); 
		
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。向右箭头。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView phoneArrow = new TextView(context);
		RelativeLayout.LayoutParams phoneArrowParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(30*scale));
		phoneArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		phoneArrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
		phoneArrowParams.rightMargin = (int)(20*scale);
		phoneArrow.setLayoutParams(phoneArrowParams);
		Drawable phoneArrowBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWRIGHT);//向右箭头
		phoneArrow.setBackgroundDrawable(phoneArrowBg);
		
		//整条的点击事件
		if (accountDBBean.getPhone() != null && !"".equals(accountDBBean.getPhone())) {
			phoneTvRight.setText(accountDBBean.getPhone());
			phoneTvRight.setTextColor(Color.BLACK);
			phoneRl.setEnabled(false);
			phoneArrow.setVisibility(View.INVISIBLE);
		}else if (accountDBBean.getPhone() == null || "".equals(accountDBBean.getPhone())) {
			//添加小红点
			TextView phoneRedPoint = new TextView(context);
			RelativeLayout.LayoutParams phoneRedPointParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(15*scale));
			phoneRedPointParams.leftMargin = (int) (100*scale);
			phoneRedPointParams.topMargin = (int) (30*scale);
			phoneRedPoint.setLayoutParams(phoneRedPointParams);
			Drawable phoneRedPointBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED_POINT);//小红点
			phoneRedPoint.setBackgroundDrawable(phoneRedPointBg);
			phoneRl.addView(phoneRedPoint);
			
			phoneTvRight.setText("未绑定");
			phoneTvRight.setTextColor(Color.RED);
			phoneRl.setEnabled(true);
			phoneRl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPopupWindow.dismiss();
					//   弹出手机号绑定界面
					new BindPopWindow(context, "", "",accountDBBean, "phone", loginListener, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							showWindow();
						}
					});
				}
			});
		}
		//..............................................邮箱....................................................................
		
		RelativeLayout mailRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams mailRlParams = new RelativeLayout.LayoutParams((int)(825*scale), (int)(105*scale));
		mailRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		mailRlParams.topMargin=(int) (439*scale);
		mailRl.setLayoutParams(mailRlParams);
		Drawable mailRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		mailRl.setBackgroundDrawable(mailRlBg);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。邮箱左。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView mailTvLeft = new TextView(context);
		RelativeLayout.LayoutParams mailTvLeftParams=new RelativeLayout.LayoutParams((int)(200*scale),(int)(105*scale));
		mailTvLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mailTvLeft.setLayoutParams(mailTvLeftParams);
		mailTvLeft.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		mailTvLeft.setPadding((int)(15*scale), 0, 0, 0);
		mailTvLeft.setText("邮箱");
		mailTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。邮箱右。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView mailTvRight = new TextView(context);
		RelativeLayout.LayoutParams mailTvRightParams=new RelativeLayout.LayoutParams((int)(625*scale),(int)(105*scale));
		mailTvRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mailTvRight.setLayoutParams(mailTvRightParams);
		mailTvRight.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);//文字右对齐竖直居中
		mailTvRight.setPadding(0, 0, (int)(50*scale), 0);
		mailTvRight.setText("");
		mailTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		mailTvRight.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		mailTvRight.setSingleLine(true);
		
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。向右箭头。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView mailArrow = new TextView(context);
		RelativeLayout.LayoutParams mailArrowParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(30*scale));
		mailArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mailArrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
		mailArrowParams.rightMargin = (int)(20*scale);
		mailArrow.setLayoutParams(mailArrowParams);
		Drawable mailArrowBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWRIGHT);//向右箭头
		mailArrow.setBackgroundDrawable(mailArrowBg);
		//整条点击事件
		if (accountDBBean.getEmail() != null && !"".equals(accountDBBean.getEmail())) {
			mailTvRight.setText(accountDBBean.getEmail());
			mailTvRight.setTextColor(Color.BLACK);
			mailRl.setEnabled(false);
			mailArrow.setVisibility(View.INVISIBLE);
		}else if (accountDBBean.getEmail() == null || "".equals(accountDBBean.getEmail())) {
			//添加小红点
			TextView mailRedPoint = new TextView(context);
			RelativeLayout.LayoutParams mailRedPointParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(15*scale));
			mailRedPointParams.leftMargin = (int) (100*scale);
			mailRedPointParams.topMargin = (int) (30*scale);
			mailRedPoint.setLayoutParams(mailRedPointParams);
			Drawable mailRedPointBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED_POINT);//小红点
			mailRedPoint.setBackgroundDrawable(mailRedPointBg);
			mailRl.addView(mailRedPoint);
			
			mailTvRight.setText("未绑定");
			mailTvRight.setTextColor(Color.RED);
			mailRl.setEnabled(true);
			mailRl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mPopupWindow.dismiss();
					//   弹出邮箱绑定界面
					new BindPopWindow(context, "", "", accountDBBean,"email", loginListener, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							showWindow();
						}
					});
				}
			});
		}
		//..............................................实名认证...............................................................
		RelativeLayout realNameRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams realNameRlParams = new RelativeLayout.LayoutParams((int)(825*scale), (int)(105*scale));
		realNameRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		realNameRlParams.topMargin=(int) (542*scale);
		realNameRl.setLayoutParams(realNameRlParams);
		Drawable realNameRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		realNameRl.setBackgroundDrawable(realNameRlBg);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。实名认证左。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView realNameTvLeft = new TextView(context);
		RelativeLayout.LayoutParams realNameTvLeftParams=new RelativeLayout.LayoutParams((int)(200*scale),(int)(105*scale));
		realNameTvLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		realNameTvLeft.setLayoutParams(realNameTvLeftParams);
		realNameTvLeft.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		realNameTvLeft.setPadding((int)(15*scale), 0, 0, 0);
		realNameTvLeft.setText("实名认证");
		realNameTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。实名认证右。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView realNameTvRight = new TextView(context);
		RelativeLayout.LayoutParams realNameTvRightParams=new RelativeLayout.LayoutParams((int)(625*scale),(int)(105*scale));
		realNameTvRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		realNameTvRight.setLayoutParams(realNameTvRightParams);
		realNameTvRight.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);//文字右对齐竖直居中
		realNameTvRight.setPadding(0, 0, (int)(50*scale), 0);
		realNameTvRight.setText("未认证");//默认是未认证的
		realNameTvRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		realNameTvRight.setTextColor(Color.RED);
		realNameTvRight.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		realNameTvRight.setSingleLine(true);
		
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。向右箭头。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView realNameArrow = new TextView(context);
		RelativeLayout.LayoutParams realNameArrowParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(30*scale));
		realNameArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		realNameArrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
		realNameArrowParams.rightMargin = (int)(20*scale);
		realNameArrow.setLayoutParams(realNameArrowParams);
		Drawable realNameArrowBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWRIGHT);//向右箭头
		realNameArrow.setBackgroundDrawable(realNameArrowBg);
		//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。小红点。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
		TextView realNameRedPoint = new TextView(context);
		RelativeLayout.LayoutParams realNameRedPointParams=new RelativeLayout.LayoutParams((int)(15*scale),(int)(15*scale));
		realNameRedPointParams.leftMargin = (int) (180*scale);
		realNameRedPointParams.topMargin = (int) (30*scale);
		realNameRedPoint.setLayoutParams(realNameRedPointParams);
		Drawable realNameRedPointBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED_POINT);//小红点
		realNameRedPoint.setBackgroundDrawable(realNameRedPointBg);
		realNameRl.addView(realNameRedPoint);
		SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
		isRealName = spUtils.getBoolean("realName", false);
		//整条点击事件
		if (isRealName) {
			//已经实名认证
			realNameRl.setEnabled(false);
			realNameRedPoint.setVisibility(View.INVISIBLE);
			realNameTvRight.setText("已认证");
			realNameTvRight.setTextColor(Color.BLACK);
			realNameArrow.setVisibility(View.INVISIBLE);
		}else {
			//未实名认证
			if ("0".equals(accountDBBean.getUserType())) {
				//游客账号 点击toast提示
				
				realNameRl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ToastUtil.showShort(context,"请先绑定手机号或邮箱升级为正式账号");
					}
				});
			}else if ("1".equals(accountDBBean.getUserType())) {
				//正式账号 点击能正常跳转
				realNameRl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//弹出实名认证界面
						mPopupWindow.dismiss();
						new RealNamePopWindow(context, loginListener, new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								showWindow();
							}
						},true,false);
					}
				});
			}
		}
		
		
		
		//..............................................退出账号...............................................................
		TextView quitTv = new TextView(context);
		RelativeLayout.LayoutParams quitTvParams=new RelativeLayout.LayoutParams((int)(825*scale),(int)(105*scale));
		quitTvParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		quitTvParams.topMargin=(int) (663*scale);
		quitTv.setLayoutParams(quitTvParams);
		quitTv.setGravity(Gravity.CENTER_VERTICAL);//文字竖直居中
		quitTv.setGravity(Gravity.CENTER);
		Drawable quitTvBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形框
		quitTv.setBackgroundDrawable(quitTvBg);
		quitTv.setText("退出当前账号");
		quitTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		quitTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				if ("0".equals(accountDBBean.getUserType())) {
					//注册一个绑定状态的广播接收者
					 bindStatusReceiver = new BindStatusReceiver();
					 bindStatusReceiver.registeMe(bindStatusReceiver);
					 MyLogger.lczLog().i("注册广播"+bindStatusReceiver);
					//  游客账号  提示绑定
					SPUtils spUtils = new SPUtils(context, SPConstant.GIFTSTATUS);
					String giftStatus = spUtils.getString("giftStatus", "0");
					new BindTipsPopWindow(context, giftStatus, loginListener, null,"");
				}else if ("1".equals(accountDBBean.getUserType())) {
					//需求文档原来要求退出后弹出相应额登录界面。后来分析可能引起与cp的冲突，取消了
					/*if (haveGuestLogin) {
						new LoginPopWindow(context, "", "", loginListener);
					}else {
						// 弹出登录界面
						new Login2PopWindow(context, accountDBBeans,loginListener, new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								// 登录二级界面关闭回调
							}
						});
					}*/
					//数据库标记一下
					accountDao.updateQuitState(accountDBBean.getUserId(), "quit");
					//sp标记一下实名制相关信息
					SPUtils spUtils3 = new SPUtils(context, SPConstant.USERINFO);
					spUtils3.putBoolean("realName", false);//初始状态未实名制
					SPUtils spUtils4 = new SPUtils(context, SPConstant.USERINFO);
					spUtils4.putString("ageStatus", "0");//初始状态是否成年未知
					//发送退出通知
					if (accountManagerListener != null) {
						LoginStatus.status = false;//内存标记当前账户登录状态
						accountManagerListener.onLogout();
					}
				}
			}
		});
		
		retAbsoluteLayout.addView(baseBarView);
		
		retAbsoluteLayout.addView(SDKVersionView);
		
		accountRl.addView(accountTvLeft);
		accountRl.addView(accountTvRight);
		retAbsoluteLayout.addView(accountRl);
		
		passwordRl.addView(passwordTvLeft);
		passwordRl.addView(passwordTvRight);
		passwordRl.addView(passwordArrow);
		retAbsoluteLayout.addView(passwordRl);
		
		phoneRl.addView(phoneTvLeft);
		phoneRl.addView(phoneTvRight);
		phoneRl.addView(phoneArrow);
		retAbsoluteLayout.addView(phoneRl);
		
		mailRl.addView(mailTvLeft);
		mailRl.addView(mailTvRight);
		mailRl.addView(mailArrow);
		retAbsoluteLayout.addView(mailRl);
		
		realNameRl.addView(realNameTvLeft);
		realNameRl.addView(realNameTvRight);
		realNameRl.addView(realNameArrow);
		retAbsoluteLayout.addView(realNameRl);
		
		retAbsoluteLayout.addView(quitTv);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}
	private void sendBroadcast(Context context,String action,String name,String value) {
		Intent intent = new Intent();
        intent.setAction(action); 
        intent.putExtra(name, value);
        //发送广播
        context.sendBroadcast(intent);
	}
	/**
	 * 展示隐私条款界面
	 */
	protected void showClauseActivity() {
		Intent intent = new Intent(context,FLSdkActivity.class);
		Bundle bundle = new Bundle();
	    intent.putExtra("fromtype",0);//表明是隐私条款意图
	    intent.putExtras(bundle);
	    ((Activity)context).startActivity(intent);
	}


	public void showWindow() {
		MyLogger.lczLog().i("展示：AccountManagerPopWindow");
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

	@Override
	public void backbuttonclick() {
		mPopupWindow.dismiss();
		if (null != lastPopOnDismissListener) {
			lastPopOnDismissListener.onDismiss();//通知上一个pop
		}
		
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
		if (null != lastPopOnDismissListener) {
			lastPopOnDismissListener.onDismiss();//通知上一个pop
		}
	}


	
}
