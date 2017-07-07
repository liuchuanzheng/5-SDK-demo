package com.feiliu.flgamesdk.listener;

import android.content.Context;
import android.os.Bundle;

/**
 * 闪屏结束的监听
 * @author liuchuanzheng
 *
 */
public interface FLOnSplashFinishListenter {
	/**
	 * 闪屏结束 cp在此方法中执行正式的代码
	 */
	public void doAction(Context context);
}
