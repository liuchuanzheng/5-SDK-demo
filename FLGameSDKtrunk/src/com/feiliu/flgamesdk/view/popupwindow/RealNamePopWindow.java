package com.feiliu.flgamesdk.view.popupwindow;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.FlViewToastUtils;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 实名认证界面
 * 
 * @author 刘传政
 * 
 */
public class RealNamePopWindow extends BasePopWindow implements
		OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText realNameET;
	private EditText idCardET;
	private AccountDao accountDao;
	private AccountDBBean accountDBBean;// 当前登录账号的信息
	private OnDismissListener lastPopOnDismissListener;
	private boolean hasBackButton;
	private boolean hasCloseButton;

	public RealNamePopWindow(Context context, FLOnLoginListener loginListener,
			OnDismissListener onDismissListener, boolean hasBackButton,
			boolean hasCloseButton) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		lastPopOnDismissListener = onDismissListener;
		this.hasBackButton = hasBackButton;
		this.hasCloseButton = hasCloseButton;
		accountDao = new AccountDao(context);
		List<AccountDBBean> findAllIncludeGuest = accountDao
				.findAllIncludeGuest();
		accountDBBean = findAllIncludeGuest.get(0);
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面
	 * 
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				(int) (designWidth * scale), (int) (designHeight * scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.WINDOWBG);// 得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		// ..........................................title............................................................
		BaseBarView baseBarView = new BaseBarView(context, "实名认证",
				hasBackButton, hasCloseButton, this);
		RelativeLayout.LayoutParams baseBarViewParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (100 * scale));
		baseBarViewParams.topMargin = (int) (0 * scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		// .............................................姓名........................................................
		realNameET = new FlEditText(context);
		RelativeLayout.LayoutParams realNameETParams = new RelativeLayout.LayoutParams(
				(int) (700 * scale), (int) (100 * scale));
		realNameETParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 居中
		realNameETParams.topMargin = (int) (185 * scale);
		realNameET.setLayoutParams(realNameETParams);
		Drawable realNameETBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.TEXTAREA);// 方形边界框
		realNameET.setBackgroundDrawable(realNameETBg);
		realNameET.setHint("请输入真实姓名");
		realNameET.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		realNameET.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		realNameET.setSingleLine(true);
		// .............................................身份证号........................................................
		idCardET = new FlEditText(context);
		RelativeLayout.LayoutParams idCardETParams = new RelativeLayout.LayoutParams(
				(int) (700 * scale), (int) (100 * scale));
		idCardETParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 居中
		idCardETParams.topMargin = (int) (320 * scale);
		idCardET.setLayoutParams(idCardETParams);
		idCardET.setBackgroundDrawable(realNameETBg);
		idCardET.setHint("请输入身份证号");
		idCardET.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		idCardET.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		idCardET.setSingleLine(true);
		// ............................................确定..........................................................
		TextView OKView = new TextView(context);
		RelativeLayout.LayoutParams OKViewParams = new RelativeLayout.LayoutParams(
				(int) (700 * scale), (int) (110 * scale));
		OKViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 居中
		OKViewParams.topMargin = (int) (470 * scale);
		OKView.setLayoutParams(OKViewParams);
		Drawable registerBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.LOGINORREGISTER);
		OKView.setBackgroundDrawable(registerBg);
		OKView.setText("确定");
		OKView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		OKView.setTextColor(Color.WHITE);
		OKView.setGravity(Gravity.CENTER);// 文字居中
		OKView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进行客户端校验,并进行错误提示 先姓名后身份证号
				boolean isRealName = checkRealName();
				if (isRealName) {
					boolean isIdCard = checkIdCard();
					if (isIdCard) {
						// 校验通过后,联网发送实名认证请求
						realNameNetRequest();
					}
				}

			}
		});

		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(realNameET);
		retAbsoluteLayout.addView(idCardET);
		retAbsoluteLayout.addView(OKView);
		// 添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE); // 居中
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}

	/**
	 * 联网走实名认证的协议
	 */
	protected void realNameNetRequest() {
		String userId = accountDBBean.getUserId();
		final JSONObject holder = new JSONObject();
		JSONObject realNameAuthInfoJsonObject = new JSONObject();
		try {
			realNameAuthInfoJsonObject.put("userId", userId);
			realNameAuthInfoJsonObject.put("realName", realNameET.getText()
					.toString().trim());
			realNameAuthInfoJsonObject.put("idCardNumber", idCardET.getText()
					.toString().trim());
			realNameAuthInfoJsonObject.put("version", "v5");
			holder.put("actionId", "6000");
			holder.put("realNameAuthInfo", realNameAuthInfoJsonObject);
			MyLogger.lczLog().i("实名认证请求参数：" + holder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 创建一个OkHttpClient对象
		OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new RequestBody() {
			@Override
			public MediaType contentType() {
				return MediaType.parse("application/json;charset=UTF-8");
			}

			@Override
			public void writeTo(BufferedSink sink) throws IOException {
				sink.writeUtf8(holder.toString());
			}

		};

		Request request = new Request.Builder().url(URLConstant.ACCOUNT_URL)
				.post(requestBody).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				String s = arg1.body().string();
				MyLogger.lczLog().i("实名认证返回参数:" + s);
				String jsonString = s.substring(7);
				JSONObject responseJsonObject;
				try {
					responseJsonObject = new JSONObject(jsonString);
					String code = responseJsonObject.getJSONObject("result")
							.getString("code");
					final String tips = responseJsonObject.getJSONObject("result")
							.getString("tips");
					if (!"0".equals(code)) {
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ToastUtil.showLong(context, tips);
							}
						});
						return;
					} else if ("0".equals(code)) {
						SPUtils spUtils = new SPUtils(context,
								SPConstant.USERINFO);
						spUtils.putBoolean("realName", true);
						String opcode = responseJsonObject.getJSONObject("result").getString("opcode");
						SPUtils spUtils1 = new SPUtils(context, SPConstant.USERINFO);
						spUtils1.putString("ageStatus", opcode);
						// 请求成功后弹出相应实名认证成功提示,更新对应各种小圆点的显示与隐藏.
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mPopupWindow.dismiss();
								FlViewToastUtils.show(context, tips, 3);
								//广播通知飞流球显示出来
								sendBroadcast(context,"com.feiliu.flgamesdk.HasBindPhoneReceiver","bindphone","realNameCompleted");  
							}
						});

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
			}
		});
	}
	
	/**
	 * 发送广播
	 * @param context
	 * @param action
	 * @param name
	 * @param value
	 */
	private void sendBroadcast(Context context,String action,String name,String value) {
		Intent intent = new Intent();
        intent.setAction(action); 
        intent.putExtra(name, value);
        //发送广播
        context.sendBroadcast(intent);
	}

	/**
	 * 校验身份证并进行相应提示
	 * 
	 * @return
	 */
	protected boolean checkIdCard() {
		String idCard = idCardET.getText().toString().trim();
		// 不能为空
		if (TextUtils.isEmpty(idCard)) {
			ToastUtil.showLong(context, "请输入身份证号");
			return false;
		}
		// 字数限制为18位
		if (idCard.length() != 18) {
			ToastUtil.showLong(context, "身份证号不正确，请重新输入");
			return false;
		}
		//出生日期年份只能以19或20开头
		String year = idCard.substring(6, 8);
		if (!"19".equals(year) && !"20".equals(year)) {
			ToastUtil.showLong(context, "身份证号不正确，请重新输入");
			return false;
		}
		// 判断最后一位格式
		String lastNum = idCard.substring(17, 18);
		Pattern pattern = Pattern.compile("[0-9Xx]");
		Matcher isNum = pattern.matcher(lastNum);
		if (!isNum.matches()) {
			ToastUtil.showLong(context, "身份证号不正确，请重新输入");
			return false;
		}
		return true;
	}

	/**
	 * 校验姓名,并进行相应提示
	 * 
	 * @return
	 */
	protected boolean checkRealName() {
		String realName = realNameET.getText().toString().trim();
		// 不能为空
		if (TextUtils.isEmpty(realName)) {
			ToastUtil.showLong(context, "请输入真实姓名");
			return false;
		}
		// 字数限制为2-20个汉字
		if (realName.length() < 2 || realName.length() > 20) {
			ToastUtil.showLong(context, "姓名格式不正确，请重新输入");
			return false;
		}
		return true;
	}

	public void showWindow() {
		MyLogger.lczLog().i("展示：RealNamePopWindow");
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
	 * 
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
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);// pop弹出动画
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
		lastPopOnDismissListener.onDismiss();// 通知上一个pop
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
	}

}
