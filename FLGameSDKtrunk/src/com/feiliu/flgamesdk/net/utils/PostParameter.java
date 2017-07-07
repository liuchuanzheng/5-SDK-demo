package com.feiliu.flgamesdk.net.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.feiliu.flgamesdk.bean.DeviceInfo;
import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.bean.SdkInfo;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.utils.SPUtils;

import android.content.Context;

public class PostParameter {
	private Context context;
	public PostParameter(Context context) {
		this.context = context;
	}
	
	/**
	 * 获取post参数的固定提交参数中的deviceInfo参数<br/>
	 * 所有参数都是自动获取手机上的信息，不需要自己配置。
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getDeviceInfoJson() throws JSONException{
		JSONObject holder = new JSONObject();
		holder.put("model", DeviceInfo.getPhoneMODEL(context));
		holder.put("language", DeviceInfo.getMobileLanguage());
		holder.put("country", DeviceInfo.getCountry(context));
		holder.put("deviceKey", DeviceInfo.getIMEI(context));
		holder.put("idfa", DeviceInfo.getIdfa(context));
		holder.put("apn", DeviceInfo.getApn(context));
		holder.put("os", DeviceInfo.getOS());
		holder.put("osVersion", DeviceInfo.getOSVersion());
		holder.put("platformModel", DeviceInfo.getPlatformModel());
		SPUtils mSpUtils = new SPUtils(context, SPConstant.DEVICE);
		
		int deviceId = mSpUtils.getInt("deviceId",0);
		if (0 != deviceId) {
			holder.put("deviceId", deviceId);
		}
		return holder;
	}
	/**
	 * 获取post参数的固定提交参数中的gameInfo参数<br/>
	 * 其中appId，appKey，companyId，coopId为cp接入gameSDK时飞流公司分配给cp公司的一套参数，用于进行一些验证和记录。配置位置在订单文件中<br/>
	 * 而appVersion是cp游戏的版本号。完全由cp自己决定。gameSDK自动读取。
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getGameInfoJson() throws JSONException{
		JSONObject holder = new JSONObject();
		GameInfo gameInfo = new GameInfo(context);
		holder.put("appId", gameInfo.getAppId());
		holder.put("appKey", gameInfo.getAppKey());
		holder.put("companyId", gameInfo.getCompanyId());
		holder.put("appVersion", gameInfo.getAppVersion());
		holder.put("coopId", gameInfo.getCoopId());
		return holder;
	}
	/**
	 * 获取post参数的固定提交参数中的sdkInfo参数<br/>
	 * protocol sdk协议版本号
	 * sdkVersion sdk版本号
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getSDKInfoJson() throws JSONException{
		JSONObject holder = new JSONObject();
		SdkInfo sdkInfo = new SdkInfo(context);
		holder.put("protocol", sdkInfo.getProtocol());
		holder.put("sdkVersion", sdkInfo.getSdkVersion());
		return holder;
	}
}
