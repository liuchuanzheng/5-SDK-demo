package com.feiliu.flgamesdk.global;

/**
 * @author liuchuanzheng
 *全局变量标记当前账号的登录状态。每次游戏初始化时变为false。便于提示cp技术在登录成功后才能调用pay方法。
 */
public class LoginStatus {
	public static boolean status = false;
}
