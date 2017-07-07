package com.feiliu.flgamesdk.view.activity.center;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.MD5;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;

/**
 * 社区activity的控制中心
 * <br/>设置界面，和功能
 * @author 刘传政
 *
 */
public class CommunityCenter implements BaseBarListener {
	private Context context;
	private WebView webView;
	private float scale;//尺寸百分比
	public CommunityCenter(Context context) {
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
		
		BaseBarView baseBarView = new BaseBarView(context, "游戏社区", false,true, this);
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
		String url = createUrl();
		webView.loadUrl(url);
	
	}
	/**
	 * 拼接URL
	 * @return
	 */
	private String createUrl() {
		AccountDao accountDao = new AccountDao(context);
		List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
		GameInfo gameInfo = new GameInfo(context);
		long tstp = System.currentTimeMillis() / 1000;
		String sign = MD5.getMD5Code("act:bbs" + ",appid:" + gameInfo.getAppId() + ",os:android,tstp:" + tstp
				+ ",uuid:" + accountDBBeans.get(0).getUserId());
		String url = URLConstant.CONMUNITY_URL + "?act=bbs&uuid=" + accountDBBeans.get(0).getUserId() + "&appid="
				+ gameInfo.getAppId() + "&os=android&tstp=" + tstp + "&sign=" + sign;
		MyLogger.lczLog().i("社区url："+url);
		return url;
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
