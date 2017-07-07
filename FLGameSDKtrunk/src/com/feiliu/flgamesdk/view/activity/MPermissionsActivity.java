package com.feiliu.flgamesdk.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

/**
 * @作者: 刘传政
 * @时间:2017-6-15 下午5:26:22
 * @QQ: 105237416
 * @电话: 18501231486
 * @作用:6.0权限处理
 */
public class MPermissionsActivity extends Activity {
	private final String TAG = "MPermissions";
	private int REQUEST_CODE_PERMISSION = 0x00099;

	/**
	 * 请求权限
	 * 
	 * @param permissions
	 *            请求的权限
	 * @param requestCode
	 *            请求权限的请求码
	 */
	public void requestPermission(String[] permissions, int requestCode) {
		this.REQUEST_CODE_PERMISSION = requestCode;
		if (checkPermissions(permissions)) {
			permissionSuccess(REQUEST_CODE_PERMISSION);
		} else {
			List<String> needPermissions = getDeniedPermissions(permissions);
			ActivityCompat
					.requestPermissions(this, needPermissions
							.toArray(new String[needPermissions.size()]),
							REQUEST_CODE_PERMISSION);
		}
	}

	/**
	 * 检测所有的权限是否都已授权
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean checkPermissions(String[] permissions) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}

		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取权限集中需要申请权限的列表
	 * 
	 * @param permissions
	 * @return
	 */
	private List<String> getDeniedPermissions(String[] permissions) {
		List<String> needRequestPermissionList = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
					|| ActivityCompat.shouldShowRequestPermissionRationale(
							this, permission)) {
				needRequestPermissionList.add(permission);
			}
		}
		return needRequestPermissionList;
	}

	/**
	 * 系统请求权限回调
	 * 
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if (verifyPermissions(grantResults)) {
				permissionSuccess(REQUEST_CODE_PERMISSION);
			} else {
				permissionFail(REQUEST_CODE_PERMISSION);
				showTipsDialog();
			}
		}
	}

	/**
	 * 确认所有的权限是否都已授权
	 * 
	 * @param grantResults
	 * @return
	 */
	private boolean verifyPermissions(int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 显示提示对话框
	 */
	private void showTipsDialog() {
		new AlertDialog.Builder(this).setTitle("提示信息")
				.setCancelable(false)
				.setMessage("当前应用缺少必要权限，暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startAppSettings();
						finish();
					}
				}).show();
	}

	/**
	 * 启动当前应用设置页面
	 */
	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

	/**
	 * 获取权限成功
	 * 
	 * @param requestCode
	 */
	public void permissionSuccess(int requestCode) {
		Log.d(TAG, "获取权限成功=" + requestCode);

	}

	/**
	 * 权限获取失败
	 * 
	 * @param requestCode
	 */
	public void permissionFail(int requestCode) {
		Log.d(TAG, "获取权限失败=" + requestCode);
	}
}