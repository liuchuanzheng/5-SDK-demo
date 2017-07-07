package com.feiliu.flgamesdk.view.activity.center;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.ColorContant;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.flrequest.FlRequest;
import com.feiliu.flgamesdk.net.request.GuestLoginRequest;
import com.feiliu.flgamesdk.pay.ali.FLALi;
import com.feiliu.flgamesdk.pay.weixin.FLWeiXin;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.Login2PopWindow;
import com.feiliu.flgamesdk.view.popupwindow.LoginNoPasswordPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.LoginPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;

/**
 * 支付activity的控制中心
 * <br/>设置界面，和功能
 * @author 刘传政
 *
 */
public class PayCenter implements BaseBarListener {
	private Context context;
	private WebView webView;
	private float scale;//尺寸百分比
	private String jsJson;
	private String url;
	private Bundle bundle;
	private AlertDialog builder;
	public PayCenter(Context context, String jsJson, String url, Bundle bundle) {
		this.context = context;
		this.jsJson = jsJson;
		this.url = url;
		this.bundle = bundle;
		scale = UiSizeUtil.getScale(this.context);
		// 将微信插件写到sd卡
		boolean isInstalled = isPackageAlreadyInstalled(context,"com.feiliu.platformsdk.wxpay");
		MyLogger.lczLog().i("是否安装插件"+isInstalled);
		if (!isExist() && !isInstalled) {
			writeToSD("flweixin.apk");
			MyLogger.lczLog().i("复制插件到sd卡");
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
	 * 将插件写到sd卡上
	 * 
	 * @param fileName
	 */
	public void writeToSD(String fileName) {
		InputStream inputStream;
		try {
			inputStream = context.getResources().getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/chajian");
			if (!file.exists()) {
				file.mkdirs();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/chajian/"
							+ fileName);
			byte[] buffer = new byte[512];
			int count = 0;
			while ((count = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, count);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	 * 展示界面
	 */
	public void activityShow() {
		View rootView = initView();//创建布局
		((Activity) context).setContentView(rootView);
		initDataAndControl();
	}
	
	/**
	 * 创建activity的布局
	 * @return 
	 */
	private View initView() {
		RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		//..............................................title.............................................................
		
		BaseBarView baseBarView = new BaseBarView(context, "充值中心", false,true, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//............................................webview...........................................................
		
		webView = new WebView(context);
		RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		webViewParams.topMargin = (int) (100*scale);
		webView.setLayoutParams(webViewParams);
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(webView);
		return retAbsoluteLayout;
		
	}
	/**
	 * 设置数据，并控制数据
	 */
	private void initDataAndControl() {

		// 设置webview属性
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 不使用缓存：
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				 handler.proceed();//接受证书  否则在部分三星手机上打开https会出现空白
				super.onReceivedSslError(view, handler, error);
			}

			/**
			 * 开始加载页面启动loading
			 */
			public void onPageStarted(WebView view, String url,
					android.graphics.Bitmap favicon) {
				MyLogger.lczLog().i("onPageStarted");
			}

			/**
			 * 页面加载完成
			 */
			public void onPageFinished(WebView view, String url) {
				MyLogger.lczLog().i("onPageFinished");
				//调用js方法。将json字符创传递过去，使其展示商品信息和支付列表。
				webView.loadUrl("javascript:androidCallJs("+jsJson+")");
			}

			/**
			 * 处理web点击请求 返回true 有应用程序处理，false 有webview处理 只有通知页面点击有app处理
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				MyLogger.lczLog().i("shouldOverrideUrlLoading");
				return super.shouldOverrideUrlLoading(view, url);
			}

			/**
			 * 同步处理关键事件 If return true, WebView will not handle the key event.
			 * If return false, WebView will always handle the key event, so
			 * none of the super in the view chain will see the key event. The
			 * default behavior returns false.
			 */
			public boolean shouldOverrideKeyEvent(WebView view,
					android.view.KeyEvent event) {
				return false;
			}

			/**
			 * error
			 */
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
			}

		});
		webView.setWebChromeClient(new WebChromeClient() {
			/*
			 * 页面加载进度0-100 (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebChromeClient#onProgressChanged(android.webkit
			 * .WebView, int)
			 */
			public void onProgressChanged(WebView view, int newProgress) {
			}
		});
		//定义一个提供给js调用本地的方法。为了把h5中选择的支付方式传过来。
		webView.addJavascriptInterface(new JsInterface(), "jsCallAndroid");
		webView.loadUrl(url);
	
	}
	/**
	 * @author 刘传政
	 * 这是提供给 js调用的类
	 *
	 */
	public class JsInterface {
		@JavascriptInterface
		public void pay(String payCompanyId,String channleId) {
			//根据不同的支付方式，去fl服务器获取不同的支付参数。
			//ToastUtil.showLong(context, channleId);
			MyLogger.lczLog().i(channleId);
			MyLogger.lczLog().i(payCompanyId);
			switch (channleId) {
			case "1001":
				//获取支付宝支付参数
				goAli(payCompanyId,channleId);
				//关闭支付界面，防止重复
				((Activity) context).finish();
				break;
			case "1002":
				//微信支付插件是否安装
				boolean isInstalled = isPackageAlreadyInstalled(
						context,
						"com.feiliu.platformsdk.wxpay");
				
				//在请求订单之前弹出安装插件对话框,以免作废订单
				if (isExist() && !isInstalled) {
					showInstallDialog();
				}else {
					//获取微信支付参数
					goWeixin(payCompanyId,channleId);
					//关闭支付界面，防止重复
					((Activity) context).finish();
				}
				
				break;
			

			default:
				break;
			}
			
		}
	}
	
	/**
	 * 弹出安装对话框
	 */
	public void showInstallDialog() {
		Builder m = new AlertDialog.Builder(context)
				.setMessage("\n您尚未安装微信支付安全插件(飞流九天),请安装后再支付或使用其他方式支付.\n")
				.setCancelable(false)
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
								context.startActivity(intent);
							}

						});
		m.show();
	}
	@Override
	public void backbuttonclick() {
		if (webView.canGoBack()) {
			webView.goBack();
		}else {
			((Activity) context).finish();
		}
	}
	
	private void sendBrocatPayState(String state, String payCompanyId,String channleId) {
		Intent intent = new Intent();
		intent.setAction("com.feiliu.feiliusdk.FLThirdPayActivity."
				+ bundle.getString("timeStamp"));
		intent.putExtra("paystate", state);
		intent.putExtra("channleId", channleId);
		intent.putExtra("payCompanyId", payCompanyId);
		((Activity) context).sendBroadcast(intent);
	}
	public void goAli(String payCompanyId,String channleId) {
		sendBrocatPayState("Ali", payCompanyId,channleId);
		MyLogger.lczLog().i("发送阿里支付状态广播");
	}
	public void goWeixin(String payCompanyId,String channleId) {
		sendBrocatPayState("Weixin", payCompanyId,channleId);
		MyLogger.lczLog().i("发送微信支付状态广播");
		
	}
	/**
	 * 发送关闭支付界面广播
	 * @param payCompanyId
	 * @param channleId
	 */
	public void goClose(String payCompanyId,String channleId) {
		sendBrocatPayState("close", payCompanyId,channleId);
		MyLogger.lczLog().i("发送关闭支付界面广播");
	}
	
	@Override
	public void closewindow() {
		//((Activity) context).finish();
		showConfirmDialog();
	}
	/**
	 * 创建界面ui并设置其中的点击事件等
	 * @return
	 */
	public View createDialogUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int) (800 * scale), (int) (450 * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		Drawable bg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		totalLayout.setBackgroundDrawable(bg);
		//.....................................提示语.........................................................
		TextView tipsView = new TextView(context);
		RelativeLayout.LayoutParams tipsViewParams=new RelativeLayout.LayoutParams((int)(800*scale),(int)(300*scale));
		tipsViewParams.leftMargin = (int) (0*scale);
		tipsViewParams.topMargin=(int) (0*scale);
		tipsView.setLayoutParams(tipsViewParams);
		Drawable tipsViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//登录注册按钮图片
		tipsView.setBackgroundDrawable(tipsViewBg);
		tipsView.setText("支付未完成，是否取消本次支付？");
		tipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		tipsView.setTextColor(ColorContant.GRAY);
		tipsView.setGravity(Gravity.CENTER);//文字居中
		//.....................................黑色横线........................................................
		TextView blackView1 = new TextView(context);
		RelativeLayout.LayoutParams blackView1Params=new RelativeLayout.LayoutParams((int)(800*scale),(int)(3*scale));
		blackView1Params.leftMargin = (int) (0*scale);
		blackView1Params.topMargin=(int) (295*scale);
		blackView1.setLayoutParams(blackView1Params);
		blackView1.setBackgroundColor(0xFFEBE3E3);
		//.....................................黑色竖线........................................................                       
		TextView blackView2 = new TextView(context);
		RelativeLayout.LayoutParams blackView2Params=new RelativeLayout.LayoutParams((int)(3*scale),(int)(150*scale));
		blackView2Params.leftMargin = (int) (399*scale);
		blackView2Params.topMargin=(int) (298*scale);
		blackView2.setLayoutParams(blackView2Params);
		blackView2.setBackgroundColor(0xFFEBE3E3);
		//....................................取消支付按钮......................................................
		TextView cancleView = new TextView(context);
		RelativeLayout.LayoutParams cancleViewParams=new RelativeLayout.LayoutParams((int)(400*scale),(int)(150*scale));
		cancleViewParams.leftMargin = (int) (0*scale);
		cancleViewParams.topMargin=(int) (300*scale);
		cancleView.setLayoutParams(cancleViewParams);
		Drawable cancleViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//登录注册按钮图片
		cancleView.setBackgroundDrawable(cancleViewBg);
		cancleView.setText("取消支付");
		cancleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		cancleView.setTextColor(ColorContant.GRAY);
		cancleView.setGravity(Gravity.CENTER);//文字居中
		cancleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goClose("", "");//发送支付关闭通知,也就是支付失败了
				builder.dismiss();
				((Activity) context).finish();
			}
		});
		//..................................继续支付按钮...................................................
		TextView continueView = new TextView(context);//登录注册按钮
		RelativeLayout.LayoutParams continueViewParams=new RelativeLayout.LayoutParams((int)(400*scale),(int)(150*scale));
		continueViewParams.leftMargin=(int) (400*scale);
		continueViewParams.topMargin=(int) (300*scale);
		continueView.setLayoutParams(continueViewParams);
		Drawable continueViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//登录注册按钮图片
		continueView.setBackgroundDrawable(continueViewBg);
		continueView.setText("继续支付");
		continueView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		continueView.setTextColor(ColorContant.GRAY);
		continueView.setGravity(Gravity.CENTER);//文字居中
		continueView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
				return;
			}
		});
		totalLayout.addView(tipsView);	
		totalLayout.addView(blackView1);
		totalLayout.addView(cancleView);
		totalLayout.addView(continueView);
		totalLayout.addView(blackView2);
		return totalLayout;
	}
	/**
	 * 弹出关闭支付界面前的确认对话框
	 */
	public void showConfirmDialog(){
		builder = new AlertDialog.Builder(context).create();
		builder.setCancelable(false);// 设置是否可以通过点击Back键取消 
        builder.show();
        RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int) (800 * scale), (int) (450 * scale));
        builder.getWindow().setContentView(createDialogUi(),totalLayoutParams);//设置弹出框加载的布局
        
	};
}
