package com.feiliu.flgamesdk.global;

import com.feiliu.flgamesdk.view.activity.toolbar.FlToolBar3;

public  class FlToolBarControler {
	public static  FlToolBar3 flToolBar;
	/**
	 * 显示飞流球  任何地方使用(必须在cp调用了显示飞流球方法之后)
	 */
	public static void show(){
		if (null == flToolBar) {
			return;
		}
		flToolBar.show();
	};
	/**
	 * 隐藏飞流球  任何地方使用(必须在cp调用了显示飞流球方法之后)
	 */
	public static void hide(){
		if (null == flToolBar) {
			return;
		}
		flToolBar.hide();
	};
}
