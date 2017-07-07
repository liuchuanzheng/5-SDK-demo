package com.feiliu.flgamesdk.utils;

import com.example.lcz_utils.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class GetSDKPictureUtils {
	public static Drawable getDrawable(Context context,String pictureUrl){
		Bitmap bgBitmap = AssetsUtil.getBitmapFromAssets(context, pictureUrl);
		Drawable bitmap2Drawable = ImageUtils.bitmap2Drawable(null, bgBitmap);
		return bitmap2Drawable;
	}
}
