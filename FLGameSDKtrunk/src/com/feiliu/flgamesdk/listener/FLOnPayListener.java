package com.feiliu.flgamesdk.listener;

public interface FLOnPayListener {
	/**
	 * 支付回调事件
	 * @param status  0成功，-1失败
	 */
	public void onPayComplete(int status);//支付成功
}
