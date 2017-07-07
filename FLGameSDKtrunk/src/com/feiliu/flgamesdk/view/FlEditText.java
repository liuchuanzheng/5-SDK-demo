package com.feiliu.flgamesdk.view;

import com.feiliu.flgamesdk.utils.UiSizeUtil;

import android.content.Context;
import android.widget.EditText;

/**
 * @author 对edittext的统一管理。设置一些通用的属性
 *
 */
public class FlEditText extends EditText{

	private Context context;
	private float scale;

	public FlEditText(Context context) {
		super(context);
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		UniteSettings();
	}

	/**
	 * 统一设置
	 */
	private void UniteSettings() {
		this.setPadding((int)(20*scale),(int)(20*scale), (int)(20*scale), (int)(20*scale));
	}
	
}
