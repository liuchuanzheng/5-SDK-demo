package com.feiliu.flgamesdk.bean;

import java.util.List;

import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.LoginStatus;
import com.feiliu.flgamesdk.utils.ToastUtil;

import android.content.Context;

/**
 * @author liuchuanzheng
 * 最近一个账号的信息
 *
 */
public class UserInfo {
	private Context context;
	private String userId;
	private String userType;
	
	public UserInfo(Context context) {
		this.context = context;
		readUserInfo();
	}
	private void readUserInfo() {
		if (LoginStatus.status) {
			AccountDao accountDao = new AccountDao(context);
			List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
			if (accountDBBeans.size() >0) {
				AccountDBBean recentAccountDBBean = accountDBBeans.get(0);
				userId = recentAccountDBBean.getUserId();
				userType = recentAccountDBBean.getUserType();
			}
		}else {
			//提示对方技术必须在登录成功后才能调用支付方法。
			ToastUtil.showLong(context, "请确认登录成功后在调用此方法");
		}
		
	}
	public String getUserId() {
		if (null == userId) {
			userId = "";
		}
		return userId;
	}
	public String getUserType() {
		if (null == userType) {
			userType = "";
		}
		return userType;
	}
	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userType=" + userType + "]";
	}
	
	
	
	
	
	
}
