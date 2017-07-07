package com.feiliu.flgamesdk.bean;
/**
 * 读取assets目录下fl_channel.conf文件后数据bean类 用于封装coopid等信息
 * @author liuchuanzheng
 *
 */
public class AssetsFlChannleInfo {
	private String FLGAMESDK_APP_ID;
	private String FLGAMESDK_APP_KEY;
	private String FLGAMESDK_COMPANY_ID;
	private String FLGAMESDK_COOP_ID = "-1";
	
	public String getFLGAMESDK_APP_ID() {
		return FLGAMESDK_APP_ID;
	}
	public void setFLGAMESDK_APP_ID(String fLGAMESDK_APP_ID) {
		FLGAMESDK_APP_ID = fLGAMESDK_APP_ID;
	}
	public String getFLGAMESDK_APP_KEY() {
		return FLGAMESDK_APP_KEY;
	}
	public void setFLGAMESDK_APP_KEY(String fLGAMESDK_APP_KEY) {
		FLGAMESDK_APP_KEY = fLGAMESDK_APP_KEY;
	}
	public String getFLGAMESDK_COMPANY_ID() {
		return FLGAMESDK_COMPANY_ID;
	}
	public void setFLGAMESDK_COMPANY_ID(String fLGAMESDK_COMPANY_ID) {
		FLGAMESDK_COMPANY_ID = fLGAMESDK_COMPANY_ID;
	}
	public String getFLGAMESDK_COOP_ID() {
		return FLGAMESDK_COOP_ID;
	}
	public void setFLGAMESDK_COOP_ID(String fLGAMESDK_COOP_ID) {
		FLGAMESDK_COOP_ID = fLGAMESDK_COOP_ID;
	}
	
}
