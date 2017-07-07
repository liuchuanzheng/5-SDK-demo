package com.feiliu.flgamesdk.listener;

import android.os.Message;

/**
 * @author Administrator
 * 为了能让pop能直接获取到请求数据。
 *
 */
public interface FLOnNetFinishListener {
	/**
	 * 主线程   但不建议做细节操作，不容易管理
	 * @param msg
	 */
	public void OnNetComplete(Message msg);
}

