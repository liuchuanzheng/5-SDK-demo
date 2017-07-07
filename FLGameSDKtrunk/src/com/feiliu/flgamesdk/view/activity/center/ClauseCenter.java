package com.feiliu.flgamesdk.view.activity.center;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;

/**
 * 隐私协议activity的控制中心
 * <br/>设置界面，和功能
 * @author 刘传政
 *
 */
public class ClauseCenter implements BaseBarListener {
	private Context context;
	private WebView webView;
	private float scale;//尺寸百分比
	public ClauseCenter(Context context) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
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
		
		BaseBarView baseBarView = new BaseBarView(context, "隐私条款", false,true, this);
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
		webView.loadUrl(URLConstant.CLAUSE_URL);
	
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
