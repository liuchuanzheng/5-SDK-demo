package com.feiliu.flgamesdk.utils;

import android.app.Activity;
import android.content.Context;

/**
 * @author Administrator
 * 主线程toast
 *
 */
public class UIThreadToastUtil {
	public static void show(final Context context,final String tips){
		 ((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtil.showShort(context, tips);
				}
			});
		
	}
}
