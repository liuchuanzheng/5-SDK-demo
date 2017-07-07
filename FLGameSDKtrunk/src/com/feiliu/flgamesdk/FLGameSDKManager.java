package com.feiliu.flgamesdk;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Global;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.bean.UserInfo;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.forceupdate.FLForceUpdateManager;
import com.feiliu.flgamesdk.global.FlToolBarControler;
import com.feiliu.flgamesdk.global.LoginStatus;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.listener.FLOnAccountManagerListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.listener.FLOnPayListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.DeviceActiveRequest;
import com.feiliu.flgamesdk.net.request.GuestLoginRequest;
import com.feiliu.flgamesdk.net.request.NormalLoginRequest;
import com.feiliu.flgamesdk.pay.ShowActivityPayCenter;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.activity.toolbar.FlToolBar3;
import com.feiliu.flgamesdk.view.popupwindow.BindTipsPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.Login2PopWindow;
import com.feiliu.flgamesdk.view.popupwindow.LoginNoPasswordPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.LoginPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.MenuPopWindow;

/**
 * @author 刘传政
 *gameSDK的主类。管理暴露给cp的接口
 */
public class FLGameSDKManager {
	private final Context mContext;
	private int FLAccountStatus = 0;// 0为普通登录,1为初始化,2游客登录
	private GameInfo gameInfo;
	private FLOnLoginListener mLoginListener = null;
	private FlToolBar3 flToolBar;
	private FLOnAccountManagerListener fLOnAccountManagerListener = null;
	private FLOnPayListener flOnPayListener = null;
	/**
	 * @param context
	 * 传递上下文，用于之后所有用到上下文的地方。
	 * <br/>(暴露给cp)
	 */
	public FLGameSDKManager(Context context){
		this.mContext = context;
		initSDK();
	}
	/**
	 * gameSDK的初始化
	 * <br/>包括激活设备，游戏强更（这是与cps打包系统相关的功能，cps设置游戏的某一个强更版本后，所有游戏运行时都会强制更新到这个版本）
	 */
	private void initSDK() {
		cpsGameVersionUpdate();//强更。游戏版本更新，与cps系统有关。
		//SDK初始化
		SPUtils mSpUtils = new SPUtils(mContext, SPConstant.DEVICE);
		int deviceId = mSpUtils.getInt("deviceId",0);
		if (0 == deviceId) {
			//联网激活
			DeviceActiveRequest deviceActiveRequest = new DeviceActiveRequest(mContext, null, null);
			deviceActiveRequest.post();
		}
		FLAccountStatus = 1;//状态为初始化
		
	}
	private void cpsGameVersionUpdate() {
		// TODO  强更功能
		new FLForceUpdateManager(mContext);
	}
	/**
	 * 展示飞流球菜单同样的功能界面
	 * <br/>只有在飞流球无法展示的情况下才能调用此方法
	 * <br/>(暴露给cp)
	 */
	public void showMenuWindow(){
		new MenuPopWindow(mContext);
	}
	/**
	 * 获取飞流公司发送给cp的配置参数为bean
	 */
	private GameInfo getGameInfo() {
		return new GameInfo(mContext);//创建对象的过程中已经检验了是否配置正确
	}
	/**
	 * 获取最近一个账号的信息。
	 */
	public UserInfo getUserInfo() {
		return new UserInfo(mContext);
	}
	/**
	 * 设置地址是正式服务器还是测试服务器
	 * <br/>(暴露给cp)
	 * @param IsTestMode
	 *            true:测试服务器 false:正式服务器
	 * @return: void
	 */

	public void setIsTestMode(boolean IsTestMode) {
		SDKConfigure.ISTESTMODE = IsTestMode;
	}
	/**
	 * 设置是否为调试模式（是否打印log）
	 * <br/>(暴露给cp)
	 * @param IsTestMode
	 *            true:打印log false:不打印log
	 * @return: void
	 */

	public void setIsDebugMode(boolean IsDebugtMode) {
		MyLogger.logFlag = IsDebugtMode;
	}
	
	/**
	 * 设置登录窗口必要的地方是否有关闭按钮。点击关闭按钮后可接收到登录取消的回调
	 * <br/>(暴露给cp)
	 * @param close
	 */
	public void showCloseButton() {
		SDKConfigure.haveCloseButton = true;
	}
	
