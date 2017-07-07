package com.feiliu.flgamesdk.db;

/**
 * 去读flaccount数据库得到的bean
 * @author 刘传政
 *
 */
public class AccountDBBean {
	private String userId;
	private String userType;
	private String userName;
	private String password;
	private String nickName;
	private String phone;
	private String email;
	private String quitState;
	public String getQuitState() {
		return quitState;
	}
	public void setQuitState(String quitState) {
		this.quitState = quitState;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
