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

import com.feiliu.flgamesdk.bean.netresultbean.BindResultJsonBean;
import com.feiliu.flgamesdk.bean.netresultbean.ResetPasswordResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.FlViewToastUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


/**
 * 绑定账号4001
 * @author 刘传政
 *
 */
public class BindRequest extends BaseReuest{
	private AccountDBBean accountDBBean;//当前登录账号的信息
	private String accountString;
	private String code;
	private String password;
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.BIND_SUCCESS:
				BindResultJsonBean bean= (BindResultJsonBean) msg.obj;
				if (bean.getResult().getCode().equals("0")) {
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					//ToastUtil.showShort(context,"绑定成功");
					FlViewToastUtils.show(context, "绑定成功",3);//顶部提示
					//  将绑定成功后的新数据写入数据库
					AccountDao accountDao = new AccountDao(context);
					String lastPassword = "";
					if ("1".equals(accountDBBean.getUserType())) {
						//正常账号绑定
						lastPassword = accountDBBean.getPassword();
					}else if ("0".equals(accountDBBean.getUserType())) {
						//游客账号绑定
						lastPassword = password;
						sendBroadcast(context,"com.feiliu.flgamesdk.BindStatusReceiver","bindstatus","finish");  
					}
					if (null != accountDBBean.getUserId()) {
						accountDao.update(accountDBBean.getUserId(),bean.getUserInfo().getUserName(), lastPassword
								,bean.getUserInfo().getNickName(),bean.getUserInfo().getPhone(),bean.getUserInfo().getEmail()
								,"1","login", System.currentTimeMillis());
					}else {
						accountDao.add(bean.getUserInfo().getUserId()+"",bean.getUserInfo().getUserName(), lastPassword
								,bean.getUserInfo().getNickName(),bean.getUserInfo().getPhone(),bean.getUserInfo().getEmail()
								,"1","login",System.currentTimeMillis());
					}
					sendBroadcast(context,"com.feiliu.flgamesdk.HasBindPhoneReceiver","bindphone","yes");  
					break;
					
				}else {
					ToastUtil.showShort(context,bean.getResult().getTips());
					if ("0".equals(accountDBBean.getUserType())) {
						sendBroadcast(context,"com.feiliu.flgamesdk.BindStatusReceiver","bindstatus","finish");  
					}
					sendBroadcast(context,"com.feiliu.flgamesdk.HasBindPhoneReceiver","bindphone","yes");  
					break;
				}
				
			case HandlerConstant.BIND_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
			
		}
		
	};
	
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
	
	public BindRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow
			,String accountString,String code,String passWord, AccountDBBean accountDBBean) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountString = accountString;
		this.code = code;
		this.password = passWord;
		this.accountDBBean = accountDBBean;
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
	 * 绑定
	 */
	public void post() {
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "4001");
			JSONObject userParamHolder = new JSONObject();
			userParamHolder.put("userId", accountDBBean.getUserId());
			userParamHolder.put("userName", accountDBBean.getUserName());
			if (!"".equals(password)) {
				userParamHolder.put("password", password);
			}
			if (!"".equals(password)) {
				userParamHolder.put("newPassword", password);	
			}
			userParamHolder.put("verifyCode", code);
			userParamHolder.put("bindName", accountString);
			userParamHolder.put("userType", accountDBBean.getUserType());
			holder.put("userParam", userParamHolder);
			MyLogger.lczLog().i("绑定请求参数："+holder.toString());
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
						MyLogger.lczLog().i("绑定请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						BindResultJsonBean bean = gson.fromJson(jsonString, BindResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.BIND_SUCCESS, bean);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						MyLogger.lczLog().i("绑定请求失败"+arg1);
						sendHandlerMessage(HandlerConstant.BIND_FAIL, "");
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		
	}
}
