package com.feiliu.flgamesdk.view.popupwindow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
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
import com.feiliu.flgamesdk.listener.FLOnNetFinishListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.AccountBindUsableRequest;
import com.feiliu.flgamesdk.net.request.GetAccountUsableRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 绑定账号界面
 * <br/>会根据上一个界面的数据自动显示手机号绑定或邮箱绑定
 * @author 刘传政
 *
 */
public class BindPopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText accountEt;//账号输入框
	private String accountString = "";//账号输入框的内容。保证可用后才会赋值
	private String bindType;//根据此数值提示输入手机号还是邮箱号
	private AccountDBBean accountDBBean;//当前登录账号的信息
	public TextView registerView;
	private ProgressDialog progressDialog = null;
	private OnDismissListener lastPopOnDismissListener;
	public BindPopWindow(Context context, String userName,
			String password,AccountDBBean accountDBBean, String bindType,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
		this.context = context;
		this.bindType = bindType;
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
		BaseBarView baseBarView = new BaseBarView(context, "绑定账号", true,false, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		//.............................................输入框........................................................
		accountEt = new FlEditText(context);
		RelativeLayout.LayoutParams accountEtParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		accountEtParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		accountEtParams.topMargin=(int) (185*scale);
		accountEt.setLayoutParams(accountEtParams);
		Drawable accountEtBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		accountEt.setBackgroundDrawable(accountEtBg);
		if ("phone".equals(bindType)) {
			accountEt.setHint("请输入您的手机号");
		}else if ("email".equals(bindType)) {
			accountEt.setHint("请输入您的邮箱");
		}else if ("phoneOrEmail".equals(bindType)) {
			accountEt.setHint("请输入您的手机号或邮箱");
		}
		
		accountEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		accountEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		accountEt.setSingleLine(true); 
		
		//.............................................隐私条款........................................................
		TextView clauseView = new TextView(context);
		RelativeLayout.LayoutParams clauseViewParams=new RelativeLayout.LayoutParams((int)(300*scale),(int)(100*scale));
		clauseViewParams.leftMargin = (int) (113*scale);
		clauseViewParams.topMargin=(int) (290*scale);
		clauseView.setLayoutParams(clauseViewParams);
		clauseView.setText("隐私条款");
		clauseView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		clauseView.setTextColor(Color.GRAY);
		clauseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开隐私条款activity，加载网页
				showClauseActivity();
			}
		});
		//............................................确定..........................................................
		registerView = new TextView(context);
		RelativeLayout.LayoutParams registerViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		registerViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		registerViewParams.topMargin=(int) (470*scale);
		registerView.setLayoutParams(registerViewParams);
		Drawable registerBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.LOGINORREGISTER);//注册按钮图片
		registerView.setBackgroundDrawable(registerBg);
		registerView.setText("确定");
		registerView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		registerView.setTextColor(Color.WHITE);
		registerView.setGravity(Gravity.CENTER);//文字居中
		registerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String s = accountEt.getText().toString();
				// 客户端判断账号是否符合规则
				accountString = s;
				//  绑定格式错误提示
				if ("phone".equals(bindType) && TextUtils.isEmpty(accountString)) {
					ToastUtil.showShort(context, "请输入手机号");
					return;
				}
				if ("email".equals(bindType) && TextUtils.isEmpty(accountString)) {
					ToastUtil.showShort(context, "请输入邮箱");
					return;
				}
				if ("phone".equals(bindType) && !StringMatchUtil.isPhone(accountString)) {
					ToastUtil.showShort(context, "手机号格式错误");
					return;
				}
				
				if ("email".equals(bindType) && !StringMatchUtil.isEmail(accountString)) {
					ToastUtil.showShort(context, "邮箱格式错误");
					return;
				}
				if ("phoneOrEmail".equals(bindType) && TextUtils.isEmpty(accountString)) {
					ToastUtil.showShort(context, "请输入手机号或邮箱");
					return;
				}
				if ("phoneOrEmail".equals(bindType) && !StringMatchUtil.isPhone(accountString) && !StringMatchUtil.isEmail(accountString)) {
					ToastUtil.showShort(context, "格式错误");
					return;
				}
				
				if ("phone".equals(bindType) && StringMatchUtil.isPhone(accountString)) {
					//联网判断账号可用性。主要判断当前账号是否注册过
					AccountBindUsableRequest accountBindUsableRequest = new AccountBindUsableRequest(context, loginListener,BindPopWindow.this, accountString,accountDBBean, "BindPopWindow",new FLOnNetFinishListener() {
						
						@Override
						public void OnNetComplete(Message msg) {
							registerView.setClickable(true);
							if (null != progressDialog) {
								progressDialog.dismiss();
							}
						}
					});
					accountBindUsableRequest.post();
					registerView.setClickable(false);//防止连续点击
					showProgressDialog();
				}else if ("email".equals(bindType) && StringMatchUtil.isEmail(accountString)){
					//联网判断账号可用性。主要判断当前账号是否注册过
					AccountBindUsableRequest accountBindUsableRequest = new AccountBindUsableRequest(context, loginListener,BindPopWindow.this, accountString,accountDBBean, "BindPopWindow",new FLOnNetFinishListener() {
						
						@Override
						public void OnNetComplete(Message msg) {
							registerView.setClickable(true);
							if (null != progressDialog) {
								progressDialog.dismiss();
							}
						}
					});
					accountBindUsableRequest.post();
					registerView.setClickable(false);
					showProgressDialog();
				}else if ("phoneOrEmail".equals(bindType)) {
					if (StringMatchUtil.isPhone(accountString) || StringMatchUtil.isEmail(accountString)) {
						//联网判断账号可用性。主要判断当前账号是否注册过
						AccountBindUsableRequest accountBindUsableRequest = new AccountBindUsableRequest(context, loginListener,BindPopWindow.this, accountString,accountDBBean, "BindPopWindow",new FLOnNetFinishListener() {
							
							@Override
							public void OnNetComplete(Message msg) {
								registerView.setClickable(true);
								if (null != progressDialog) {
									progressDialog.dismiss();
								}
							}
						});
						accountBindUsableRequest.post();
						registerView.setClickable(false);
						showProgressDialog();
					}
				}else {
					ToastUtil.showShort(context,"格式错误");
				}
				
			}

			
		});
		
		
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(accountEt);
		retAbsoluteLayout.addView(registerView);
		retAbsoluteLayout.addView(clauseView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}
	/**
	 * 展示联网等待对话框
	 */
	private void showProgressDialog() {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);//白色主题
		}
		
		progressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消 
		
		progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条  
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		progressDialog.setMessage("请稍候...");
		progressDialog.show();
		//设置参数一定要在show之后，因为只有显示出来后才能计算出宽高信息
		WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
		params.alpha = 1.0f;//这是对话框背景的透明度
		params.width = (int) (800*scale);
		params.height = (int) (400*scale);
		params.dimAmount = 0f;//这是全屏背景的透明度
		progressDialog.getWindow().setAttributes(params);
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
		MyLogger.lczLog().i("展示：BindPopWindow");
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
