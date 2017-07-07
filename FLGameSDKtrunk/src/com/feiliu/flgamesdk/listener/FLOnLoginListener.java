package com.feiliu.flgamesdk.listener;

public interface FLOnLoginListener {
	/**
	 * 登录成功回调事件
	 * @param userId
	 * @param Sign
	 * @param Timestamp
	 */
	public void onLoginComplete(String userId,String Sign,String Timestamp);
	/**
	 * @param code状态码 0表示用户主动取消，1表示其它问题
	 */
	public void onLoginCancel();//表示用户主动取消    主要通过登录框的关闭按钮通知
	public void onLoginFailed();//网络等问题导致失败
	
}

