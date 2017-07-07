package com.feiliu.flgamesdk.bean;

import java.io.InputStream;

import android.content.Context;

import com.feiliu.flgamesdk.utils.AssetsUtil;
import com.feiliu.flgamesdk.utils.StreamTools;

public class SdkInfo {
	private Context context;
	private String sdkVersion = "5.0.0";//sdk版本号  读取配置文件而来
	private String protocol = "1.0";//sdk协议版本号    默认为1.0  手动配置
	public SdkInfo(Context context) {
		this.context = context;
		sdkVersion = SDKVersion(this.context);
	}
	public String getSdkVersion() {
		return sdkVersion;
	}
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * 从assets的fl_SDKVersion.conf中获取版本号
	 * <br/>之后有需要从配置文件中配置的东西可以修改此方法
	 * @param context
	 * @return
	 */
	private String SDKVersion(Context context){
		String SDKVersion = "5.0.0";
		InputStream is = AssetsUtil.getInputStreamFromAssets(context, "fl_SDKVersion.conf");
		try {
			String json = StreamTools.readStream(is).trim();
			SDKVersion = json;
		} catch (Exception e) {
			
		}
		return SDKVersion;
	}
}
