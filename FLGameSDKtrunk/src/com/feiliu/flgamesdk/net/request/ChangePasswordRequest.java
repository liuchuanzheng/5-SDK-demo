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
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.feiliu.flgamesdk.bean.netresultbean.ChangePasswordResultJsonBean;
import com.feiliu.flgamesdk.bean.netresultbean.ResetPasswordResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


/**
 * 修改密码的网络请求4010
 * @author 刘传政
 *
 */
public class ChangePasswordRequest extends BaseReuest{
	private String newPassword;
	private AccountDBBean accountDBBean;
	private String popFrom;
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.CHANGEPASSWORD_SUCCESS:
				ChangePasswordResultJsonBean bean=(ChangePasswordResultJsonBean) msg.obj;
				if (bean.getResult().getCode().equals("0")) {
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					ToastUtil.showShort(context,bean.getResult().getTips());
					//更改数据库
					AccountDao accountDao = new AccountDao(context);
					accountDao.update(accountDBBean.getUserId(),bean.getUserInfo().getUserName(), newPassword
							,accountDBBean.getNickName(),accountDBBean.getPhone(),accountDBBean.getEmail(),
							"1","login", System.currentTimeMillis());
					sendBroadcast(context,"com.feiliu.flgamesdk.HasBindPhoneReceiver","bindphone","yes");  
					break;
					
				}else {
					ToastUtil.showShort(context,bean.getResult().getTips());
					break;
				}
				
			case HandlerConstant.CHANGEPASSWORD_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
			
		}
		
	};
	
	
	
	
	
	public ChangePasswordRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow
			,AccountDBBean accountDBBean,String newPassword,String popFrom) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountDBBean = accountDBBean;
		this.newPassword = newPassword;
		this.popFrom = popFrom;
	}


	@Override
	public void sendHandlerMessage(int mWhat, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = obj;
		mHandler.sendMessage(msg);
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
	
	//...........................................................................................................
	/**
	 * 修改密码
	 */
	public void post() {
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "4010");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("userName", accountDBBean.getUserName());
			userParamHolder.put("password", accountDBBean.getPassword());
			userParamHolder.put("newPassword", newPassword);
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("修改密码请求参数："+holder.toString());
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
						MyLogger.lczLog().i("修改密码请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						ChangePasswordResultJsonBean bean = gson.fromJson(jsonString, ChangePasswordResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.CHANGEPASSWORD_SUCCESS, bean);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						MyLogger.lczLog().i("修改密码请求失败"+arg1);
						sendHandlerMessage(HandlerConstant.CHANGEPASSWORD_FAIL, "");
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		
	}
}
