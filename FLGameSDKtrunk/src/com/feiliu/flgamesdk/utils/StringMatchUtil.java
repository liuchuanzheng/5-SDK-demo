package com.feiliu.flgamesdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断字符串是否符合规则的工具类
 * @author 刘传政
 *
 */
public class StringMatchUtil {
	/** 
	 * 描述：是否是邮箱. 
	 * 
	 * @param str 指定的字符串 
	 * @return 是否是邮箱:是为true，否则false 
	 */  
	public static Boolean isEmail(String str) { 
		//此方法可能导致程序无响应。废除
	    /*Boolean isEmail = false;  
	    String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";  
	    if (str.matches(expr)) {  
	        isEmail = true;  
	    }  
	    return isEmail;  */
		Pattern pattern = Pattern    
		            .compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");    
		Matcher matcher = pattern.matcher(str);    
		  
		if (matcher.matches()) {    
		    return true;    
		}    
		    return false; 
	}  
	/** 
	 * 判断是否是手机号 
	 * @param phone 
	 * @return 
	 */  
	public static boolean isPhone(String phone) {    
	    Pattern pattern = Pattern    
	            .compile("1\\d{10}");    
	    Matcher matcher = pattern.matcher(phone);    
	  
	    if (matcher.matches()) {    
	        return true;    
	    }    
	    return false;    
	} 
	/**
	 * 6-16位的数字、字母或下划线组合
	 * @param str
	 * @return
	 */
	public static Boolean isPassword(String str) {  
	    Boolean isPassword = false;  
	    String expr = "^[0-9a-zA-Z_]{6,16}$";
	    if (str.matches(expr)) {  
	    	isPassword = true;  
	    }  
	    return isPassword;  
	} 
	/**
	 * 是否是中文
	 * @param str
	 * @return
	 */
	public static Boolean isChinese(String str) {  
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
	    Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	} 
}
