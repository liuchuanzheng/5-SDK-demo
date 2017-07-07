package com.feiliu.flgamesdk.pay.weixin;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.global.StringConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnPayListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.flrequest.FlRequest;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UIThreadToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class FLWeiXin {
	public static String FLWeiXinOrderIDURL;
	private Context mContext;
	private static String payState;//支付状态提示语
	WeiXinPayStateReceiver weiXinPayStateReceiver;//支付状态的广播接收者
	//微信订单请求的返回结果对象
	Handler mHandler;
	private Bundle bundle;
	private String channleId;
	private FLOnPayListener flOnPayListener;
	private String payCompanyId;
	class WeiXinPayStateReceiver extends BroadcastReceiver {

		public void onReceive(Context arg0, Intent intent) {
			
			String state = intent.getStringExtra("wxpaystate");
			Log.i("liuchuanzheng","onreceive"+state);
			if (state.equals("success")){
				payState="支付成功";
				showToastTips(2022, "");
				Log.i("liuchuanzheng",2022+"");
			}
				
			if (state.equals("failed")){
				payState="支付失败";
				showToastTips(2023, "");
				Log.i("liuchuanzheng",2023+"");
			}
				
			if (state.equals("cancle")) {
				payState="用户取消";
				payState="支付失败";//飞流默认支付结果只有两种，支付成功和支付失败。
				showToastTips(20231, "");//因为2025被占用,临时加个1
				Log.i("liuchuanzheng",20231+"");
			}	
			unregisteMe(weiXinPayStateReceiver);
		}

		public void registeMe(WeiXinPayStateReceiver receiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.feiliu.gameplatform.WeiXinPayStateReceiver");
			mContext.registerReceiver(receiver, filter);
		}

		public void unregisteMe(WeiXinPayStateReceiver receiver) {
			mContext.unregisterReceiver(receiver);
		}

	}

	/**
	 * 判断软件是否已安装
	 * 
	 * @param cnt
	 * @param pkgName
	 * @return
	 */
	public static boolean isPackageAlreadyInstalled(Context cnt, String pkgName) {

		try {
			ApplicationInfo applicationInfo = cnt.getPackageManager()
					.getApplicationInfo(pkgName, 1);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 判断复制的文件是否存在
	 * 
	 * @return
	 */
	private boolean isExist() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/chajian/flweixin.apk");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * 弹出支付结果对话框
	 */
	public void showPayStateDialog(String payState) {
		Builder m = new AlertDialog.Builder(mContext)
				.setMessage("支付结果:"+payState)
				.setPositiveButton("返回游戏",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}

						});
		m.show();
	}
	/**
	 * 弹出安装对话框
	 */
	public void showInstallDialog() {
		Builder m = new AlertDialog.Builder(mContext)
				.setMessage("\n您尚未安装微信支付安全插件(飞流九天),请安装后再支付或使用其他方式支付.\n")
				.setNegativeButton("使用其他方式支付", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}

				})
				.setPositiveButton("\b\b\b安装\b\b\b",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.setDataAndType(Uri.parse("file://"
										+ Environment
												.getExternalStorageDirectory()
												.getAbsolutePath()
										+ "/chajian/flweixin.apk"),
										"application/vnd.android.package-archive");
								mContext.startActivity(intent);
							}

						});
		m.show();
	}

	public FLWeiXin(final Context mContext, FLOnPayListener flOnPayListener, Bundle bundle, String channleId, String payCompanyId) {
		this.mContext = mContext;
		this.flOnPayListener = flOnPayListener;
		this.bundle = bundle;
		this.channleId = channleId;
		this.payCompanyId = payCompanyId;
		
		mHandler = new Handler(Looper.getMainLooper()) {

			public void handleMessage(Message msg) {
				String tips = (String) msg.obj;
				switch (msg.what) {
				case 2024://请求订单成功
					
					break;

				case 2022:
					//支付成功
					//showPayStateDialog(payState);//不使用弹对话框的形式，以免与cp弹对话框冲突
					ToastUtil.showShort(mContext, payState);
					if (null != FLWeiXin.this.flOnPayListener) {
						FLWeiXin.this.flOnPayListener.onPayComplete(0);
					}
					break;

				case 2023:
					//支付失败
					ToastUtil.showShort(mContext, payState);
					//showPayStateDialog(payState);
					ToastUtil.showShort(mContext, payState);
					if (null != FLWeiXin.this.flOnPayListener) {
						FLWeiXin.this.flOnPayListener.onPayComplete(-1);
					}
					break;
				case 20231:
					//用户取消
					ToastUtil.showShort(mContext, payState);
					//showPayStateDialog(payState);
					if (null != FLWeiXin.this.flOnPayListener) {
						FLWeiXin.this.flOnPayListener.onPayComplete(-1);
					}
					break;
				case 2003:
					break;
				}
			}
		};
		
	}
	 
	public void FLWXPay() {
		boolean isWeiXinInstalled = isPackageAlreadyInstalled(
				FLWeiXin.this.mContext,
				"com.tencent.mm");
		if (!isWeiXinInstalled) {
			Toast.makeText(mContext,"微信未安装,无法进行支付,请安装再试", 0).show();
			return;
		}
		FLGetPayInfo();
		
		
	}
	
	/**
	 * 联网向飞流服务器获取唤起微信app的支付参数
	 */
	private void FLGetPayInfo() {
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
			MyLogger.lczLog().i("获取微信支付参数请求参数"+holder.toString());
			FlRequest request1 = new FlRequest(holder, URLConstant.GET_PAYPARAMETER_URL);
			request1.post(new Callback() {
				
				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					if (null != arg1 && arg1.code() != 200) {
						 UIThreadToastUtil.show(mContext, StringConstant.SERVER_EXCEPTION);
						return;
					 }
					 String s = arg1.body().string();
					 MyLogger.lczLog().i("获取微信支付参数响应参数"+s);
					 try {
						JSONObject jsonObject = new JSONObject(s);
						
						final String appid = jsonObject.getString("appid");
						final String noncestr = jsonObject.getString("noncestr");
						final String packagek = jsonObject.getString("package");
						final String partnerid = jsonObject.getString("partnerid");
						final String prepayid = jsonObject.getString("prepayid");
						final String sign = jsonObject.getString("sign");
						final String timestamp = jsonObject.getString("timestamp");
						 ((Activity)(mContext)).runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									weiXinPayStateReceiver = new WeiXinPayStateReceiver();
									weiXinPayStateReceiver.registeMe(weiXinPayStateReceiver);
									callWeixin(appid,noncestr,packagek,partnerid,prepayid,sign,timestamp);
								}
							});
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
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

	/**
	 * 唤起微信app
	 * @param timestamp 
	 * @param sign 
	 * @param prepayid 
	 * @param partnerid 
	 * @param packagek 
	 * @param noncestr 
	 * @param appid 
	 */
	private void callWeixin(String appid, String noncestr, String packagek, String partnerid, String prepayid, String sign, String timestamp) {
		ComponentName componetName = new ComponentName(
				// 这个是另外一个应用程序的包名
						"com.feiliu.platformsdk.wxpay",
						// 这个参数是要启动的Activity
						"com.feiliu.platformsdk.wxpay.MainActivity");
				Intent LaunchIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("appid", appid);
				bundle.putString("partnerid",partnerid);
				bundle.putString("prepayid", prepayid);
				bundle.putString("packagevalue",packagek);
				bundle.putString("noncestr",noncestr);
				bundle.putString("timestamp", timestamp);
				bundle.putString("sign", sign);
				LaunchIntent.putExtras(bundle);
				LaunchIntent.setComponent(componetName);
				LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
						    | Intent.FLAG_ACTIVITY_NEW_TASK
						    | Intent.FLAG_ACTIVITY_CLEAR_TOP
						    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
						    | Intent.FLAG_ACTIVITY_NO_USER_ACTION
						    | Intent.FLAG_ACTIVITY_NO_ANIMATION);

				mContext.startActivity(LaunchIntent);
	}

	

	protected void showToastTips(int mWhat, String tips) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = tips;
		mHandler.sendMessage(msg);
	}

	

	

}
