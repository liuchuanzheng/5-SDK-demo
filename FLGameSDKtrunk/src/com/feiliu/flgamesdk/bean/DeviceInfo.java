package com.feiliu.flgamesdk.bean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.net.utils.NetworkUtils;
import com.feiliu.flgamesdk.utils.SPUtils;

/**
 * 完成以下功能：读到设备的IMEI IMSI 型号 出厂串号 CPU类型 WIFI的MAC 设备的语言 厂商
 * 
 * @version v1.0
 * 
 * @author 刘传政
 * 
 */
public class DeviceInfo {

	static TelephonyManager mTelephonyMgr;
	public static String IMEI;
	public static String IMSI;
	/**
	 * 获取设备国家
	 * @param context 
	 * @return 国家
	 */
	public static String getCountry(Context context){
		String locale = context.getResources().getConfiguration().locale.getCountry();
		return locale;
	}
	
	/**
	 * 得到手机或pad的IMEI  代表联网请求参数的deviceKey
	 * <br/>NEW Android SDK 5 deviceKey: 
	 *	<br/>Android传IMEI，如果取不到取MAC地址，如果MAC也无，生成FL开头的字符串：
	 *	<br/>FL（2位） + appid(6位) + 时间戳（13位） + 随机数（6位）
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
	      String deviceKey;
	      SPUtils spUtils = new SPUtils(context, SPConstant.DEVICE);
	      deviceKey = spUtils.getString("deviceKey", "");//先sp获取，节省效率
	      if ("".equals(deviceKey)) {//sp取不到。就正式去从设备身上获取
	    	  //先获取imei
		      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		      String IMEI = tm.getDeviceId();
		      deviceKey = IMEI;
		      if (null == deviceKey) {
		    	  //获取mac地址
		    	  String macAddress = "";
		    	  try{
						WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
						WifiInfo wifiInfo = wifiManager.getConnectionInfo();
						
						if (wifiInfo == null) {
							macAddress = "";
						}else {
							macAddress = wifiInfo.getMacAddress();
						}
						 
					}
					catch (Exception e){
						macAddress = "";
					}
		    	  deviceKey = macAddress;
		      }
		      if (deviceKey.equals("")) {
		    	  //随机生成字符串
		    	  GameInfo gameInfo = new GameInfo(context);
		    	  String appid = gameInfo.getAppId();
		    	  String timeAndRandom = System.currentTimeMillis()+String.valueOf((1000000+1000000*Math.random())).substring(1, 7);
		    	  deviceKey = "FL"+appid+timeAndRandom;
		      }
		      //从设备身上取到后，存储在sp。便于下次获取直接sp获取
		      spUtils.putString("deviceKey", deviceKey);
	      }
	      
	        return deviceKey;
		 
	 }
	/**
	 * 得到手机或pad的IMEI  代表联网请求参数的idfa
	 *	<br/>Android传IMEI.获取不到也没关系。得到什么传什么
	 * @param context
	 * @return
	 */
	public static String getIdfa(Context context) {
	      String idfa;
	      //获取imei
		  TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		  idfa = tm.getDeviceId();
	      return idfa;
	 }
	/**
	 * 获取当前的网络类型(WIFI,2G,3G,4G)
	 * @param context
	 * @return
	 */
	public static String getApn(Context context){
		String apn = null;
		apn = NetworkUtils.getNetworkTypeName(context);
		return apn;
	}
	
	/**
	 * 获取设备系统类型
	 * 此处只能是android 所以返回内容固定了
	 * @return Android或者ios
	 */
	public static String getOS(){
		return "android";
	}
	 /**
     * 获取设备系统版本号
     *
     * @return 设备系统版本号
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.SDK_INT+"";
    }
    /**
     * 获取设备具体型号
     * <p>如Xiaomi</p>
     *
     * @return 设备厂商
     */

    public static String getPlatformModel() {
    	String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }


	/**
     * 判断设备是否是手机
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isPhone(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
	public final static String LANGUAGE_TW = "30";
	public final static String Language_ZH = "31";
	public final static String Language_EN = "1";

	/**
	 * 系统语言 根据协议 需要上传阿拉伯数字
	 * 
	 * @return 1表示手机显示语言为非中文。31表示手机语言为中文。30为繁体中文。
	 */
	public static String getMobileLanguage() {
		String Language = Locale.getDefault().getLanguage();
		if (Language.equalsIgnoreCase("zh")) {// 中文
			return Language_ZH;
		} else if (Language.equalsIgnoreCase("en")) {// 英文
			return Language_EN;
		} else if (Language.equalsIgnoreCase("tw")) {
			Language = LANGUAGE_TW;
		}
		return Language_EN;
	}

