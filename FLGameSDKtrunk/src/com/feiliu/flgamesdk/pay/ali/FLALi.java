package com.feiliu.flgamesdk.pay.ali;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.global.StringConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnPayListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.flrequest.FlRequest;
import com.feiliu.flgamesdk.pay.weixin.FLWeiXin;
import com.feiliu.flgamesdk.utils.EncodeUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UIThreadToastUtil;

public class FLALi {
	private static final int SDK_PAY_FLAG = 1;
	//ALI支付相关状态
	private final String ALIPAY_SUCCESS ="9000";
	private final String ALIPAY_ISCONFIRMING ="8000";
	private final String ALIPAY_FAILED ="4000";
	private final String ALIPAY_CANCEL ="6001";
	private final String ALIPAY_NETERROR ="6002";

	private Context mContext;
	
	
	//ALi支付订单ID,在支付中心点击ALi支付时通过URL截取
	public static String FLALiOrderIDURL;
	private Bundle bundle;
	private String channleId;
	private FLOnPayListener flOnPayListener;
	private String payCompanyId;

	public FLALi(Context mContext, FLOnPayListener flOnPayListener, Bundle bundle, String channleId, String payCompanyId) {
		this.mContext = mContext;
		this.flOnPayListener = flOnPayListener;
		this.bundle = bundle;
		this.channleId = channleId;
		this.payCompanyId = payCompanyId;
	}
	/**
	 * 请求支付
	 */
	public void FLALiPay() {
		FLGetPayInfo();

	}

	public void FLGetPayInfo() {
		try {
			final JSONObject holder = new JSONObject();
			holder.put("osType","0");
			holder.put("cpOrderId", bundle.getString("cpOrderId"));
			holder.put("payCompanyId", payCompanyId);
			holder.put("appId", bundle.getString("appId"));
			GameInfo gameInfo = new GameInfo(mContext);
			holder.put("channelId", gameInfo.getCoopId());
			holder.put("cpId", gameInfo.getCompanyId());
			holder.put("payChannelId",channleId);
			holder.put("userId", bundle.getString("userId"));
			holder.put("amount", bundle.getString("amount"));
			holder.put("goodsId", bundle.getString("goodsId"));
			holder.put("roleId", bundle.getString("roleId"));
			holder.put("groupId", bundle.getString("groupId"));
			holder.put("merPriv", bundle.getString("merPriv"));
			holder.put("cpNotifyUrl", bundle.getString("cpNotifyUrl"));
			MyLogger.lczLog().i("获取阿里支付参数请求参数"+holder.toString());
			FlRequest request1 = new FlRequest(holder, URLConstant.GET_PAYPARAMETER_URL);
			request1.post(new Callback() {
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					if (null != arg1 && arg1.code() != 200) {
						 UIThreadToastUtil.show(mContext, StringConstant.SERVER_EXCEPTION);
						return;
					 }
					 final String s = arg1.body().string();
					 MyLogger.lczLog().i("获取阿里支付参数响应参数"+s);
					 
						
					 ((Activity)(mContext)).runOnUiThread(new Runnable() {
								
							@Override
							public void run() {
								try {
									JSONObject jsonObject = new JSONObject(s);
									String payInfo = jsonObject.getString("data");
									MyLogger.lczLog().i("阿里支付请求参数"+payInfo);
									pay(payInfo);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
					 });
					
					
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
					 UIThreadToastUtil.show(mContext, StringConstant.NET_EXCEPTION);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void showToastTips(int mWhat, String tips){
		Message msg = mFLHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = tips;
		mFLHandler.sendMessage(msg);
	}

	Handler mFLHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			String tips = (String) msg.obj;
			switch (msg.what) {}
		}
	};
	
	//支付回调处理
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				MyLogger.lczLog().i("阿里支付结果"+payResult);
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
					if (null != FLALi.this.flOnPayListener) {
						FLALi.this.flOnPayListener.onPayComplete(0);
					}
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
					if (null != FLALi.this.flOnPayListener) {
						FLALi.this.flOnPayListener.onPayComplete(-1);
					}
				}
				break;
			}
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 */
	public void pay(final String payInfo) {
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				//设置沙箱环境
				//EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
				PayTask alipay = new PayTask((Activity) mContext);
				Map<String, String> result = alipay.payV2(payInfo, true);
				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
}
