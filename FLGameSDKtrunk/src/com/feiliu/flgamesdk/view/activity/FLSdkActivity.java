package com.feiliu.flgamesdk.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.view.activity.center.ClauseCenter;
import com.feiliu.flgamesdk.view.activity.center.CommunityCenter;
import com.feiliu.flgamesdk.view.activity.center.OrderCenter;
import com.feiliu.flgamesdk.view.activity.center.PayCenter;
import com.feiliu.flgamesdk.view.activity.center.ServiceCenter;

/**
 * 第三方登录和支付中心和隐私协议用到的公共Activity 共用Activity的目的是减少CP接入时Activity的注册数量
 * SDK所有需要用到Activity的功能，都可以共用些类
 */
public class FLSdkActivity extends Activity {
	private int fromtype;//打开此activity的来源。根据此来源会展示不同的界面和功能。
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		Intent intent = getIntent();
		fromtype = intent.getIntExtra("fromtype", 0);
		switch (fromtype) {
		case 0://打开隐私条款
			ClauseCenter clauseCenter = new ClauseCenter(context);
			clauseCenter.activityShow();
			break;
		case 1://打开订单界面
			OrderCenter orderCenter = new OrderCenter(context);
			orderCenter.activityShow();
			break;
		case 2://打开社区界面
			CommunityCenter communityCenter = new CommunityCenter(context);
			communityCenter.activityShow();
			break;
		case 3://打开客服界面
			ServiceCenter serviceCenter = new ServiceCenter(context);
			serviceCenter.activityShow();
			break;
		case 4://打开支付界面
			//由于支付界面有三个模版，会使得横竖屏展示不同。所以要强制设置横竖屏。
			//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式  
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏  
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏  
			Bundle bundle = intent.getExtras();
			String screen = bundle.getString("screen");
			if ("0".equals(screen)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏  
			}else if ("1".equals(screen)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为横屏  并且与之前的横屏匹配
			}else if ("2".equals(screen)) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为横屏  并且与之前的横屏匹配
			}
			String jsJson = bundle.getString("jsJson");
			String url = bundle.getString("url");
			
			PayCenter payCenter = new PayCenter(context,jsJson,url,bundle);
			payCenter.activityShow();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean ret_value = false;
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				switch (fromtype) {
				case 0://隐私协议 屏蔽返回键
					ret_value = false;
					break;
				case 1://订单界面屏蔽返回键
					ret_value = false;
					break;
				case 2://社区界面屏蔽返回键
					ret_value = false;
					break;
				case 3://客服界面屏蔽返回键
					ret_value = false;
					break;
				case 4://支付界面屏蔽返回键
					ret_value = false;
					break;
			
				default:
					ret_value = super.onKeyDown(keyCode, event);
					break;
				}
		}
		return ret_value;
	}
}
