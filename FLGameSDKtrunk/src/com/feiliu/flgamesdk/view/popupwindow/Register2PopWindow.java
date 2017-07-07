package com.feiliu.flgamesdk.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.ColorContant;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.CheckIdenfyCodeRequest;
import com.feiliu.flgamesdk.net.request.SendIdenfyCodeRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 注册二级界面
 * @author 刘传政
 *
 */
public class Register2PopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText identifyEt;//验证码输入框
	public TextView reSendView;//重新发送验证码的按钮
	private String accountString = "";//注册一级界面传过来的账号
	private OnDismissListener lastPopOnDismissListener;
	private String code;//短信得到的验证码
	private CountDownTimer countDownTimer=null;
	public Register2PopWindow(Context context, String accountString,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
		this.accountString = accountString;
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		lastPopOnDismissListener = onDismissListener;
		//联网发送验证码
		SendIdenfyCodeRequest sendidenfyCodeRequest = new SendIdenfyCodeRequest(context, loginListener, Register2PopWindow.this
				, accountString);
		sendidenfyCodeRequest.post();
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
				(int) (designWidth * scale), (int) (designHeight * scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		//..........................................title............................................................
		BaseBarView baseBarView = new BaseBarView(context, "验证码校验", true,false, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//..............................................验证码发送到...........................................................
		TextView tipsView = new TextView(context);
		RelativeLayout.LayoutParams tipsViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		tipsViewParams.leftMargin = (int) (100*scale);
		tipsViewParams.topMargin=(int) (150*scale);
		tipsView.setLayoutParams(tipsViewParams);
		tipsView.setText("验证码已发送到："+accountString);
		tipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
		tipsView.setTextColor(Color.BLACK);
		tipsView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		tipsView.setSingleLine(true);
		tipsView.setGravity(Gravity.CENTER_VERTICAL);
		
		//......................................验证码输入框.....................................................................
		identifyEt = new FlEditText(context);
		RelativeLayout.LayoutParams identifyEtParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		identifyEtParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		identifyEtParams.topMargin=(int) (236*scale);
		identifyEt.setLayoutParams(identifyEtParams);
		Drawable identifyEtBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		identifyEt.setBackgroundDrawable(identifyEtBg);
		identifyEt.setHint("请输入验证码");
		identifyEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		identifyEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		identifyEt.setSingleLine(true); 
		
		//.............................................隐私条款.......不使用了.................................................
		TextView clauseView = new TextView(context);
		RelativeLayout.LayoutParams clauseViewParams=new RelativeLayout.LayoutParams((int)(300*scale),(int)(60*scale));
		clauseViewParams.leftMargin = (int) (142*scale);
		clauseViewParams.topMargin=(int) (380*scale);
		clauseView.setLayoutParams(clauseViewParams);
		clauseView.setText("隐私条款");
		clauseView.setTextColor(Color.BLUE);
		clauseView.setGravity(Gravity.CENTER_VERTICAL);
		clauseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开隐私条款activity，加载网页
				showClauseActivity();
			}
		});
		//............................................确定..........................................................
		TextView yesView = new TextView(context);
		RelativeLayout.LayoutParams yesViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		yesViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		yesViewParams.topMargin=(int) (426*scale);
		yesView.setLayoutParams(yesViewParams);
		Drawable yesViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOGINORREGISTER);
		yesView.setBackgroundDrawable(yesViewBg);
		yesView.setText("确定");
		yesView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		yesView.setTextColor(Color.WHITE);
		yesView.setGravity(Gravity.CENTER);//文字居中
		yesView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//   确定点击事件
				code = identifyEt.getText().toString().trim();
				if (TextUtils.isEmpty(code)) {
					ToastUtil.showShort(context, "验证码不能为空");
					return;
				}
				//checkIdenfyCodePost(code,accountString);
				CheckIdenfyCodeRequest checkIdenfyCodeRequest = new CheckIdenfyCodeRequest(context, loginListener
						, Register2PopWindow.this, null,accountString, code,"Register2PopWindow");
				checkIdenfyCodeRequest.post();
				
			}
		});
		
		//...........................................重新发送........................................................
		reSendView = new TextView(context);
		RelativeLayout.LayoutParams reSendViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		reSendViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		reSendViewParams.topMargin=(int) (556*scale);
		reSendView.setLayoutParams(reSendViewParams);
		Drawable reSendViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GUESTLOGINVIEWBG);
		reSendView.setBackgroundDrawable(reSendViewBg);
		reSendView.setText("重新发送");
		reSendView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		startTimeCoutDown();//倒计时
		reSendView.setTextColor(ColorContant.GRAY);
		reSendView.setGravity(Gravity.CENTER);//文字居中
		reSendView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//   重新发送点击事件
				SendIdenfyCodeRequest sendidenfyCodeRequest = new SendIdenfyCodeRequest(context, loginListener, Register2PopWindow.this
						, accountString);
				sendidenfyCodeRequest.post();
				startTimeCoutDown();//倒计时
			}
		});
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(tipsView);
		retAbsoluteLayout.addView(identifyEt);
		//retAbsoluteLayout.addView(clauseView);
		retAbsoluteLayout.addView(yesView);
		retAbsoluteLayout.addView(reSendView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}
	private void startTimeCoutDown() {
		reSendView.setEnabled(false);
		countDownTimer=	new CountDownTimer(60000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				reSendView.setText("重新发送(" + millisUntilFinished/1000+")");
			}
			@Override
			public void onFinish() {
				reSendView.setText("重新发送");
				reSendView.setEnabled(true);
			}
		}.start();
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
		MyLogger.lczLog().i("展示：Register2PopWindow");
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
		lastPopOnDismissListener.onDismiss();//通知上一个pop
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
	}


	
}
