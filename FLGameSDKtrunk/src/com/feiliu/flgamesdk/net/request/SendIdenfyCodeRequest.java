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

import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;


public class SendIdenfyCodeRequest extends BaseReuest{
	private String accountString;
	
	public SendIdenfyCodeRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow,String accountString) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountString = accountString;
	}


	@Override
	public void sendHandlerMessage(int mWhat, Object obj) {
	}

	
	//...........................................................................................................
	
	/**
	 * 联网发送验证码
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
			holder.put("actionId", "3000");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("verifyType", "0");
			userParamHolder.put("userName", accountString);
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("发送验证码请求参数："+holder.toString());
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
						MyLogger.lczLog().i("发送验证码请求成功："+s);
						String jsonString = s.substring(7);
						
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						MyLogger.lczLog().i("发送验证码请求失败"+arg1);
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		
	}
}
