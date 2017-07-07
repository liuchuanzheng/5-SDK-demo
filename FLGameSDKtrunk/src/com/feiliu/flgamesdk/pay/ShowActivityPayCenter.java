package com.feiliu.flgamesdk.pay;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.bean.netresultbean.urlAndPayListBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.ColorContant;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.StringConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnPayListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.flrequest.FlRequest;
import com.feiliu.flgamesdk.pay.ali.FLALi;
import com.feiliu.flgamesdk.pay.weixin.FLWeiXin;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UIThreadToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.google.gson.Gson;

public class ShowActivityPayCenter {

	private Context context;
	private Bundle bundle;
	private ThirdPayStateReceiver thirdPayStateReceiver;
	private FLOnPayListener flOnPayListener;
	private ProgressDialog dialog;
	private float scale;
	private FlRequest request1;
	private GameInfo gameInfo;
	private String appid;
	private String appName;
	private String userId;
	public ShowActivityPayCenter(Context mContext, FLOnPayListener flOnPayListener, String screen, String cpOrderId,
			String amount, String goodsId,
			String roleId, String groupId, String merPriv, String cpNotifyUrl) {
			this.context = mContext;
			initData();
			this.flOnPayListener = flOnPayListener;
			scale = UiSizeUtil.getScale(this.context);
			getUrlAndList(screen,cpOrderId, appid, appName,userId, amount, goodsId, roleId, groupId, merPriv, cpNotifyUrl);
	}
	/**
	 * 获取游戏和用户的一些数据，便于网络请求时提交。
	 */
	private void initData() {
		this.gameInfo = new GameInfo(context);//创建对象的过程中已经检验了是否配置正确
		appid = gameInfo.getAppId();
		appName = getAppName(context);
		AccountDao accountDao = new AccountDao(context);
		List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
		if (accountDBBeans.size() > 0) {
			userId = accountDBBeans.get(0).getUserId();
		}
	}
	/** 
     * 获取应用程序名称 
     */  
    private  String getAppName(Context context)  
    {  
        try  
        {  
            PackageManager packageManager = context.getPackageManager();  
            PackageInfo packageInfo = packageManager.getPackageInfo(  
                    context.getPackageName(), 0);  
            int labelRes = packageInfo.applicationInfo.labelRes;  
            return context.getResources().getString(labelRes);  
        } catch (NameNotFoundException e)  
        {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    
	/**
	 * 获取支付url和支付通道列表
	 */
	private void getUrlAndList(final String screen,final String cpOrderId,final String appId,String appName
			,final String userId,final String amount,final String goodsId,final String roleId,final String groupId
			,final String merPriv,final String cpNotifyUrl) {
		try {
			JSONObject holder = new JSONObject();
			JSONObject payCenterInfoHolder = new JSONObject();
			payCenterInfoHolder.put("screen", screen);
			payCenterInfoHolder.put("channelId", gameInfo.getCoopId());
			payCenterInfoHolder.put("cpId", gameInfo.getCompanyId());
			holder.put("payCenterInfo", payCenterInfoHolder);
			
			JSONObject orderInfoHolder = new JSONObject();
			orderInfoHolder.put("osType", "0");//系统类型(0-Android、1-IOS)
			orderInfoHolder.put("cpOrderId", cpOrderId);
			orderInfoHolder.put("appId", appId);
			orderInfoHolder.put("appName", appName);
			orderInfoHolder.put("userId", userId);
			orderInfoHolder.put("amount", amount);
			orderInfoHolder.put("goodsId", goodsId);
			orderInfoHolder.put("roleId", roleId);
			orderInfoHolder.put("groupId", groupId);
			orderInfoHolder.put("merPriv", merPriv);
			orderInfoHolder.put("cpNotifyUrl", cpNotifyUrl);
			holder.put("orderInfo", orderInfoHolder);
			MyLogger.lczLog().i("获取支付url和支付通道列表请求参数"+holder.toString());
			request1 = new FlRequest(holder, URLConstant.GET_PAYURL_URL);
			showProgress();
			request1.post(new Callback() {
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					 hideProgress();
					 if (null != arg1 && arg1.code() != 200) {
						 UIThreadToastUtil.show(context, StringConstant.SERVER_EXCEPTION);
						return;
					 }
					 final String jsJson = arg1.body().string();
					 MyLogger.lczLog().i("获取支付url和支付通道列表返回参数"+jsJson);
					 Gson gson = new Gson();
					 urlAndPayListBean bean = gson.fromJson(jsJson, urlAndPayListBean.class);
					 //拿到返回的支付界面地址
					 final String payCenterUrl = bean.getPayCenterInfo().getPayCenterUrl();
					 ((Activity) context).runOnUiThread(new Runnable() {
							

							@Override
							public void run() {
								
								try {
									bundle = new Bundle();
									bundle.putString("screen", screen);
									bundle.putString("jsJson", jsJson.toString());
									bundle.putString("url", payCenterUrl);
									//请求订单的9个参数
									bundle.putString("cpOrderId", cpOrderId);
									bundle.putString("appId", appId);
									bundle.putString("userId", userId);
									bundle.putString("amount", amount);
									bundle.putString("goodsId", goodsId);
									bundle.putString("roleId", roleId);
									bundle.putString("groupId", groupId);
									bundle.putString("merPriv", merPriv);
									bundle.putString("cpNotifyUrl", cpNotifyUrl);
									String timeStamp=""+System.currentTimeMillis();
									thirdPayStateReceiver=new ThirdPayStateReceiver();
									thirdPayStateReceiver.registeMe(thirdPayStateReceiver,timeStamp);
									MyLogger.lczLog().i("注册三方支付状态广播");
									bundle.putString("timeStamp", timeStamp);
									showClauseActivity(context, 4, bundle);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						});
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
					hideProgress();
					UIThreadToastUtil.show(context, StringConstant.NET_EXCEPTION);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 隐藏等待框
	 */
	protected void hideProgress() {
		if (null != dialog) {
			dialog.dismiss();
		}
	}
	/**
	 * 显示等待框
	 */
	private void showProgress() {
		dialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);//白色主题
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消 
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//点击返回键才可触发。普通的dismiss不会触发
				MyLogger.lczLog().i("cancle了");
				if (null != request1) {
					request1.newCall.cancel();//取消所有请求
					request1.okHttpClient.dispatcher().cancelAll();//取消本次请求
				}
			}
		});
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条  
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		dialog.setMessage("加载中，请稍候...");
		dialog.show();
		//设置参数一定要在show之后，因为只有显示出来后才能计算出宽高信息
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.alpha = 1.0f;//这是对话框背景的透明度
		params.width = (int) (800*scale);
		params.height = (int) (400*scale);
		params.dimAmount = 0f;//这是全屏背景的透明度
		dialog.getWindow().setAttributes(params);
		
	}
	/**
	 * 展示activity
	 */
	protected void showClauseActivity(Context activityContext,int intentType,Bundle bundle) {
		Intent intent = new Intent(activityContext,FLSdkActivity.class);
	    intent.putExtra("fromtype",intentType);//表明是隐私条款意图
	    intent.putExtras(bundle);
	    ((Activity)activityContext).startActivity(intent);
	}
	/**
	 * @author Administrator
	 *接收三方支付状态的广播接收者
	 */
	class ThirdPayStateReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent intent) {
			MyLogger.lczLog().i("接收到三方支付状态广播");
			String state=intent.getStringExtra("paystate");
			String channleId = intent.getStringExtra("channleId");
			String payCompanyId = intent.getStringExtra("payCompanyId");
			if ("Weixin".equals(state)) {
				FLWeiXin w=new FLWeiXin(context,flOnPayListener,bundle,channleId,payCompanyId);
				w.FLWXPay();
			}else if ("Ali".equals(state)) {
				FLALi flaLi = new FLALi(context,flOnPayListener,bundle,channleId,payCompanyId);
				flaLi.FLALiPay();
			}else if ("close".equals(state)) {
				flOnPayListener.onPayComplete(-1);//通知cp支付失败
			}
			
		}
		public void registeMe(ThirdPayStateReceiver receiver,String timeStamp){
			IntentFilter filter=new IntentFilter();
			filter.addAction("com.feiliu.feiliusdk.FLThirdPayActivity."+timeStamp);
			context.registerReceiver(receiver, filter);
		}
		public void unregisteMe(ThirdPayStateReceiver receiver){
			context.unregisterReceiver(receiver);
		}
	}
}
