package com.feiliu.flgamesdk.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 获取assets目录下的文件工具类
 * 
 * @author liuchuanzheng
 * 
 */
public class AssetsUtil {
	/**
	 * 获取assets目录下的图片资源为bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmapFromAssets(Context context, String fileName) {
		AssetManager am = context.getAssets();

		InputStream is = null;
		Bitmap bitmap = null;
		try {
			is = am.open(fileName);
			if (is != null) {
				bitmap = BitmapFactory.decodeStream(is);
			} else {
				return null;
			}

		} catch (IOException e) {
			//e.printStackTrace();
			Log.i("AssetsUtil", "转换bitmap的文件未找到"+fileName);
		} finally {
			try {
				if (is != null) {
					is.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 获取assets目录下的文件为输入流
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static InputStream getInputStreamFromAssets(Context context,
			String fileName) {
		AssetManager am = context.getAssets();
		InputStream is = null;
		try {
			is = am.open(fileName);
		} catch (IOException e) {
			//e.printStackTrace();
			if ("fl_channle.conf".equals(fileName)) {
				Log.i("AssetsUtil", "未读取到fl_channle.conf文件，SDK将以AndroidManifest.xml中FLGAMESDK_COOP_ID的值作为渠道号");
			}else {
				Log.i("AssetsUtil", "文件未找到"+fileName);
			}
			
		}
		return is;

	}

	/**
	 * 获取assets目录下的文件内容,返回字符串.
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getStringFromAssets(Context context, String fileName) {
		AssetManager am = context.getAssets();
		InputStream is = null;
		try {
			is = am.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//GLogUtils.i("fl_AssetsUtil", "获取assests下的文件字符串"+fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//GLogUtils.i("fl_AssetsUtil", fileName+"的文件内容为"+sb.toString());
		return sb.toString();
	}
}
