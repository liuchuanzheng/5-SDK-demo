package com.feiliu.flgamesdk.net.request;

import java.io.IOException;
import java.text.BreakIterator;

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

import com.feiliu.flgamesdk.bean.netresultbean.RequestDeviceActiveResultJsonBean;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;


/**
 * 请求激活设备
 * @author 刘传政
 *
 */
public class DeviceActiveRequest extends BaseReuest{
	private  Handler mHandler = new Handler(Looper.getMainLooper()){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.REQUESTDEVICEACTIVE_SUCCESS:
				RequestDeviceActiveResultJsonBean mResultJsonBean= (RequestDeviceActiveResultJsonBean) msg.obj;
				if (mResultJsonBean.getResult().getCode().equals("0")) {
					SPUtils mSpUtils = new SPUtils(context, SPConstant.DEVICE);
					mSpUtils.putInt("deviceId", mResultJsonBean.getDeviceInfo().getDeviceId());
				}
				break;
			case HandlerConstant.REQUESTDEVICEACTIVE_FAIL:
				ToastUtil.showShort(context,"当前设备无网络，请检查网络设置");
				break;
			}
			
		}
		
	};
	
	
	
	public DeviceActiveRequest(Context context,FLOnLoginListener loginListener
			,BasePopWindow basePopWindow) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
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
	 * 联网激活设备
	 * <br/>对应网络协议2000
	 */
	public void post() {
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "2000");
			
			MyLogger.lczLog().i("激活设备请求参数："+holder.toString());
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
				            .url(URLConstant.REQUEST_DEVICE_ACTIVE_URL)
				            .post(requestBody)
				            .build();

				  okHttpClient.newCall(request).enqueue(new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						
						String s = arg1.body().string();
						MyLogger.lczLog().i("激活设备请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						RequestDeviceActiveResultJsonBean mResultJsonBean = gson.fromJson(jsonString, RequestDeviceActiveResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.REQUESTDEVICEACTIVE_SUCCESS,mResultJsonBean);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						sendHandlerMessage(HandlerConstant.REQUESTDEVICEACTIVE_FAIL,"");
						MyLogger.lczLog().i("激活设备请求失败"+arg1);
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 
	}
	
}
