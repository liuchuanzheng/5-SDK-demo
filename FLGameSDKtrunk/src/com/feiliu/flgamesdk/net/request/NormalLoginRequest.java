package com.feiliu.flgamesdk.net.request;

import java.io.IOException;

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
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.bean.netresultbean.NormalLoginResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.FlToolBarControler;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.LoginStatus;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.ExToast;
import com.feiliu.flgamesdk.utils.FlViewToastUtils;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.LoginFailPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.RealNameForceTipsPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.RealNameTipsPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;

public class NormalLoginRequest extends BaseReuest {
	private String accountString;
	private String password;
	private Boolean isRead;// 记录密码是否是本地读取出来的
	private float scale;
	private ExToast exToast;// 登录进度
	private boolean isHideProgress = false;// 登录进度是否消失
	private long startTime = 0;
	private long endTime;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {

		private NormalLoginResultJsonBean bean;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.NORMALLOGIN_SUCCESS:
				bean = (NormalLoginResultJsonBean) msg.obj;

				if (bean.getResult().getCode().equals("0")) {
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					// 登录成功后存储用户数据，便于之后使用
					// 将账户信息保存在数据库，便于账号历史显示使用
					AccountDao accountDao = new AccountDao(context);
					// 由于qa后台清账号后，会导致客户端出现两次历史记录。故想法删掉清账号之前的记录。
					if (null != bean.getUserInfo().getPhone()) {
						AccountDBBean qaBean1 = accountDao.findByPhone(bean
								.getUserInfo().getPhone());
						if (null != qaBean1.getPhone()) {
							accountDao.updateByPhone(bean.getUserInfo()
									.getUserId() + "", bean.getUserInfo()
									.getPhone());
						}
					}
					if (null != bean.getUserInfo().getEmail()) {
						AccountDBBean qaBean2 = accountDao.findByEmail(bean
								.getUserInfo().getEmail());
						if (null != qaBean2.getPhone()) {
							accountDao.updateByEmail(bean.getUserInfo()
									.getUserId() + "", bean.getUserInfo()
									.getEmail());
						}
					}

					AccountDBBean accountDBBean = accountDao.find(bean
							.getUserInfo().getUserId() + "");
					if (null != accountDBBean.getUserId()) {
						accountDao.update(accountDBBean.getUserId(), bean
								.getUserInfo().getUserName(), bean
								.getUserInfo().getPassword(), bean
								.getUserInfo().getNickName(), bean
								.getUserInfo().getPhone(), bean.getUserInfo()
								.getEmail(), "1", "login", System
								.currentTimeMillis());
					} else {
						accountDao.add(bean.getUserInfo().getUserId() + "",
								bean.getUserInfo().getUserName(), bean
										.getUserInfo().getPassword(), bean
										.getUserInfo().getNickName(), bean
										.getUserInfo().getPhone(), bean
										.getUserInfo().getEmail(), "1",
								"login", System.currentTimeMillis());
					}
					LoginStatus.status = true;// 内存标记当前账户登录状态
					loginListener.onLoginComplete(bean.getUserInfo()
							.getUserId() + "", bean.getUserInfo().getSign()
							.getCode(), bean.getUserInfo().getSign()
							.getCurrentTimeSeconds()
							+ "");
					// 顶部提示
					/*Toast mToast = Toast.makeText(context,"欢迎回来 "+bean.getUserInfo().getNickName(), Toast.LENGTH_LONG);
					mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
					mToast.show();*/
					if (isHideProgress) {
						FlViewToastUtils.show(context, "欢迎回来 "
								+ bean.getUserInfo().getUserName(), 3);

					}
					realNameForceRequest(bean.getUserInfo().getUserId()+"");
					break;
				} else {
					String opcode = bean.getResult().getOpcode();//只有部分错误的时候才返回opcode
					// 区分是账号回显时密码错误还是手动输密码登录错误，而弹出不同的提示
					if (isRead && !"40031".equals(opcode)) {
						//是读取的密码且游戏未停用
						if (basePopWindow != null) {
							basePopWindow.mPopupWindow.dismiss();
						}
						// 联网成功但是登录失败（主要原因是密码错误），弹出账号安全提示
						new LoginFailPopWindow(context, accountString, "",
								loginListener, null);
					} else {
						// 弹出错误提示信息
						ToastUtil
								.showShort(context, bean.getResult().getTips());
					}

				}

				break;

			case HandlerConstant.NORMALLOGIN_FAIL:
				ToastUtil.showLong(context, "当前设备无网络，请检查网络设置");
				loginListener.onLoginFailed();
				break;
			case HandlerConstant.NORMALLOGINDELAY:
				exToast.hide();
				isHideProgress = true;
				if (null != bean && bean.getResult().getCode().equals("0")) {
					FlViewToastUtils.show(context, "欢迎回来 "
							+ bean.getUserInfo().getUserName(), 3);
				}
				break;
			}

		}

	};

	public NormalLoginRequest(Context context, FLOnLoginListener loginListener,
			Boolean isRead, String accountString, String password,
			BasePopWindow basePopWindow) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.isRead = isRead;
		this.accountString = accountString;
		this.password = password;
		this.basePopWindow = basePopWindow;
		scale = UiSizeUtil.getScale(this.context);
	}

	/**
	 *  联网请求此账号是否需要强制实名制认证
	 * @param userId
	 *           
	 */
	protected void realNameForceRequest(String userId) {
		try {
			final JSONObject holder = new JSONObject();
			final JSONObject realNameAuthInfoJsonObject = new JSONObject();
			realNameAuthInfoJsonObject.put("userId", userId);
			realNameAuthInfoJsonObject.put("version", "v5");
			holder.put("actionId", "6001");
			holder.put("realNameAuthInfo", realNameAuthInfoJsonObject);
			MyLogger.lczLog().i("realNameForceRequest请求参数" + holder.toString());
			// 创建一个OkHttpClient对象
			OkHttpClient okHttpClient = new OkHttpClient();
			RequestBody requestBody = new RequestBody() {
				@Override
				public MediaType contentType() {
					return MediaType.parse("text/x-markdown; charset=utf-8");
				}

				@Override
				public void writeTo(BufferedSink sink) throws IOException {
					sink.writeUtf8(holder.toString());
				}

			};

			Request request = new Request.Builder()
					.url(URLConstant.ACCOUNT_URL).post(requestBody).build();

			okHttpClient.newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response arg1)
						throws IOException {
					String s = arg1.body().string();
					MyLogger.lczLog().i("realNameForceRequest请求成功：" + s);
					String jsonString = s.substring(7);
					try {
						JSONObject responseJsonObject = new JSONObject(jsonString);
						String code = responseJsonObject.getJSONObject("result").getString("code");
						if (!"0".equals(code)) {
							MyLogger.lczLog().i("realNameForceRequest" + "返回异常");
							return;
						}
						String opcode = responseJsonObject.getJSONObject("result").getString("opcode");
						SPUtils spUtils1 = new SPUtils(context, SPConstant.USERINFO);
						spUtils1.putString("ageStatus", opcode);
						final String realNameAuth = responseJsonObject.getJSONObject("result").getString("realNameAuth");
						((Activity)context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								FlToolBarControler.show();//刷新一下飞流球个状态
								if ("-1".equals(realNameAuth)) {
									//隐藏飞流球
									FlToolBarControler.hide();
									MyLogger.lczLog().i("隐藏飞流球");
									new RealNameForceTipsPopWindow(context, null, null);
									//记录当前账号未实名认证
									SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
									spUtils.putBoolean("realName", false);
								}else if ("0".equals(realNameAuth)){
									//隐藏飞流球
									FlToolBarControler.hide();
									MyLogger.lczLog().i("隐藏飞流球");
									new RealNameTipsPopWindow(context, null, null);
									//记录当前账号未实名认证
									SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
									spUtils.putBoolean("realName", false);
								}else if ("1".equals(realNameAuth)) {
									//记录当前账号是已经实名注册的
									SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
									spUtils.putBoolean("realName", true);
								}
								//sendBroadcast(context, "com.feiliu.flgamesdk.ToolBarReceiver", "toolBarAction", "refrush");
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}

					
				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					MyLogger.lczLog().i("realNameForceRequest请求失败" + arg1);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void sendHandlerMessage(int mWhat, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	/**
	 * 创建界面ui并设置其中的点击事件等
	 * 
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) (100 * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		// ......................................提示语.............................................................
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(50 * scale);
		shape.setColor(Color.WHITE);
		TextView tips = new TextView(context);
		RelativeLayout.LayoutParams tipsParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) (100 * scale));
		tipsParams
				.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		tips.setLayoutParams(tipsParams);
		Drawable logoIvBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.ROUNDLOGO);
		tips.setBackgroundDrawable(shape);
		logoIvBg.setBounds(0, 0, (int) (100 * scale), (int) (100 * scale));
		tips.setCompoundDrawables(logoIvBg, null, null, null);
		tips.setText("账号登录中…");
		tips.setPadding((int) (0 * scale), (int) (0 * scale),
				(int) (50 * scale), (int) (0 * scale));
		tips.setTextColor(Color.BLACK);
		tips.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		tips.setSingleLine(true);
		tips.setGravity(Gravity.CENTER);// 文字居中
		totalLayout.addView(tips);
		return totalLayout;
	}

	// ...........................................................................................................
	/**
	 * 联网普通登录
	 */
	public void post() {
		showProgress();
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "4003");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("password", password);
			userParamHolder.put("userName", accountString);
			// 根据情况添加firstLogin字段
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("普通登录请求参数：" + holder.toString());
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 创建一个OkHttpClient对象
					OkHttpClient okHttpClient = new OkHttpClient();
					RequestBody requestBody = new RequestBody() {
						@Override
						public MediaType contentType() {
							return MediaType
									.parse("text/x-markdown; charset=utf-8");
						}

						@Override
						public void writeTo(BufferedSink sink)
								throws IOException {
							sink.writeUtf8(holder.toString());
						}

					};

					Request request = new Request.Builder()
							.url(URLConstant.ACCOUNT_URL).post(requestBody)
							.build();

					okHttpClient.newCall(request).enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1)
								throws IOException {
							hideProgress();
							String s = arg1.body().string();
							MyLogger.lczLog().i("普通登录请求成功：" + s);
							String jsonString = s.substring(7);
							Gson gson = new Gson();
							NormalLoginResultJsonBean bean = gson
									.fromJson(jsonString,
											NormalLoginResultJsonBean.class);
							sendHandlerMessage(
									HandlerConstant.NORMALLOGIN_SUCCESS, bean);
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							hideProgress();
							MyLogger.lczLog().i("普通登录请求失败" + arg1);
							sendHandlerMessage(
									HandlerConstant.NORMALLOGIN_FAIL, "");
						}
					});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 展示登录进度提示
	 */
	private void showProgress() {
		View view = createMyUi();
		exToast = new ExToast(context);
		exToast.setDuration(5);// 5秒
		exToast.setView(view);
		exToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		exToast.show();
		startTime = System.currentTimeMillis();
	}

	/**
	 * 隐藏进度提示
	 */
	private void hideProgress() {
		endTime = System.currentTimeMillis();
		long betweenTime = endTime - startTime;
		if (betweenTime >= 1000) {
			if (exToast != null) {
				exToast.hide();
				isHideProgress = true;
			}
		} else {
			Message msg = mHandler.obtainMessage();
			msg.what = HandlerConstant.NORMALLOGINDELAY;
			mHandler.sendMessageDelayed(msg, 1000 - betweenTime);
		}
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
}
