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

import com.feiliu.flgamesdk.bean.netresultbean.GetAccountUsableResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.listener.FLOnNetFinishListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.StringMatchUtil;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.BindIdenfyCodePopWindow;
import com.feiliu.flgamesdk.view.popupwindow.BindPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.Register2PopWindow;
import com.feiliu.flgamesdk.view.popupwindow.Register3PopWindow;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


/**
 * @author 刘传政
 * 判断绑定账号时，手机或邮箱是否可用
 *
 */
public class AccountBindUsableRequest extends BaseReuest{
	private String accountString;
	private AccountDBBean accountDBBean;//当前登录账号的信息
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			if (null != fLOnNetFinishListener) {
				fLOnNetFinishListener.OnNetComplete(msg);
			}
			switch (msg.what) {
			case HandlerConstant.CHECKACCOUNT_SUCCESS:
				GetAccountUsableResultJsonBean bean= (GetAccountUsableResultJsonBean) msg.obj;
				if (bean.getResult().getCode().equals("0")) {
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					new BindIdenfyCodePopWindow(context, accountDBBean,accountString, loginListener, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							if (basePopWindow != null) {
								basePopWindow.showWindow();
							}
						}
					});
				}else{
					ToastUtil.showShort(context,bean.getResult().getTips());
				}
				break;
				
			case HandlerConstant.CHECKACCOUNT_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
			
		}
		
	};
	
	public AccountBindUsableRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow,String accountString, AccountDBBean accountDBBean,String popFrom, FLOnNetFinishListener flOnNetFinishListener) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		this.accountString = accountString;
		this.accountDBBean = accountDBBean;
		this.popFrom = popFrom;
		this.fLOnNetFinishListener = flOnNetFinishListener;
	}


	@Override
	public void sendHandlerMessage(int mWhat, Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	
	//...........................................................................................................
	@Override
	public void post() {
				PostParameter parameter = new PostParameter(context);
				final JSONObject holder;
				try {
					holder = new JSONObject();
					holder.put("gameInfo", parameter.getGameInfoJson());
					holder.put("sdkInfo", parameter.getSDKInfoJson());
					holder.put("deviceInfo", parameter.getDeviceInfoJson());
					holder.put("actionId", "4004");
					JSONObject userParamHolder = new JSONObject();
					userParamHolder.put("verifyType", "2");
					userParamHolder.put("userName", accountString);
					userParamHolder.put("userId", accountDBBean.getUserId());
					holder.put("userParam", userParamHolder);
					MyLogger.lczLog().i("绑定账号可用性请求参数："+holder.toString());
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
								MyLogger.lczLog().i("绑定账号可用性请求成功："+s);
								String jsonString = s.substring(7);
								Gson gson = new Gson();
								GetAccountUsableResultJsonBean bean = gson.fromJson(jsonString, GetAccountUsableResultJsonBean.class);
								sendHandlerMessage(HandlerConstant.CHECKACCOUNT_SUCCESS, bean);
							}
							
							@Override
							public void onFailure(Call arg0, IOException arg1) {
								MyLogger.lczLog().i("绑定账号可用性请求失败"+arg1);
								sendHandlerMessage(HandlerConstant.CHECKACCOUNT_FAIL, "");
							}
						});
						}
					}).start();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	}
}
