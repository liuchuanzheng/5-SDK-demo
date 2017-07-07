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

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.RegisterRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 注册三级界面
 * @author 刘传政
 *
 */
public class Register3PopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText passWordEt;//密码输入框
	private String accountString = "";//注册2级界面传过来的账号
	private OnDismissListener lastPopOnDismissListener;
	private String code;//短信得到的验证码
	private Boolean lookStatus = false;//小眼睛的状态
	/**
	 * @param context
	 * @param accountString  手机号或邮箱
	 * @param code   验证码
	 * @param loginListener
	 * @param onDismissListener  便于监听返回键
	 */
	public Register3PopWindow(Context context, String accountString,String code,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
		this.accountString = accountString;
		this.code = code;
		this.context = context;
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
		BaseBarView baseBarView = new BaseBarView(context, "设置密码", true,false, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//..............................................手机号或邮箱号：...........................................................
		TextView phoneView = new TextView(context);
		RelativeLayout.LayoutParams phoneViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(60*scale));
		phoneViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		phoneViewParams.topMargin=(int) (120*scale);
		phoneView.setLayoutParams(phoneViewParams);
		if (StringMatchUtil.isPhone(accountString)) {
			phoneView.setText("手机号："+accountString);
		}else if (StringMatchUtil.isEmail(accountString)) {
			phoneView.setText("邮箱："+accountString);
		}
		phoneView.setTextColor(Color.BLACK);
		phoneView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		phoneView.setGravity(Gravity.CENTER_VERTICAL);
		phoneView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		phoneView.setSingleLine(true); 
		
		//......................................将作为账号用于登录或找回密码............................................................
		TextView tipsView = new TextView(context);
		RelativeLayout.LayoutParams tipsViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(60*scale));
		tipsViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		tipsViewParams.topMargin=(int) (180*scale);
		tipsView.setLayoutParams(tipsViewParams);
		tipsView.setText("将作为账号用于登录及找回密码");
		tipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		tipsView.setTextColor(Color.BLACK);
		tipsView.setGravity(Gravity.CENTER_VERTICAL);
		
		//.............................................密码输入框.......................................................
		passWordEt = new FlEditText(context);
		RelativeLayout.LayoutParams passWordEtParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		passWordEtParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		passWordEtParams.topMargin=(int) (236*scale);
		passWordEt.setLayoutParams(passWordEtParams);
		Drawable passWordEtBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		passWordEt.setBackgroundDrawable(passWordEtBg);
		passWordEt.setHint("6-16位的数字、字母或下划线组合");
		passWordEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		passWordEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		passWordEt.setSingleLine(true);  
		passWordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		//...............................................小眼睛.........................................................
		final TextView look = new TextView(context);
		RelativeLayout.LayoutParams lookParams=new RelativeLayout.LayoutParams((int)(100*scale),(int)(100*scale));
		lookParams.leftMargin = (int) (710*scale);
		lookParams.topMargin=(int) (236*scale);
		look.setLayoutParams(lookParams);
		final Drawable lookOn =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOOKON);
		final Drawable lookOff =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOOKOFF);
		look.setBackgroundDrawable(lookOff);
		look.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(passWordEt.getText().toString())) {
					if (lookStatus) {
						lookStatus = false;
						passWordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						look.setBackgroundDrawable(lookOff);
					}else {
						lookStatus = true;
						passWordEt.setInputType(InputType.TYPE_CLASS_TEXT);
						look.setBackgroundDrawable(lookOn);
					}
				}
				
			}
		});
		//..............................................确定..........................................................
		TextView yesView = new TextView(context);
		RelativeLayout.LayoutParams yesViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		yesViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		yesViewParams.topMargin=(int) (470*scale);
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
				String password = passWordEt.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					ToastUtil.showShort(context, "请输入密码");
					return;
				}
				if (!StringMatchUtil.isPassword(password)) {
					ToastUtil.showShort(context, "密码应为6-16位的数字、字母或下划线组合");
					return;
				}
				RegisterRequest registerRequest = new RegisterRequest(context, loginListener
						, Register3PopWindow.this, accountString, code, password);
				registerRequest.post();
			}
		});
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(phoneView);
		retAbsoluteLayout.addView(tipsView);
		retAbsoluteLayout.addView(passWordEt);
		retAbsoluteLayout.addView(look);
		retAbsoluteLayout.addView(yesView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}


	public void showWindow() {
		MyLogger.lczLog().i("展示：Register3PopWindow");
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
