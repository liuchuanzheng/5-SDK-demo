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

import com.feiliu.flgamesdk.bean.netresultbean.ResetPasswordResultJsonBean;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


/**
 * 重置密码的网络请求4011
 * @author 刘传政
 *
 */
public class ResetPasswordRequest extends BaseReuest{
	private String accountString;
	private String code;
	private String password;
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.RESETPASSWORD_SUCCESS:
				ResetPasswordResultJsonBean bean= (ResetPasswordResultJsonBean) msg.obj;
				if (bean.getResult().getCode().equals("0")) {
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					ToastUtil.showShort(context,"重置密码成功");
					NormalLoginRequest normalLoginRequest = new NormalLoginRequest(context, loginListener
							,false, bean.getUserInfo().getUserName(),bean.getUserInfo().getPassword(),null);
					normalLoginRequest.post();
					break;
					
				}else {
					ToastUtil.showShort(context,bean.getResult().getTips());
				}
				
			case HandlerConstant.RESETPASSWORD_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
			
		}
		
	};
	
	
	
	public ResetPasswordRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow
			,String accountString,String code,String password) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountString = accountString;
		this.code = code;
		this.password = password;
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
	 * 重置密码
	 */
	public void post() {
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "4011");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("password", password);
			userParamHolder.put("verifyCode", code);
			userParamHolder.put("userName", accountString);
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("重置密码请求参数："+holder.toString());
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
						MyLogger.lczLog().i("重置密码请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						ResetPasswordResultJsonBean bean = gson.fromJson(jsonString, ResetPasswordResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.RESETPASSWORD_SUCCESS, bean);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						MyLogger.lczLog().i("重置密码请求失败"+arg1);
						sendHandlerMessage(HandlerConstant.RESETPASSWORD_FAIL, "");
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		
	}
}
