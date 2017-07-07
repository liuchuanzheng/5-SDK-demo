package com.feiliu.flgamesdk.view.popupwindow;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.listener.FLOnNetFinishListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.AccountFindPasswordUsableRequest;
import com.feiliu.flgamesdk.net.request.GetAccountUsableRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 找回密码界面
 * @author 刘传政
 *
 */
public class FindPasswordPopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText accountEt;//账号输入框
	private String accountString = "";//账号输入框的内容。保证可用后才会赋值
	public TextView OKView;
	private ProgressDialog progressDialog = null;
	private OnDismissListener lastPopOnDismissListener;
	public FindPasswordPopWindow(Context context, String userName,
			String password,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener) {
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
		BaseBarView baseBarView = new BaseBarView(context, "找回密码", true,false, this);
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
		accountEt.setHint("请输入您的手机号或邮箱");
		accountEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		accountEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		accountEt.setSingleLine(true);  
		
		//............................................确定..........................................................
		OKView = new TextView(context);
		RelativeLayout.LayoutParams OKViewParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(110*scale));
		OKViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		OKViewParams.topMargin=(int) (470*scale);
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
				String s = accountEt.getText().toString();
				//  客户端判断账号是否符合规则
				accountString = s;
				if (StringMatchUtil.isEmail(accountString) || StringMatchUtil.isPhone(accountString)) {//静态判断账号合法性
					//   判断账号可用性
					AccountFindPasswordUsableRequest accountFindPasswordUsableRequest = new AccountFindPasswordUsableRequest(context, loginListener, FindPasswordPopWindow.this, accountString,new FLOnNetFinishListener() {
						
						@Override
						public void OnNetComplete(Message msg) {
							OKView.setClickable(true);
							if (null != progressDialog) {
								progressDialog.dismiss();
							}
						}
					});
					accountFindPasswordUsableRequest.post();
					OKView.setClickable(false);
					showProgressDialog();
				}else {
					if (accountString.equals("")) {
						ToastUtil.showShort(context,"用户名不能为空");
						return;
					}
					ToastUtil.showShort(context,"只能是手机号或邮箱");
				}
				
			}
		});
		
		
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(accountEt);
		retAbsoluteLayout.addView(OKView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}
	


	public void showWindow() {
		MyLogger.lczLog().i("展示：FindPasswordPopWindow");
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
