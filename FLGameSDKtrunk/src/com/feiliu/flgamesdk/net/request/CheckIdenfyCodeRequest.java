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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.PopupWindow.OnDismissListener;

import com.feiliu.flgamesdk.bean.netresultbean.checkIdenfyCodeResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.BindPasswordPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.Register3PopWindow;
import com.feiliu.flgamesdk.view.popupwindow.ResetPasswordPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


public class CheckIdenfyCodeRequest extends BaseReuest{
	private AccountDBBean accountDBBean;//当前登录账号的信息
	private String accountString;
	private String code;
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.CHECKIDENFYCODE_SUCCESS:
				checkIdenfyCodeResultJsonBean bean= (checkIdenfyCodeResultJsonBean) msg.obj;
				if (bean.getResult().getCode().equals("0")) {
					
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					ToastUtil.showShort(context, "验证码校验成功");
					if (popFrom.equals("IdentifyCodeCheckPopWindow")) {
						//来源于找回密码
						//打开重置密码界面
						new ResetPasswordPopWindow(context, accountString,code, loginListener, new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								basePopWindow.showWindow();
							}
						});
					}else if (popFrom.equals("Register2PopWindow")){
						//  打开注册三级界面   主要是输入密码进行联网验证
						new Register3PopWindow(context, accountString, code, loginListener, new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								basePopWindow.showWindow();
								
							}
						});
					}else if(popFrom.equals("BindIdenfyCodePopWindow")){
						//区分绑定来源。游客绑定需要弹出设置密码界面，正常账号绑定直接进行绑定请求
						if ("0".equals(accountDBBean.getUserType())) {
							//来源于游客账号
							new BindPasswordPopWindow(context,accountDBBean, accountString, code, loginListener, new OnDismissListener() {
								
								@Override
								public void onDismiss() {
									if (basePopWindow != null) {
										basePopWindow.showWindow();
									}
								}
							});
						}else if ("1".equals(accountDBBean.getUserType())) {
							//来源于正常账号
							BindRequest bindRequest = new BindRequest(context, loginListener, basePopWindow, accountString, code,"", accountDBBean);
							bindRequest.post();
						}
					}
					
					break;
				}else {
					ToastUtil.showShort(context,bean.getResult().getTips());
					break;
				}
				
			case HandlerConstant.CHECKIDENFYCODE_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
			
		}
		
	};
	
	
	public CheckIdenfyCodeRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow
			,AccountDBBean accountDBBean, String accountString,String code,String popFrom) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountDBBean = accountDBBean;
		this.accountString = accountString;
		this.code = code;
		this.popFrom = popFrom;
		
	}


	@Override
	public void sendHandlerMessage(int mWhat, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	
	//...........................................................................................................
	/**
	 * 联网校验验证码
	 */
	@Override
	public void post() {
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "3001");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("verifyCode", code);
			userParamHolder.put("userName", accountString);
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("验证验证码请求参数："+holder.toString());
			new Thread(new Runnable() {
				
				@Override
				public void run() {
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
				            .url(URLConstant.ACCOUNT_URL)
				            .post(requestBody)
				            .build();

				  okHttpClient.newCall(request).enqueue(new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						
						String s = arg1.body().string();
						MyLogger.lczLog().i("验证验证码请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						checkIdenfyCodeResultJsonBean bean = gson.fromJson(jsonString, checkIdenfyCodeResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.CHECKIDENFYCODE_SUCCESS, bean);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						MyLogger.lczLog().i("验证验证码请求失败"+arg1);
						sendHandlerMessage(HandlerConstant.CHECKIDENFYCODE_SUCCESS, "");
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
