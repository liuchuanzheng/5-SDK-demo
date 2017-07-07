package com.feiliu.flgamesdk.view.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.feiliu.flgamesdk.FlSplashManager;
import com.feiliu.flgamesdk.listener.FLOnSplashFinishListenter;
import com.feiliu.flgamesdk.log.MyLogger;
/*
 * <activity
            android:name="com.feiliu.flgamesdk.view.activity.FLSplashAndPermissionsActivity"
            android:configChanges="orientation|keyboardHidden"
            android:screenOrientation="landscape"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
            android:windowSoftInputMode="stateHidden" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
                
            </intent-filter>
            <meta-data android:name="splashImageName" android:value="fl_splash.png" />
            <meta-data android:name="splashDuration" android:value="5000" />
        </activity>
        <activity
            android:name="xxx.GameActivity"
            android:label="@string/title_activity_game"
            android:screenOrientation="landscape"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
            <intent-filter>

                <action android:name="feiliu.MAIN" />

                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
 */
/**
 * @作者: 刘传政
 * @时间:2017-6-15 下午5:26:22
 * @QQ: 105237416
 * @电话: 18501231486
 * @作用:闪屏和6.0权限处理
 */
public class FLSplashAndPermissionsActivity extends MPermissionsActivity {
	private Activity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		initView();
		showSplash();
		
	}

	/**
	 * 
	 * @返回值类型: void
	 * @作者: 刘传政
	 * @时间:2017-6-23 上午11:41:53
	 * @QQ: 1052374416
	 * @电话: 18501231486
	 * @作用:设置一个默认的布局
	 * @注意事项:
	 */
	private void initView() {
		TextView textView = new TextView(context);
		//textView.setText("权限设置界面默认提示语");
		textView.setBackgroundColor(Color.WHITE);
		RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(textViewParams);
		//textView.setGravity(Gravity.CENTER);
		setContentView(textView);
	}
	
    /**
     * 
     * @返回值类型: void
     * @作者: 刘传政
     * @时间:2017-6-23 上午11:22:14
     * @QQ: 1052374416
     * @电话: 18501231486
     * @作用:申请权限
     * @注意事项:
     */
    private void permission() {
    	//清单文件的cp权限
    	String cpPermissions = getMetaDataStringFromActivity(context,"cpPermissions");
    	MyLogger.lczLog().i("cp申请的权限" + cpPermissions);
    	String[] temp=cpPermissions.split(";");
    	//SDK必须的权限
    	String[] strings = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE};
    	//合并两个权限
    	String[] c= new String[temp.length+strings.length];  
    	System.arraycopy(temp, 0, c, 0, temp.length);  
    	System.arraycopy(strings, 0, c, temp.length, strings.length);  
    	for (String string : c) {
    		MyLogger.lczLog().i(string);
		}
    	requestPermission(c, 1);
	}

	/**
	 * 
	 * @返回值类型: void
	 * @作者: 刘传政
	 * @时间:2017-6-23 上午11:21:55
	 * @QQ: 1052374416
	 * @电话: 18501231486
	 * @作用:闪屏
	 * @注意事项:
	 */
	private void showSplash() {
    	FlSplashManager mFlSplashManager = new FlSplashManager();
		String splashImageName = getMetaDataStringFromActivity(context,"splashImageName");
		int splashDuration = getMetaDataIntFromActivity(context,"splashDuration");
		mFlSplashManager.showFLSplash(context, splashImageName, splashDuration, new FLOnSplashFinishListenter() {

			@Override
			public void doAction(Context context) {
				//start cp game activity		
				((Activity)context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//startGame();
						permission();
					}
				});
			}
		});
	}

	/**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 1:
            	startGame();
                break;
        }

    }

    private void startGame() {
        Intent intent = new Intent();
        intent.setAction("feiliu.MAIN");
        intent.setPackage(this.getPackageName());
        this.startActivity(intent);
        this.finish();
    }
    
	/**
	 * 获取清单文件的meta_data内容 string from Activity meta-data
	 * @param activity
	 * @param key
	 * @return
	 */
	public static String getMetaDataStringFromActivity(Activity activity,String key){
		
		ActivityInfo activityInfo;
		try {
			activityInfo = activity.getPackageManager().getActivityInfo(activity.getComponentName(),PackageManager.GET_META_DATA);
			String msg=activityInfo.metaData.getString(key);
			return msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取清单文件的meta_data内容 string from application meta-data
	 * @param activity
	 * @param key
	 * @return
	 */
	public static String getMetaDataString(Context activity,String key){
		
		ApplicationInfo appInfo;
		try {
			appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),PackageManager.GET_META_DATA);
			String msg=appInfo.metaData.getString(key);
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取清单文件的meta_data内容 int from Activity meta-data
	 * @param activity
	 * @param key
	 * @return
	 */
	public static int getMetaDataIntFromActivity(Activity activity,String key){
		
		ActivityInfo activityInfo;
		try {
			activityInfo = activity.getPackageManager().getActivityInfo(activity.getComponentName(),PackageManager.GET_META_DATA);
			int msg = activityInfo.metaData.getInt(key);
			return msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 * 获取清单文件的meta_data内容 int
	 * @param activity
	 * @param key
	 * @return
	 */
	public static int getMetaDataInt(Context activity,String key){
		
		ApplicationInfo appInfo;
		try {
			appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),PackageManager.GET_META_DATA);
			int msg = appInfo.metaData.getInt(key);
			return msg;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		super.onKeyDown(keyCode, event);
		return false;
	}
}