	/**
	 * 支付
	 * <br/>暴露给cp
	 * @param screen 支付界面类型
	 * @param cpOrderId cp订单号
	 * @param amount 订单金额（分）
	 * @param goodsId 商品id/名称
	 * @param roleId 角色id
	 * @param groupId  游戏区服
	 * @param merPriv 透传私有参数
	 * @param cpNotifyUrl 支付订单通知cp地址
	 */
	public void pay(String screen,String cpOrderId,String amount,String goodsId,String roleId,String groupId,String merPriv,String cpNotifyUrl){
		if (LoginStatus.status) {
			//如果是登录状态
			AccountDao accountDao = new AccountDao(mContext);
			List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
			AccountDBBean recentAccountDBBean = accountDBBeans.get(0);
			if ("0".equals(recentAccountDBBean.getUserType())) {
				//如果是游客
				SPUtils spUtils = new SPUtils(mContext, SPConstant.GIFTSTATUS);
				String giftStatus = spUtils.getString("giftStatus", "0");
				new BindTipsPopWindow(mContext, giftStatus, mLoginListener, null,"payToNomalAccount");
				return;
			}
			mPay(screen,cpOrderId,amount, goodsId, roleId, groupId, merPriv, cpNotifyUrl);
		}else {
			//提示对方技术必须在登录成功后才能调用支付方法。
			ToastUtil.showLong(mContext, "请确认登录成功后在调用此方法");
		}
	};
	
	private void mPay(String screen,String cpOrderId,String amount,String goodsId,String roleId,String groupId,String merPriv,String cpNotifyUrl) {
		//getUrlAndList(screen,cpOrderId, appId, appName, userId, amount, goodsId, roleId, groupId, merPriv, cpNotifyUrl);
		new ShowActivityPayCenter(mContext,flOnPayListener,screen,cpOrderId, amount, goodsId, roleId, groupId, merPriv, cpNotifyUrl);
	}
	
	/**
	 * 获取当前账号是否成年
	 * <br/>(暴露给cp)
	 * @return 0 未知  1 未成年 2 成年
	 */
	public String getAgeStatus(){
		String ageStatus = "0";
		SPUtils spUtils = new SPUtils(mContext, SPConstant.USERINFO);
		ageStatus = spUtils.getString("ageStatus", "0");
		if (!LoginStatus.status) {
			ageStatus = "0";
		}
		return ageStatus;
	};
	
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
	 * 设置飞流球的账号监听。主要用于接收退出账号按钮的回调
	 * <br/>(暴露给cp)
	 * @param fLOnAccountManagerListener
	 */
	public void setFLOnAccountManagerListener(FLOnAccountManagerListener fLOnAccountManagerListener) {
		this.fLOnAccountManagerListener = fLOnAccountManagerListener;
		
	}
	/**
	 * 设置支付结果的监听。
	 * <br/>(暴露给cp)
	 * @param flOnPayListener
	 */
	public void setFlOnPayListener(FLOnPayListener flOnPayListener){
		this.flOnPayListener = flOnPayListener;
	}
	/**
	 * 展示飞流球
	 *  <br/>(暴露给cp)
	 */
	public void showFLBall(Context context,int place) {
		mShowToolBar(context,place,mLoginListener,fLOnAccountManagerListener);
	}
	/**
	 * 隐藏飞流球
	 *  <br/>(暴露给cp)
	 */
	private void hideToolBar() {
		mHideToolBar();
	}
	