	/**
	 * 系统语言是否是英文
	 * 
	 * @return true or false
	 */
	public static boolean isEnglish() {
		try {
			if (Locale.getDefault().getLanguage().equalsIgnoreCase("en"))
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	/***
	 * 返回手机类型
	 * @param context 
	 * 
	 * @return 手机类型  pad  phone
	 * */
	public static String getPhoneMODEL(Context context) {
		if (isPad(context)) {
			return "pad";
		}
		return "phone";
	}
	/** 
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android 
	 * 
	 * @param context 
	 * @return 平板返回 True，手机返回 False 
	 */  
	public static boolean isPad(Context context) {  
	    return (context.getResources().getConfiguration().screenLayout  
	            & Configuration.SCREENLAYOUT_SIZE_MASK)  
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;  
	}  

	/***
	 * 返回手机CPU型号。可用作兼容ARM,MIPS型号CPU的参数
	 * 
	 * @return CPU型号
	 * */
	public static String getCpuType() {

		return android.os.Build.CPU_ABI;
	}

	/*****
	 * 取设备厂商名称
	 * 
	 * @return 设备厂商名称
	 */
	public static String getMANUFACTURER() {
		return android.os.Build.MANUFACTURER;
	}

	public static String phoneVerionCode() {
		return "" + android.os.Build.VERSION.SDK_INT;
	}

	/***
	 * 返回厂商定的设备串号
	 * 
	 * Android系统2.3版本以上可以通过下面的方法得到Serial Number，且非手机设备也可以通过该接口获取。
	 * 
	 * String SerialNumber = android.os.Build.SERIAL;
	 * 
	 * @return 返回厂商定的设备串号 .出错的情况下返回null。
	 * */
	public static String getPhoneSerial() {
		String serial = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception ignored) {
			return null;
		}
		return serial;
	}

	/**
	 * 获取wifi的MAC 地址。
	 * 
	 * @return wifi的MAC 地址。如果没有WIFI设备，返回NULL
	 * */
	public static String getWifiMac(Context aContext) {
		WifiManager wifiMan = (WifiManager) aContext.getSystemService(Context.WIFI_SERVICE);
		if (null == wifiMan) {
			return null;
		}
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		if (null == wifiInf) {
			return null;
		}
		String macAddr = wifiInf.getMacAddress();
		return macAddr;
	}

	/**
	 * ANDROID_ID 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来，
	 * 这个16进制的字符串就是ANDROID_ID，当设备被wipe后该值会被重置。可以通过下面的方法获取：
	 * 
	 * @param context
	 * @return
	 */
	public static String getAndroidId(Context context) {
		String deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
		if (deviceId != null) {
			return deviceId;
		} else {
			return "";
		}
	}

	public static String getCpuModel() {
		String _cpuType = getFileCpuInfo();
		if (null == _cpuType) {
			_cpuType = getCpuModelFromRun();
		}
		if (null != _cpuType) {
			String[] _tmpStr = _cpuType.split(":");
			if ((null != _tmpStr) && (2 >= _tmpStr.length)) {
				_cpuType = _tmpStr[1];
			}
		}
		return _cpuType;
	}

	private static String getCpuModelFromRun() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec("/system/bin/cat /proc/cpuinfo");
			process.waitFor();
		} catch (Exception e) {
			return null;
		}
		InputStream in = process.getInputStream();
		BufferedReader boy = new BufferedReader(new InputStreamReader(in));
		String mystring = null;
		try {
			mystring = boy.readLine();
			while (mystring != null) {
				if (mystring.toLowerCase().indexOf("hardware") >= 0) {
					return mystring;
				}
				mystring = boy.readLine();
			}
		} catch (IOException e) {
		}
		return null;
	}

	private static String getFileCpuInfo() {
		String _cmd_cpuinfo = "/proc/cpuinfo";
		String _cpuHardWare = null;
		try {
			FileReader fr = new FileReader(_cmd_cpuinfo);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((_cpuHardWare = localBufferedReader.readLine()) != null) {
				if (_cpuHardWare.toLowerCase().indexOf("hardware") >= 0) {
					localBufferedReader.close();
					return _cpuHardWare;
				}
			}
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return null;
	}

	public static boolean isEmulator() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec("/system/bin/cat /proc/cpuinfo");
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		InputStream in = process.getInputStream();
		BufferedReader boy = new BufferedReader(new InputStreamReader(in));
		String mystring = null;
		try {
			mystring = boy.readLine();
			while (mystring != null) {
				mystring = mystring.trim().toLowerCase();
				if ((mystring.startsWith("hardware")) && mystring.endsWith("goldfish")) {
					return true;
				}
				mystring = boy.readLine();
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}
}
