package com.feiliu.flgamesdk.forceupdate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.feiliu.flgamesdk.bean.GameInfo;
import com.feiliu.flgamesdk.forceupdate.ProgressResponseBody.ProgressListener;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.log.MyLogger;

/**
 * 强更功能  包括弹框等操作
 * @author 刘传政
 *
 */
public class FLForceUpdateManager {
	private Context context;
	private boolean haveLocalApk = false;// 是否有下载好的apk
	private boolean haveUpdate = false;// 服务器是否有更新
	public  String PACKAGE_URL = "";
	private long breakPoints;
	private ProgressDownloader downloader;
	private long totalBytes;
	private long contentLength;
	private ProgressDialog progressDialog;
	private boolean hasShowTips = false;

	public FLForceUpdateManager(Context context) {
		super();
		this.context = context;
		checkApkFromLocal();
		checkUpdateFromServer();
	}

	/**
	 * 检查本地是否有上次下载好的,但是没有安装的apk
	 */
	private void checkApkFromLocal() {
		GameInfo gameInfo = new GameInfo(context);
		String apkName = gameInfo.getAppId() + gameInfo.getCoopId() + gameInfo.getVersionCode()+ ".apk";//更新包的名字
		MyLogger.lczLog().i("更新包名字" + apkName);
		String apkFilePath = "Feiliu/versionUpdate";//存放更新包的目录
		File fileDir = new File(Environment.getExternalStorageDirectory(),apkFilePath);
		if (!fileDir.exists()) {
			 fileDir.mkdir();
		}
		List<String> allAPK = getAllAPK(apkFilePath);
		if (allAPK.size() >0) {
			for (String apk : allAPK) {
				File nowFile = new File(fileDir,apk);
				if (apkName.equals(apk)) {
					haveLocalApk = true;
					showInstallDialog(nowFile);
					return;
				}else {
					//如果文件名不一样 就删除此文件
					if (nowFile.isFile()) {
						nowFile.delete();
					}
				}
			}
		}
	}
	