	public void mHideToolBar() {
		if (null != flToolBar) {
			flToolBar.hide();
		}
	}
	private void mShowToolBar(Context context, int toolBarPlace ,FLOnLoginListener loginListener, FLOnAccountManagerListener fLOnAccountManagerListener) {
		if (null == flToolBar) {
			flToolBar = new FlToolBar3(context, toolBarPlace,loginListener,fLOnAccountManagerListener);
			FlToolBarControler.flToolBar = flToolBar;
		}
		flToolBar.show();
		
		
	}
	/**
	 * cp手动控制展示飞流球
	 *  <br/>(暴露给cp)
	 */
	public void onResume(Context context) {
		mOnResume(context);
	}
	private void mOnResume(Context context) {
		if (null != flToolBar) {
			flToolBar.handShow();
		}
		
		
		
	}
	/**
	 * cp手动控制展示飞流球
	 *  <br/>(暴露给cp)
	 */
	public void onPause(Context context) {
		mOnPause(context);
	}
	private void mOnPause(Context context) {
		if (null != flToolBar) {
			flToolBar.handHide();
		}
		
		
		
	}
	/**
	 * 普通登录功能
	 * @param haveGestLogin 是否附带使用游客登录功能。true为使用
	 * @param haveFastlogin 是否附带使用快速登录功能（可以不许要任何点击的登录上次账号）。true为使用
	 * <br/>(暴露给cp)
	 */
	public void FlLogin(Boolean haveFastlogin,Boolean haveGestLogin,FLOnLoginListener flOnLoginListener) {
		//为了保证初始化时强更失败.登录时再走一遍强更
		//cpsGameVersionUpdate();
		// 登录功能之前的逻辑判断
		if (mLoginListener != null) {
			mLoginListener = flOnLoginListener;
		}
		//flToolBar = null;//这样会导致飞流球里的广播接收者无法注销
		mFlLogin(haveFastlogin,haveGestLogin,flOnLoginListener);
	}
	/**
	 * 内部真正的登录方法
	 * @param haveGestLogin
	 * @param flOnLoginListener 
	 */
	private void mFlLogin(Boolean haveFastlogin,Boolean haveGestLogin, FLOnLoginListener flOnLoginListener) {
		//登录方式详细分为八种情况
		AccountDao accountDao = new AccountDao(mContext);
		List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
		if (haveGestLogin) {
			accountDBBeans = accountDao.findAllIncludeGuest();
		}else {
			accountDBBeans = accountDao.findAll();
		}
		
		
		if (haveFastlogin) {
			if (haveGestLogin) {
				if (accountDBBeans.size() > 0) {
					//第一种情况    
					//直接联网登录
					//如果有历史，就去最近一个账号直接登录
					AccountDBBean recentAccountDBBean = accountDBBeans.get(0);
					if ("quit".equals(recentAccountDBBean.getQuitState())) {
						new LoginPopWindow(mContext,"username", "pwd",flOnLoginListener );
						return;
					}
					if ("0".equals(recentAccountDBBean.getUserType())) {
						//0代表数据库中存储的游客登录的账号类型
						GuestLoginRequest guestLoginRequest = new GuestLoginRequest(mContext, flOnLoginListener, null);
						guestLoginRequest.post();
					}else if ("1".equals(recentAccountDBBean.getUserType())) {
						NormalLoginRequest normalLoginRequest = new NormalLoginRequest(mContext, flOnLoginListener
								,true, recentAccountDBBean.getUserName(), recentAccountDBBean.getPassword(), null);
						normalLoginRequest.post();
					}
				}else {
					//第二种情况
					//创建LoginPopWindow
					new LoginPopWindow(mContext,"username", "pwd",flOnLoginListener );
				}
			}else {
				if (accountDBBeans.size() > 0) {
					//第三种情况    
					//直接联网登录
					AccountDBBean recentAccountDBBean = accountDBBeans.get(0);
					if ("quit".equals(recentAccountDBBean.getQuitState())) {
						List<AccountDBBean> noGuestBeans = accountDao.findAll();
						new LoginNoPasswordPopWindow(mContext, noGuestBeans, flOnLoginListener,null);
						return;
					}
					if ("0".equals(recentAccountDBBean.getUserType())) {
						//0代表数据库中存储的游客登录的账号类型
						GuestLoginRequest guestLoginRequest = new GuestLoginRequest(mContext, flOnLoginListener, null);
						guestLoginRequest.post();
					}else if ("1".equals(recentAccountDBBean.getUserType())) {
						NormalLoginRequest normalLoginRequest = new NormalLoginRequest(mContext, flOnLoginListener
								,true, recentAccountDBBean.getUserName(), recentAccountDBBean.getPassword(), null);
						normalLoginRequest.post();
					}
				}else {
					//第四种情况
					//创建Login2PopWindow
					new Login2PopWindow(mContext, null, flOnLoginListener, null,"noPop");
				}
			}
		}else {
			if (haveGestLogin) {
				if (accountDBBeans.size() > 0) {
					//第五种情况    
					//创建LoginPopWindow
					new LoginPopWindow(mContext, "username", "pwd",flOnLoginListener );
				}else {
					//第六种情况
					//创建LoginPopWindow
					new LoginPopWindow(mContext, "username", "pwd",flOnLoginListener );	
				}
			}else {
				if (accountDBBeans.size() > 0) {
					//第七种情况    
					//创建LoginNoPasswordPopWindow
					new LoginNoPasswordPopWindow(mContext, accountDBBeans, flOnLoginListener,null);
				}else {
					//第八种情况
					//创建Login2PopWindow
					new Login2PopWindow(mContext, null, flOnLoginListener, null,"noPop");
				}
			}
		}
	}
}
