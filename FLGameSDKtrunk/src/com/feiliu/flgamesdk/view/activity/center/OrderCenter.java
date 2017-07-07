package com.feiliu.flgamesdk.view.activity.center;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.StringConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.flrequest.FlRequest;
import com.feiliu.flgamesdk.utils.EncryptUtils;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UIThreadToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;

/**
 * 订单activity的控制中心
 * <br/>设置界面，和功能
 * @author 刘传政
 *
 */
public class OrderCenter implements BaseBarListener {
	private Context context;
	private WebView webView;
	private float scale;//尺寸百分比
	private String url;//订单页面的url
	private GameInfo gameInfo;
	private String appid;
	private String userId;
	private String appKey;
	private String coopId;
	private String companyId;
	private String lastPayOrderDate;
	private String result = "";
	private ProgressDialog dialog;
	private TextView backgroundViewTv;//网页下的默认背景
	public OrderCenter(Context context) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
	}
	/**
	 * 展示界面
	 */
	public void activityShow() {
		View rootView = initView();//创建布局
		((Activity) context).setContentView(rootView);
		getDataFromInternet();
		//initDataAndControl();
	}
	
	
	/**
	 * 联网获取一些网页相关参数第二次开始
	 */
	private void getDataFromInternet2() {
		try {
			gameInfo = new GameInfo(context);//创建对象的过程中已经检验了是否配置正确
			appid = gameInfo.getAppId();
			appKey = gameInfo.getAppKey();
			coopId = gameInfo.getCoopId();
			companyId = gameInfo.getCompanyId();
			AccountDao accountDao = new AccountDao(context);
			List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
			if (accountDBBeans.size() > 0) {
				userId = accountDBBeans.get(0).getUserId();
			}
			String sign = appid + userId + lastPayOrderDate + appKey;
			//做md5签名 规则是签名：MD5(appid+userId+lastPayOrderDate +appKey)，appKey为飞流分配。
			sign = EncryptUtils.encryptMD5ToString(sign);
			
			JSONObject holder = new JSONObject();
			holder.put("appid", appid);
			holder.put("userId", userId);
			holder.put("coopId", coopId);
			holder.put("companyId", companyId);
			holder.put("lastPayOrderDate", lastPayOrderDate);
			holder.put("sign", sign);
			
			MyLogger.lczLog().i("获取订单页url和列表请求参数"+holder.toString());
			FlRequest request = new FlRequest(holder, URLConstant.GET_ORDERLIST_URL);
			request.post(new Callback() {
				
				

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					if (null != arg1 && arg1.code() != 200) {
						 UIThreadToastUtil.show(context, StringConstant.SERVER_EXCEPTION);
						return;
					 }
					 final String s = arg1.body().string();
					 MyLogger.lczLog().i("获取订单页url和列表返回参数"+s);
					 result = s;
					 //主动调用js方法，将数据传过去。
					 /*try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
					 ((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								//主动把下拉得到的数据传给js
								 webView.loadUrl("javascript:androidCallJs("+result+")");
							}
						});
					
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
					 UIThreadToastUtil.show(context, StringConstant.NET_EXCEPTION);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 联网获取一些网页相关参数
	 */
	private void getDataFromInternet() {
		try {
			gameInfo = new GameInfo(context);//创建对象的过程中已经检验了是否配置正确
			appid = gameInfo.getAppId();
			appKey = gameInfo.getAppKey();
			coopId = gameInfo.getCoopId();
			companyId = gameInfo.getCompanyId();
			AccountDao accountDao = new AccountDao(context);
			List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
			if (accountDBBeans.size() > 0) {
				userId = accountDBBeans.get(0).getUserId();
			}
			lastPayOrderDate = "0";
			String sign = appid + userId + lastPayOrderDate + appKey;
			//做md5签名 规则是签名：MD5(appid+userId+lastPayOrderDate +appKey)，appKey为飞流分配。
			sign = EncryptUtils.encryptMD5ToString(sign);
			
			JSONObject holder = new JSONObject();
			holder.put("appid", appid);
			holder.put("userId", userId);
			holder.put("channelId", coopId);
			holder.put("cpId", companyId);
			holder.put("lastPayOrderDate", lastPayOrderDate);
			holder.put("sign", sign);
			
			MyLogger.lczLog().i("获取订单页url和列表请求参数"+holder.toString());
			FlRequest request = new FlRequest(holder, URLConstant.GET_ORDERLIST_URL);
			request.post(new Callback() {
				
				

				@Override
				public void onResponse(Call arg0, Response arg1) throws IOException {
					if (null != arg1 && arg1.code() != 200) {
						 UIThreadToastUtil.show(context, StringConstant.SERVER_EXCEPTION);
						return;
					 }
					 final String s = arg1.body().string();
					 MyLogger.lczLog().i("获取订单页url和列表返回参数"+s);
					 try {
						JSONObject jsonString = new JSONObject(s);
						url = jsonString.getString("url");
						 ((Activity) context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									backgroundViewTv.setVisibility(View.INVISIBLE);
									webView.setVisibility(View.VISIBLE);
									initDataAndControl(s);
								}
							});
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
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
	 * (由于需求改动，暂不使用)
	 */
	private void showProgress() {
		dialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);//白色主题
		dialog.setCancelable(false);// 设置是否可以通过点击Back键取消 
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//点击返回键才可触发。普通的dismiss不会触发
				MyLogger.lczLog().i("cancle了");
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
		
		BaseBarView baseBarView = new BaseBarView(context, "充值记录", false,true, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarView.setLayoutParams(baseBarViewParams);
		backgroundViewTv = new TextView(context);
		RelativeLayout.LayoutParams backgroundViewTvParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		backgroundViewTvParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		backgroundViewTvParams.topMargin=(int) (0*scale);
		backgroundViewTv.setLayoutParams(backgroundViewTvParams);
		backgroundViewTv.setGravity(Gravity.CENTER);//文字居中
		backgroundViewTv.setText("加载中...");
		backgroundViewTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		//............................................webview...........................................................
		
		webView = new WebView(context);
		RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		webViewParams.topMargin = (int) (100*scale);
		webView.setLayoutParams(webViewParams);
		
		retAbsoluteLayout.addView(baseBarView);
		retAbsoluteLayout.addView(backgroundViewTv);
		retAbsoluteLayout.addView(webView);
		webView.setVisibility(View.INVISIBLE);
		return retAbsoluteLayout;
		
	}
	/**
	 * 设置数据，并控制数据
	 * @param s 
	 */
	private void initDataAndControl(final String s) {

		// 设置webview属性
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 不使用缓存：
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//
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
			}

			/**
			 * 页面加载完成
			 */
			public void onPageFinished(WebView view, String url) {
				webView.loadUrl("javascript:androidCallJs("+s+")");
			}

			/**
			 * 处理web点击请求 返回true 有应用程序处理，false 有webview处理 只有通知页面点击有app处理
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
		public void getOrderList(String lastPayOrderDate) {
			//根据不同lastPayOrderDate，去fl服务器获取订单数据。
			OrderCenter.this.lastPayOrderDate = lastPayOrderDate;
			getDataFromInternet2();
		}
	}
	
	@Override
	public void backbuttonclick() {
		if (webView.canGoBack()) {
			webView.goBack();
		}else {
			((Activity) context).finish();
		}
	}
	
	@Override
	public void closewindow() {
		((Activity) context).finish();
	}
}
