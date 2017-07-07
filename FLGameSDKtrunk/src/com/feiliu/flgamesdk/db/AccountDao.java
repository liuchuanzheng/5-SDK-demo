package com.feiliu.flgamesdk.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 飞流账号数据库的dao( data access objcet)
 * 增删改查
 */
public class AccountDao {
	private AccountDBOpenHelper helper;
	
	/**
	 * 只有一个有参的构造方法,要求必须传入上下文
	 * @param context
	 */
	public AccountDao(Context context) {
		helper = new AccountDBOpenHelper(context);
	}
	/**
	 * 添加
	 * @param userName 用户名
	 * @param password 密码
	 * @param timestamp 时间戳，用于排序
	 * @return result 添加到数据库的那一行, -1添加失败
	 */
	public long add(String userId,String userName,String password,String nickName,String phone,String email,String userType,String quitState,long time){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values =new ContentValues();
		values.put("userId", userId);
		values.put("userName", userName);
		values.put("password", password);
		values.put("nickName", nickName);
		values.put("phone", phone);
		values.put("email", email);
		values.put("userType", userType);
		values.put("quitState", quitState);
		values.put("time", time);
		long result = db.insert("account", null, values); //组拼sql语句实现的.带返回值
		db.close();//释放资源
		return result;
	}
	
	/**
	 * 删除
	 * @param userId 用户id
	 * @return result 删除了几行 0 代表删除失败
	 */
	public int delete(String userId){
		SQLiteDatabase  db = helper.getWritableDatabase();
		int result = db.delete("account", "userId=?", new String[]{userId});
		db.close();//释放资源
		return result;
	}
	
