package com.feiliu.flgamesdk.view.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
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

import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.ChangePasswordRequest;
import com.feiliu.flgamesdk.net.request.ResetPasswordRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 修改密码界面
 * @author 刘传政
 *
 */
public class ChangePasswordPopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText oldPasswordET;
	private EditText password1ET;
	private EditText password2ET;
	private OnDismissListener lastPopOnDismissListener;
	private AccountDBBean accountDBBean;
	public ChangePasswordPopWindow(Context context,
			AccountDBBean accountDBBean, FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
		this.context = context;
		this.accountDBBean = accountDBBean;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		lastPopOnDismissListener = onDismissListener;
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
		BaseBarView baseBarView = new BaseBarView(context, "修改密码", true,false, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		//............................................原密码............................................................
		oldPasswordET = new FlEditText(context);
		RelativeLayout.LayoutParams oldPasswordETParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		oldPasswordETParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		oldPasswordETParams.topMargin=(int) (160*scale);
		oldPasswordET.setLayoutParams(oldPasswordETParams);
		Drawable password1ETBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		oldPasswordET.setBackgroundDrawable(password1ETBg);
		oldPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		oldPasswordET.setHint("请输入原密码");
		oldPasswordET.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		oldPasswordET.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		oldPasswordET.setSingleLine(true); 
		//.............................................密码1........................................................
		password1ET = new FlEditText(context);
		RelativeLayout.LayoutParams password1ETParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		password1ETParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		password1ETParams.topMargin=(int) (280*scale);
		password1ET.setLayoutParams(password1ETParams);
		password1ET.setBackgroundDrawable(password1ETBg);
		password1ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		password1ET.setHint("请输入新密码");
		password1ET.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		password1ET.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		password1ET.setSingleLine(true); 
		//.............................................密码2........................................................
		password2ET = new FlEditText(context);
		RelativeLayout.LayoutParams password2ETParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		password2ETParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		password2ETParams.topMargin=(int) (400*scale);
		password2ET.setLayoutParams(password2ETParams);
		password2ET.setBackgroundDrawable(password1ETBg);
		password2ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		password2ET.setHint("请再次输入新密码");
		password2ET.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		password2ET.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		password2ET.setSingleLine(true); 
		//............................................确定..........................................................
		TextView OKView = new TextView(context);
		RelativeLayout.LayoutParams OKViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		OKViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		OKViewParams.topMargin=(int) (520*scale);
		OKView.setLayoutParams(OKViewParams);
		Drawable registerBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOGINORREGISTER);
		OKView.setBackgroundDrawable(registerBg);
		OKView.setText("确定");
		OKView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		OKView.setTextColor(Color.WHITE);
		OKView.setGravity(Gravity.CENTER);//文字居中
		OKView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPassword = oldPasswordET.getText().toString().trim();
				String password1 = password1ET.getText().toString().trim();
				String password2 = password2ET.getText().toString().trim();
				//输入原密码为空：请输入原密码
				if (TextUtils.isEmpty(oldPassword)) {
					ToastUtil.showShort(context, "请输入原密码");
					return;
				}
				//输入新密码有1项或2项为空：请输入新密码
				if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
					ToastUtil.showShort(context, "请输入新密码");
					return;
				}
				//原密码错误：原密码不正确，请重新输入
				if (!accountDBBean.getPassword().equals(oldPassword)) {
					ToastUtil.showShort(context, "原密码不正确，请重新输入");
					return;
				}
				//新密码格式错误：密码应为6-16位的数字、字母或下划线组合
				if (!StringMatchUtil.isPassword(password1)) {
					ToastUtil.showShort(context, "密码应为6-16位的数字、字母或下划线组合");
					return;
				}
				//两次输入的内容不一致：两次所输入的新密码不一致，请重新输入
				if (!password1.equals(password2)) {
					ToastUtil.showShort(context, "两次所输入的密码不一致，请重新输入");
					return;
				}
				ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(context, loginListener, ChangePasswordPopWindow.this
						, accountDBBean, password1, "ChangePasswordPopWindow");
				changePasswordRequest.post();
			}
		});
		
		
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(oldPasswordET);
		retAbsoluteLayout.addView(password1ET);
		retAbsoluteLayout.addView(password2ET);
		retAbsoluteLayout.addView(OKView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}



	public void showWindow() {
		MyLogger.lczLog().i("展示：ChangePasswordPopWindow");
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
	}


	
}
