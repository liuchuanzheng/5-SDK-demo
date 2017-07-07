package com.feiliu.flgamesdk.global;


/**
 * @author 刘传政
 * gameSDK中用到的所有URL常量
 */
public class URLConstant {
	/**
	 * 激活设备,登录等
	 * @return
	 */
	private static String setLoginPath() {
		// 开关控制登录服务器地址
		String url = "https://dolphin.feiliuchengdu.com";// 正式地址
		
		if (SDKConfigure.ISTESTMODE) {
			url = "http://test.dolphin.feiliu.com:8100";//测试服地址
		}
		return url;
	}
	/**
	 * 支付
	 * @return
	 */
	private static String setPayPath() {
		// 开关控制支付服务器地址
		String url = "https://pay5.feiliuchengdu.com/paysdk";// 正式地址
		
		if (SDKConfigure.ISTESTMODE) {
			url = "http://54.222.142.17:8080/paysdk";//测试服地址
		}
		return url;
	}
	/**
	 * 获取支付界面地址和支付通道列表的url
	 */
	public final static String GET_PAYURL_URL = setPayPath() + "/order/getPaycenterUrl";
	/**
	 * 获取订单界面地址和列表数据的url
	 */
	public final static String GET_ORDERLIST_URL = setPayPath() + "/order/getOrderList";
	/**
	 * 获取三方支付参数的url
	 */
	public final static String GET_PAYPARAMETER_URL = setPayPath() + "/order/pay";
	/**
	 * 登录，注册，修改密码等于账号有关的URL
	 */
	public final static String ACCOUNT_URL = setLoginPath();
	/**
	 * 设备激活URL
	 */
	public final static String REQUEST_DEVICE_ACTIVE_URL = setLoginPath();
	/**
	 * 隐私协议URL
	 */
	public final static String CLAUSE_URL = "https://app.feiliuchengdu.com/gamesdk/secretprotocol.html";
	/**
	 * 社区URL
	 */
	//public final static String CONMUNITY_URL = "https://app.feiliuchengdu.com/gamesdk/sdktool/main.html";
	public final static String CONMUNITY_URL = setLoginPath()+"/sdktool";
	/**
	 * 客服URL
	 */
	public final static String SERVICE_URL = "https://game.feiliuchengdu.com/kefu/feedback2.htm";
	/**
	 * 强更URL
	 */
	public final static String FORCEUPDATE_URL = setLoginPath() + "/update/check";
}
