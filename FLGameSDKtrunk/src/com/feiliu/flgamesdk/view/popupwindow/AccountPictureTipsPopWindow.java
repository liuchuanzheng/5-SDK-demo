package com.feiliu.flgamesdk.view.popupwindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 账号安全提示window
 * 
 * @author 刘传政
 * 
 */
public class AccountPictureTipsPopWindow extends BasePopWindow implements
		OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private OnDismissListener lastPopOnDismissListener;
	private boolean haveGestLogin;
	private String giftStatus;
	private String userName;

	public AccountPictureTipsPopWindow(Context context, Boolean haveGestLogin,
			String giftStatus, String userName, FLOnLoginListener loginListener,OnDismissListener onDismissListener) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		this.haveGestLogin = haveGestLogin;
		this.giftStatus = giftStatus;
		this.userName = userName;
		this.loginListener = loginListener;
		lastPopOnDismissListener = onDismissListener;
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面ui并设置其中的点击事件等
	 * 
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int) (designWidth * scale), (int) (designHeight * scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		Drawable bg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.WINDOWBG);// 得到背景图片
		totalLayout.setBackgroundDrawable(bg);
		// ..........................................title............................................................
		BaseBarView baseBarView = new BaseBarView(context, "账号安全提示", false, false,this);
		RelativeLayout.LayoutParams baseBarViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (100 * scale));
		baseBarViewParams.topMargin = (int) (0 * scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		
		//........................................固定提示部分...........................................................
		TextView changelessTipsView = new TextView(context);
		RelativeLayout.LayoutParams changelessTipsViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (500 * scale));
		changelessTipsViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE); // 水平居中
		changelessTipsViewParams.topMargin = (int) (185 * scale);
		changelessTipsView.setLayoutParams(changelessTipsViewParams);
		changelessTipsView.setText("为便于找回账号，我们将把游客账号信息以图片的形式存储到您的存储卡中，请勿删除");
		changelessTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		
		// ....................................确定按钮......................................................
		TextView OKView = new TextView(context);
		RelativeLayout.LayoutParams bindViewParams = new RelativeLayout.LayoutParams((int) (700 * scale), (int) (110 * scale));
		bindViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); // 水平居中
		bindViewParams.topMargin = (int) (470 * scale);
		OKView.setLayoutParams(bindViewParams);
		Drawable bindViewBg = GetSDKPictureUtils.getDrawable(context,
				PictureNameConstant.LOGINORREGISTER);
		OKView.setBackgroundDrawable(bindViewBg);
		OKView.setText("确定");
		OKView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		OKView.setTextColor(Color.WHITE);
		OKView.setGravity(Gravity.CENTER);// 文字居中
		OKView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				saveGuestLoginPicture(userName);
				//绑定提示
				new BindTipsPopWindow(context, giftStatus, loginListener, null,"");
			}
		});
		
		totalLayout.addView(baseBarView);
		totalLayout.addView(changelessTipsView);
		totalLayout.addView(OKView);
		
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		totalLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		totalLayout.setLayoutParams(totalLayoutParams);
		rootView.addView(totalLayout);
		return rootView;
	}


	@Override
	public void onDismiss() {

	}

	@Override
	public void showWindow() {
		MyLogger.lczLog().i("展示：AccountPictureTipsPopWindow");
		new Runnable() {
			@Override
			public void run() {
				TextView tv;
				tv = new TextView(context);
				createPopWindow(tv);
			}
		}.run();
	}

	@Override
	public void createPopWindow(View parent) {
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(currentWindowView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // 创建一个PopuWidow对象
		}
		mPopupWindow.setFocusable(true);// 使其聚集
		mPopupWindow.setOutsideTouchable(false);// 设置是否允许在外点击消失
		// mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//设置弹出窗体需要软键盘
		mPopupWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 再设置模式，和Activity的一样，覆盖，调整大小。
		// mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//
		// 响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);// pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
	}
	
	/**
	 * 保存游客登录截图到系统相册，并发送广播让系统相册刷新列表，便于立即显示
	 * @param userName 
	 * @throws IOException 
	 */
	protected void saveGuestLoginPicture(String userName) {
		Bitmap bitmap = createGuestLoginPic(userName);  
		saveImageToGallery(context,bitmap);
	}
	/**
	 * 创建游客登录信息图片
	 * @param userName 
	 * @return
	 */
	private Bitmap createGuestLoginPic(String userName) {
		float scale = UiSizeUtil.getScale(this.context);
		RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				(int)(640*scale), (int)(720*scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		Drawable bg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GUESTSAVE);//得到背景图片
		totalLayout.setBackgroundDrawable(bg);
		//.................................................游戏名称........................................................
		TextView tipsTv = new TextView(context);
		RelativeLayout.LayoutParams tipsTvParams=new RelativeLayout.LayoutParams((int)(600*scale),(int)(100*scale));
		tipsTvParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //水平居中 
		tipsTvParams.topMargin=(int) (200*scale);
		tipsTv.setLayoutParams(tipsTvParams);
		Drawable tipsTvBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//登录注册按钮图片
		tipsTv.setBackgroundDrawable(tipsTvBg);
		tipsTv.setText(getAppName(context)+"");
		tipsTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		tipsTv.setTextColor(Color.BLACK);
		tipsTv.setGravity(Gravity.CENTER);//文字居中
		//..................................................账号.............................................................
		TextView guestAccountTv = new TextView(context);
		RelativeLayout.LayoutParams guestAccountTvParams=new RelativeLayout.LayoutParams((int)(600*scale),(int)(120*scale));
		guestAccountTvParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //水平居中 
		guestAccountTvParams.topMargin=(int) (408*scale);
		guestAccountTv.setLayoutParams(guestAccountTvParams);
		guestAccountTv.setBackgroundColor(Color.TRANSPARENT);
		guestAccountTv.setText(userName);
		guestAccountTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
		guestAccountTv.setTextColor(Color.BLACK);
		guestAccountTv.setGravity(Gravity.CENTER);//文字居中
		//...................................................................................................................
		totalLayout.addView(tipsTv);
		totalLayout.addView(guestAccountTv);
		totalLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
		totalLayout.layout(0, 0, totalLayout.getMeasuredWidth(), totalLayout.getMeasuredHeight());  
		totalLayout.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(totalLayout.getDrawingCache());  
		//千万别忘最后一步  
		totalLayout.destroyDrawingCache();
		return bitmap;
	}
	/**
	 * 将图片保存到系统相册
	 * @param context
	 * @param bmp
	 */
	public  void saveImageToGallery(Context context, Bitmap bmp) {
	    if (bmp == null){
	        ToastUtil.showShort(context, "保存出错了");
	        return;
	    }
	    // 首先保存图片
	    File appDir = new File(Environment.getExternalStorageDirectory(), "Feiliu");
	    //File appDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    String timeAndRandom = System.currentTimeMillis()+"";
	    String fileName = "游客账号信息"+timeAndRandom+".png";//此名字没有具体效果（有些机型上会以类似备注的形式显示）。以为此文件创建后会代码删除。保存到系统相册里的才是真正的截图
	    File file = new File(appDir, fileName);
	    try {
	        FileOutputStream fos = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
	        fos.flush();
	        fos.close();
	    } catch (FileNotFoundException e) {
	        ToastUtil.showShort(context, "文件未发现");
	        e.printStackTrace();
	    } catch (IOException e) {
	        ToastUtil.showShort(context, "保存出错了");
	        e.printStackTrace();
	    }catch (Exception e){
	        ToastUtil.showShort(context, "保存出错了");
	        e.printStackTrace();
	    }
	  /*  Boolean isNeedDelete = true;
	    //将图片移到系统相册中。但是文件名会系统自动分配。
	    String resultUrl = "";
	    try {
	    	resultUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
	    } catch (Exception e) {
	    	isNeedDelete = false;
	        e.printStackTrace();
	    }
	    if (isNeedDelete) {
	    	MyLogger.lczLog().i("删除了");
	    	file.delete();
		}*/
	    //appDir.delete();
	    // 最后通知图库更新
	    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri uri = Uri.fromFile(file);
	    intent.setData(uri);
	    context.sendBroadcast(intent);
	   /* Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri uri1 = Uri.fromFile(new File(resultUrl));
	    intent1.setData(uri1);
	    context.sendBroadcast(intent1);*/
	    ToastUtil.showShort(context, "图片已保存至/Feiliu/文件夹");
	    MyLogger.lczLog().i("保存游客截图成功了...");
	}
	
	 /** 
     * 获取应用程序名称 
     */  
    public String getAppName(Context context)  {  
        try  {  
            PackageManager packageManager = context.getPackageManager();  
            PackageInfo packageInfo = packageManager.getPackageInfo(  
                    context.getPackageName(), 0);  
            int labelRes = packageInfo.applicationInfo.labelRes;  
            return context.getResources().getString(labelRes);  
        } catch (NameNotFoundException e)  {  
            e.printStackTrace();  
        }  
        return null;  
    }  

	@Override
	public void backbuttonclick() {
		mPopupWindow.dismiss();
		lastPopOnDismissListener.onDismiss();//通知上一个pop
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
		//绑定提示
		new BindTipsPopWindow(context, giftStatus, loginListener, null,"");
	}

}