	private static List<String> getAllAPK(String apkFilePath) {
		List<String> fileList = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory(),apkFilePath);
		File[] subFile = file.listFiles();
		if (subFile == null || subFile.length <= 0) {
			return fileList;
		}
		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				String filename = subFile[iFileLength].getName();
				// 判断是否为APK结尾
				if (filename.trim().toLowerCase().endsWith(".apk")) {
					fileList.add(filename);
				}
			}
		}
		return fileList;
	}

	/**
	 * 安装apk
	 * 
	 * @param file
	 */
	private void installApk(File file) {
		if (!file.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果没有i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);这一步的话，最后安装好了，点打开，是不会打开新版本应用的。
		i.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());//如果没有android.os.Process.killProcess(android.os.Process.myPid());最后不会提示完成、打开。
		
	};

	/**
	 * 请求服务器是否有更新
	 */
	private void checkUpdateFromServer() {
		if (haveLocalApk) {
			// 如果有本地apk 不进行联网更新
			return;
		}

		// TODO 联网检查更新
		GameInfo gameInfo = new GameInfo(context);
		String apkFilePath = "Feiliu/versionUpdate";//存放更新包的目录
		versionUpdateRequest(context, gameInfo, apkFilePath);
		
	}
	/**
	 * 联网查询是否有更新包
	 * @param mContext
	 * @param gameInfo
	 * @param apkFilePath
	 */
	private void versionUpdateRequest(final Context mContext, GameInfo gameInfo,
			String apkFilePath) {
		final JSONObject holder = new JSONObject();
		JSONObject gameUpdateCheckJsonObject = new JSONObject();
		try {
			gameUpdateCheckJsonObject.put("appId", gameInfo.getAppId());
			gameUpdateCheckJsonObject.put("coopId", gameInfo.getCoopId());
			gameUpdateCheckJsonObject.put("versionCode", gameInfo.getVersionCode());
			holder.put("actionId", "6002");
			holder.put("gameUpdateCheck",gameUpdateCheckJsonObject);
			MyLogger.lczLog().i("强更请求:" + holder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 创建一个OkHttpClient对象
		 OkHttpClient okHttpClient = new OkHttpClient();
		RequestBody requestBody = new RequestBody() {
	        @Override
	        public MediaType contentType() {
	            return MediaType.parse("application/json;charset=UTF-8");
	        }
	        
	        @Override
	        public void writeTo(BufferedSink sink) throws IOException {
	            sink.writeUtf8(holder.toString());
	        }

	    };

	     Request request = new Request.Builder()
	            .url(URLConstant.FORCEUPDATE_URL)
	            .post(requestBody)
	            .build();
	  
		
		
			okHttpClient.newCall(request).enqueue(new Callback() {
				
				
				@Override
				public void onFailure(Call arg0, IOException arg1) {
				}

				@Override
				public void onResponse(Call arg0, Response arg1)
						throws IOException {
					String s = arg1.body().string();
					MyLogger.lczLog().i("强更请求返回:" + s);
					final String jsonString = s.substring(7);
					((Activity) mContext).runOnUiThread(new Runnable() {

						public void run() {
							try {
								JSONObject jsonObject = new JSONObject(jsonString);
								String code = jsonObject.getJSONObject("result").getString("code");
								if ("0".equals(code)) {
									//有升级
									haveUpdate = true;
									PACKAGE_URL = jsonObject.getString("updateUrl");
									showDownLoadTipsDialog();
								}else {
									//无升级
									haveUpdate = false;
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						}
					});
				}
			});
		
	}

	/**
	 * 提示用户下载对话框
	 */
	private void showDownLoadTipsDialog() {
		if (hasShowTips) {
			//如果显示过安装提示.下次直接显示下载进度框
			showDownloadProgress();
			return;
		}
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				context);
		normalDialog.setCancelable(false);
		normalDialog.setTitle("温馨提示");
		normalDialog.setMessage("为了给您更好的游戏体验,请下载并安装最新版本");
		normalDialog.setPositiveButton("确认下载",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showDownloadProgress();
					}
				});
		normalDialog.setNegativeButton("退出游戏",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 退出游戏
						System.exit(0);
					}
				});
		// 显示
		normalDialog.show();
		hasShowTips = true;
	}

	/**
	 * 是否安装对话框
	 */
	private void showInstallDialog(final File file) {

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setCancelable(false)
				.setTitle("温馨提示")
				.setMessage("新版本安装包已下载完成,是否安装?")
				.setPositiveButton("确认安装", null)
				.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 退出游戏
						System.exit(0);
					}
				}).create();
		// 这里必须要先调show()方法，后面的getButton才有效
		dialog.show();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						installApk(file);
					}
				});
	}

	/**
	 * 继续下载对话框
	 */
	private void showContinueDownLoadTipsDialog() {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				context);
		normalDialog.setCancelable(false);
		normalDialog.setTitle("温馨提示");
		normalDialog.setMessage("是否取消当前游戏包下载?");
		normalDialog.setPositiveButton("继续下载",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog.show();
						downloader.download(breakPoints);
					}
				});
		normalDialog.setNegativeButton("取消并退出",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 退出游戏
						System.exit(0);
					}
				});
		// 显示
		normalDialog.show();
	}

	/**
	 * 下载出错对话框
	 */
	private void showFailDownLoadTipsDialog(String tips) {
		AlertDialog dialog  = new AlertDialog.Builder(context)
		.setCancelable(false)
		.setTitle("温馨提示")
		.setMessage(tips)
		.setPositiveButton("重新下载",null)
		.setNegativeButton("退出游戏",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 退出游戏
						System.exit(0);
					}
				})
		.create();
		// 显示
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						FLForceUpdateManager.this.contentLength = 0l;
						checkApkFromLocal();
						checkUpdateFromServer();
					}
				});
		
	
	}

	protected void showDownloadProgress() {
		/* @setProgress 设置初始进度
		* @setProgressStyle 设置样式（水平进度条）
		* @setMax 设置进度最大值
		*/
		final int MAX_PROGRESS = 100;
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.setTitle("游戏包下载中");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(MAX_PROGRESS);
		// 设置可点击的按钮，最多有三个(默认情况下)
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消下载",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloader.pause();
						// 存储此时的totalBytes，即断点位置。
						breakPoints = totalBytes;
						progressDialog.dismiss();
						showContinueDownLoadTipsDialog();
					}
				});
		progressDialog.show();

		// 新下载前清空断点信息
		breakPoints = 0L;
		final String SDPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Feiliu/versionUpdate";
		File filePre = new File(SDPath, "feiliutemp.apk");
		if (filePre.exists()) {
			filePre.delete();
		}
		final File file = new File(SDPath, "feiliutemp.apk");
		downloader = new ProgressDownloader(PACKAGE_URL, file,
				new ProgressListener() {

					@Override
					public void update(long totalBytes, boolean done) {
						// 注意加上断点的长度
						FLForceUpdateManager.this.totalBytes = totalBytes
								+ breakPoints;
						progressDialog
								.setProgress((int) (totalBytes + breakPoints) / 1024);
						if (done) {
							((Activity) context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									progressDialog.dismiss();
									if (file.exists()) {
										GameInfo gameInfo = new GameInfo(context);
										String apkName = gameInfo.getAppId() + gameInfo.getCoopId() + gameInfo.getVersionCode()+ ".apk";
										File finalFile = new File(SDPath,apkName);
										file.renameTo(finalFile);
										showInstallDialog(finalFile);
									}
								}
							});
						}
					}

					@Override
					public void onPreExecute(long contentLength) {
						MyLogger.lczLog().i("下载开始,总大小为:" + contentLength);
						long sdAvailableSize = getSDAvailableSize();
						MyLogger.lczLog().i("内存卡剩余空间" + sdAvailableSize);
						if (sdAvailableSize < contentLength ) {
							//内存过小
							((Activity)context).runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									downloader.pause();
									progressDialog.dismiss();
									String tips = "当前设备存储空间不足，请在清理后重试";
									showFailDownLoadTipsDialog(tips);
								}
							});
							return;
						}
						
						// 文件总长只需记录一次，要注意断点续传后的contentLength只是剩余部分的长度
						if (FLForceUpdateManager.this.contentLength == 0L) {
							FLForceUpdateManager.this.contentLength = contentLength;
							progressDialog.setMax((int) (contentLength / 1024));
						}
					}

					@Override
					public void fail(Exception e) {
						MyLogger.lczLog().i("下载失败" + e);
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
								String tips = "下载出错,是否重新下载?";
								showFailDownLoadTipsDialog(tips);
							}
						});
					}
				});
		downloader.download(0L);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	private long getSDAvailableSize() {
		  //判断sdcard存储空间是否满足文件的存储
        File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
        long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
        return usableSpace;
	}
}
