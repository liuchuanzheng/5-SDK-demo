package com.feiliu.flgamesdk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 
 * @author liuchuanzheng
 * 
 */
public class BitmapUtil {

	/**
	 * 根据屏幕的宽高对bitmap进行放大.不留空隙
	 * 
	 * @param bitmap
	 * @param activity
	 * @return
	 */
	public static Bitmap changeImage(Bitmap bitmap, Activity activity) {
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int screenHeight = wm.getDefaultDisplay().getHeight();

		int imageWidth = bitmap.getWidth();
		int imageHeight = bitmap.getHeight();

		Matrix matrix = new Matrix();

		// 宽度比
		float fw = new BigDecimal((float) screenWidth / imageWidth).setScale(2,
				BigDecimal.ROUND_HALF_UP).floatValue();
		// 高度比
		float fh = new BigDecimal((float) screenHeight / imageHeight).setScale(
				2, BigDecimal.ROUND_HALF_UP).floatValue();

		Bitmap resizeBmp;
		if (fw > fh) {
			matrix.postScale(fw, fw); // 长和宽放大缩小的比例
			// 先放大到覆盖屏幕
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			// 裁剪成屏幕大小
			resizeBmp = Bitmap.createBitmap(resizeBmp, 0,
					(resizeBmp.getHeight() - screenHeight) / 2,
					resizeBmp.getWidth(), screenHeight - 1);
		} else {
			matrix.postScale(fh, fh); // 长和宽放大缩小的比例
			// 先放大到覆盖屏幕
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			int w = resizeBmp.getWidth();
			int h = resizeBmp.getHeight();
			// 裁剪成屏幕大小
			resizeBmp = Bitmap.createBitmap(resizeBmp,
					(resizeBmp.getWidth() - screenWidth) / 2, 0,
					screenWidth - 1, resizeBmp.getHeight());
		}

		return resizeBmp;

	}
}