	/**
	 * 修改
	 * @param userName 姓名
	 * @param password 密码
	 * @return 更新了几行 0更新失败
	 */
	public int update(String userId,String userName,String password,String nickName,String phone,String email,String userType,String quitState,long time){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("username", userName);
		values.put("password", password);
		values.put("nickName", nickName);
		values.put("phone", phone);
		values.put("email", email);
		values.put("userType", userType);
		values.put("quitState", quitState);
		values.put("time", time);
		int result = db.update("account", values, "userId=?", new String[]{userId});
		db.close();//释放资源
		return result;
	}
	/**
	 * 通过手机修改
	 * @param userName 姓名
	 * @param password 密码
	 * @return 更新了几行 0更新失败
	 */
	public int updateByPhone(String userId,String phone){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userId", userId);
		int result = db.update("account", values, "phone=?", new String[]{phone});
		db.close();//释放资源
		return result;
	}
	/**
	 * 通过邮箱修改
	 * @param userName 姓名
	 * @param password 密码
	 * @return 更新了几行 0更新失败
	 */
	public int updateByEmail(String userId,String email){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userId", userId);
		int result = db.update("account", values, "email=?", new String[]{email});
		db.close();//释放资源
		return result;
	}
	/**
	 * 更新账号退出状态。主要用于退出登录按钮
	 * @param userId
	 * @param quitState
	 * @return
	 */
	public int updateQuitState(String userId,String quitState){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("quitState", quitState);
		int result = db.update("account", values, "userId=?", new String[]{userId});
		db.close();//释放资源
		return result;
	}
	/**
	 * 查询
	 * @param userId id
	 * @return null代表不存在
	 */
	public AccountDBBean find(String userId){
		AccountDBBean accountDBBean = new AccountDBBean();
		SQLiteDatabase  db = helper.getReadableDatabase();
		//结果集 游标
		Cursor cursor = db.query("account", new String[]{"username,password,nickName,phone,email,userType,quitState,userId"}, "userId=?", new String[]{userId}, null, null, null);
		boolean result = cursor.moveToNext();
		if(result){
			
			accountDBBean.setUserName(cursor.getString(0));
			accountDBBean.setPassword(cursor.getString(1));
			accountDBBean.setNickName(cursor.getString(2));
			accountDBBean.setPhone(cursor.getString(3));
			accountDBBean.setEmail(cursor.getString(4));
			accountDBBean.setUserType(cursor.getString(5));
			accountDBBean.setQuitState(cursor.getString(6));
			accountDBBean.setUserId(cursor.getString(7));
		}
		cursor.close();//释放资源
		db.close();
		return accountDBBean;
	}
	/**
	 * 查询手机号
	 * @param phone 手机号
	 * @return null代表不存在
	 */
	public AccountDBBean findByPhone(String phone){
		AccountDBBean accountDBBean = new AccountDBBean();
		SQLiteDatabase  db = helper.getReadableDatabase();
		//结果集 游标
		Cursor cursor = db.query("account", new String[]{"username,password,nickName,phone,email,userType,userId"}, "phone=?", new String[]{phone}, null, null, null);
		boolean result = cursor.moveToNext();
		if(result){
			
			accountDBBean.setUserName(cursor.getString(0));
			accountDBBean.setPassword(cursor.getString(1));
			accountDBBean.setNickName(cursor.getString(2));
			accountDBBean.setPhone(cursor.getString(3));
			accountDBBean.setEmail(cursor.getString(4));
			accountDBBean.setUserType(cursor.getString(5));
			accountDBBean.setUserId(cursor.getString(6));
		}
		cursor.close();//释放资源
		db.close();
		return accountDBBean;
	}
	/**
	 * 查询邮箱
	 * @param phone 手机号
	 * @return null代表不存在
	 */
	public AccountDBBean findByEmail(String email){
		AccountDBBean accountDBBean = new AccountDBBean();
		SQLiteDatabase  db = helper.getReadableDatabase();
		//结果集 游标
		Cursor cursor = db.query("account", new String[]{"username,password,nickName,phone,email,userType,userId"}, "email=?", new String[]{email}, null, null, null);
		boolean result = cursor.moveToNext();
		if(result){
			
			accountDBBean.setUserName(cursor.getString(0));
			accountDBBean.setPassword(cursor.getString(1));
			accountDBBean.setNickName(cursor.getString(2));
			accountDBBean.setPhone(cursor.getString(3));
			accountDBBean.setEmail(cursor.getString(4));
			accountDBBean.setUserType(cursor.getString(5));
			accountDBBean.setUserId(cursor.getString(6));
		}
		cursor.close();//释放资源
		db.close();
		return accountDBBean;
	}
	/**
	 * 查询
	 * @param username 用户名
	 * @return null代表不存在
	 */
	public AccountDBBean findByUsername(String username){
		AccountDBBean accountDBBean = new AccountDBBean();
		SQLiteDatabase  db = helper.getReadableDatabase();
		//结果集 游标
		Cursor cursor = db.query("account", new String[]{"username,password,nickName,phone,email,userType,userId"}, "username=?", new String[]{username}, null, null, null);
		boolean result = cursor.moveToNext();
		if(result){
			
			accountDBBean.setUserName(cursor.getString(0));
			accountDBBean.setPassword(cursor.getString(1));
			accountDBBean.setNickName(cursor.getString(2));
			accountDBBean.setPhone(cursor.getString(3));
			accountDBBean.setEmail(cursor.getString(4));
			accountDBBean.setUserType(cursor.getString(5));
			accountDBBean.setUserId(cursor.getString(6));
		}
		cursor.close();//释放资源
		db.close();
		return accountDBBean;
	}
	/**
	 * 获取全部的正常登录用户信息
	 * @return
	 */
	public List<AccountDBBean> findAll(){
		List<AccountDBBean> accountDBList =new ArrayList<AccountDBBean>();
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor =  db.query("account", new String[]{"userId,username,password,nickName,phone,email,userType,quitState"}, "userType=?", new String[]{"1"}, null, null, "time DESC");
		//Cursor cursor =  db.query("account", new String[]{"userId,username","password,userType"}, null, null, null, null, "time DESC");
		while(cursor.moveToNext()){
			String userId = cursor.getString(0);
			String userName = cursor.getString(1);
			String password = cursor.getString(2);
			String nickName = cursor.getString(3);
			String phone = cursor.getString(4);
			String email = cursor.getString(5);
			String userType = cursor.getString(6);
			String quitState = cursor.getString(7);
			AccountDBBean accountDBBean = new AccountDBBean();
			accountDBBean.setUserId(userId);
			accountDBBean.setUserName(userName);
			accountDBBean.setPassword(password);
			accountDBBean.setNickName(nickName);
			accountDBBean.setPhone(phone);
			accountDBBean.setEmail(email);
			accountDBBean.setUserType(userType);
			accountDBBean.setQuitState(quitState);
			accountDBList.add(accountDBBean);
		}
		cursor.close();
		db.close();
		return accountDBList;
	}
	
	/**
	 * 获取全部的登录用户信息包含游客账号
	 * @return
	 */
	public List<AccountDBBean> findAllIncludeGuest(){
		List<AccountDBBean> accountDBList =new ArrayList<AccountDBBean>();
		SQLiteDatabase  db = helper.getReadableDatabase();
		//Cursor cursor =  db.query("account", new String[]{"userId,username","password,userType"}, null, null, null, null, "time DESC");
		Cursor cursor =  db.query("account", new String[]{"userId,username,password,nickName,phone,email,userType,quitState"}, null, null, null, null, "time DESC");
		while(cursor.moveToNext()){
			String userId = cursor.getString(0);
			String userName = cursor.getString(1);
			String password = cursor.getString(2);
			String nickName = cursor.getString(3);
			String phone = cursor.getString(4);
			String email = cursor.getString(5);
			String userType = cursor.getString(6);
			String quitState = cursor.getString(7);
			AccountDBBean accountDBBean = new AccountDBBean();
			accountDBBean.setUserId(userId);
			accountDBBean.setUserName(userName);
			accountDBBean.setPassword(password);
			accountDBBean.setNickName(nickName);
			accountDBBean.setPhone(phone);
			accountDBBean.setEmail(email);
			accountDBBean.setUserType(userType);
			accountDBBean.setQuitState(quitState);
			accountDBList.add(accountDBBean);
		}
		cursor.close();
		db.close();
		return accountDBList;
	}
	
}
