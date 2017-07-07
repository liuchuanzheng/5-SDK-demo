package com.feiliu.flgamesdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.feiliu.flgamesdk.FLGameSDKManager;
import com.feiliu.flgamesdk.FlSplashManager;
import com.feiliu.flgamesdk.global.FLBallPlace;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.listener.FLOnAccountManagerListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.listener.FLOnPayListener;
import com.feiliu.flgamesdk.listener.FLOnSplashFinishListenter;

public class MainActivity extends Activity implements FLOnAccountManagerListener, FLOnPayListener {
	public Context mContext;
	private FLGameSDKManager mFlGameSDKManager;
	private Button btndenglu;
	private CheckBox cbGuestLogin;
	private CheckBox cbFastLogin;
	private CheckBox cbClose;
	private Boolean isGuestLogin = true;
	private Boolean isFastLogin = false;
	private Boolean isHaveClose = false;
	private FLOnLoginListener mFlOnLoginListener;
	private RelativeLayout rlLoginBefore;
	private RelativeLayout rlLoginSuccess;
	private Button btn_pay;//支付按钮
	private EditText et_name;
	private EditText et_price;
	private EditText et_screen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.activity_main);
		mFlGameSDKManager = new FLGameSDKManager(mContext);
		mFlGameSDKManager.setIsTestMode(false);
		mFlGameSDKManager.setIsDebugMode(true);//设置是否打印log
		mFlGameSDKManager.setFLOnAccountManagerListener(MainActivity.this);
		mFlGameSDKManager.setFlOnPayListener(MainActivity.this);
		findViews();
		mFlOnLoginListener = new FLOnLoginListener() {
			
			@Override
			public void onLoginFailed() {
				Toast.makeText(mContext, "cp接收到了登录失败回调", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLoginComplete(String userId, String Sign, String Timestamp) {
				Toast.makeText(mContext, "cp接收到了登录成功回调", Toast.LENGTH_SHORT).show();
				rlLoginBefore.setVisibility(View.GONE);
				rlLoginSuccess.setVisibility(View.VISIBLE);
				mFlGameSDKManager.showFLBall(mContext, FLBallPlace.Left);
				Toast.makeText(mContext, mFlGameSDKManager.getUserInfo().toString(), Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onLoginCancel() {
				Toast.makeText(mContext, "cp接收到了登录 取消回调", Toast.LENGTH_SHORT).show();
			}
		};
	
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (null != mFlGameSDKManager) {
			mFlGameSDKManager.onPause(mContext);
		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (null != mFlGameSDKManager) {
			mFlGameSDKManager.onResume(mContext);
		}
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	protected void findViews() {
		rlLoginBefore = (RelativeLayout) findViewById(R.id.re_loginbefore);
		rlLoginSuccess = (RelativeLayout) findViewById(R.id.re_loginsuccess);
		
		cbGuestLogin = (CheckBox) findViewById(R.id.cb_guestlogin);
		cbGuestLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isGuestLogin = isChecked;
			}
		});
		cbFastLogin = (CheckBox) findViewById(R.id.cb_fastlogin);
		cbFastLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isFastLogin = isChecked;
			}
		});
		
		cbClose = (CheckBox) findViewById(R.id.cb_close);
		cbClose.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isHaveClose = isChecked;
				if (isHaveClose) {
					mFlGameSDKManager.showCloseButton();
				}else {
					SDKConfigure.haveCloseButton = false;
				}
				
			}
		});
		btndenglu = (Button) findViewById(R.id.button1);
		btndenglu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mFlGameSDKManager.FlLogin(isFastLogin,isGuestLogin,mFlOnLoginListener);
			}
		});
		et_name = (EditText) findViewById(R.id.et_name);
		et_price =(EditText) findViewById(R.id.et_price);
		et_screen = (EditText) findViewById(R.id.et_screen);
		
		btn_pay = (Button) findViewById(R.id.btn_pay);
		btn_pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "获取是否成年结果:" +mFlGameSDKManager.getAgeStatus(), Toast.LENGTH_SHORT).show();
				String cpOrderId = System.currentTimeMillis()+String.valueOf((1000000+1000000*Math.random())).substring(0, 7);
				try {
					int a = Integer.parseInt(et_price.getText().toString().trim());
					if (a>=0) {
						mFlGameSDKManager.pay(et_screen.getText().toString().trim(),cpOrderId,et_price.getText().toString().trim(),
								et_name.getText().toString().trim(),"1164","116","1164111111$23213","http://54.222.142.17:8080/paysdk/notifycp" );
					}else {
						//提示对方技术j金额必须是正整数
						Toast.makeText(mContext, "金额必须是正整数", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					//提示对方技术j金额必须是正整数
					Toast.makeText(mContext, "金额必须是正整数", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	@Override
	public void onLogout() {
		Toast.makeText(mContext, "cp接收到了退出账号回调", Toast.LENGTH_SHORT).show();
		rlLoginBefore.setVisibility(View.VISIBLE);
		rlLoginSuccess.setVisibility(View.GONE);
	}
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pKeyEvent) {
		switch (pKeyCode) {
			case KeyEvent.KEYCODE_BACK:{
				new AlertDialog.Builder(this)
				.setTitle("是否要退出 程序")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						})
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								System.exit(0);
							}
						}).show();
				
	            return true;
			}
			
			default:
				//return super.onKeyDown(pKeyCode, pKeyEvent);
		}
		return false;
	}
	@Override
	public void onPayComplete(int status) {
		//支付结果会回调此方法
		if (0 == status) {
			//支付成功
			Toast.makeText(mContext, "cp接收到了支付成功回调", Toast.LENGTH_SHORT).show();
		}else if (-1 == status) {
			//支付失败
			Toast.makeText(mContext, "cp接收到了支付失败回调", Toast.LENGTH_SHORT).show();
		}
	}
}
