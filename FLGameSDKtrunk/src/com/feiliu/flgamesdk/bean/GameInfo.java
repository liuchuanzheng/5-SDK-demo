package com.feiliu.flgamesdk.bean;

import java.io.InputStream;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.feiliu.flgamesdk.utils.AssetsUtil;
import com.feiliu.flgamesdk.utils.StreamTools;
import com.google.gson.Gson;

public class GameInfo {
	private Context context;
	private String appId = "";
	private String appKey = "";
	private String companyId = "";
	private String appVersion = "";
	private String versionCode = "";
	private String coopId = "";
	public GameInfo(Context context) {
		this.context = context;
		initGameInfo();
	}
	private void initGameInfo() {
		ApplicationInfo appInfo;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			setAppId(Integer.toString(appInfo.metaData.getInt("FLGAMESDK_APP_ID")));
			String tFLGAMESDK_APP_KEY=appInfo.metaData.getString("FLGAMESDK_APP_KEY");
			setAppKey(tFLGAMESDK_APP_KEY);
			setCompanyId(Integer.toString(appInfo.metaData.getInt("FLGAMESDK_COMPANY_ID")));
			setAppVersion(info.versionName);
			setVersionCode(info.versionCode+"");
			String mt_CoopId=getCoopid(context);//文件中获取
			if (mt_CoopId.equals("-1")) {//如果fl_channel.conf的渠道号获取错误,从清单文件中读取
				mt_CoopId=""+appInfo.metaData.getInt("FLGAMESDK_COOP_ID");
			}
			setCoopId(mt_CoopId);
		} catch (Exception e) {
			Toast.makeText(context, "metaData 配置信息不正确！ ", Toast.LENGTH_LONG).show();
		}
	}
	/**
	 * 从assets的fl_channel.conf中获取渠道号
	 * @param context
	 * @return
	 */
	private String getCoopid(Context context){
		String mCoopId = "-1";
		InputStream is = AssetsUtil.getInputStreamFromAssets(context, "fl_channle.conf");
		try {
			String json = StreamTools.readStream(is);
			Gson gson = new Gson();
			AssetsFlChannleInfo mLGameRead = gson.fromJson(json, AssetsFlChannleInfo.class);
			mCoopId=mLGameRead.getFLGAMESDK_COOP_ID();
			
		} catch (Exception e) {
			
		}
		return mCoopId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getCoopId() {
		return coopId;
	}
	public void setCoopId(String coopId) {
		this.coopId = coopId;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionCode() {
		return versionCode;
	}
	
}
