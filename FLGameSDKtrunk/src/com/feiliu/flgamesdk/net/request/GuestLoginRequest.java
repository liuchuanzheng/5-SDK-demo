package com.feiliu.flgamesdk.net.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feiliu.flgamesdk.bean.netresultbean.GuestLoginResultJsonBean;
import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.HandlerConstant;
import com.feiliu.flgamesdk.global.LoginStatus;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.global.URLConstant;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.utils.PostParameter;
import com.feiliu.flgamesdk.utils.ExToast;
import com.feiliu.flgamesdk.utils.FlViewToastUtils;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.AccountPictureTipsPopWindow;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;
import com.google.gson.Gson;

/**
 * 所有网络请求
 * @author 刘传政
 *
 */
public class GuestLoginRequest extends BaseReuest {
	private ExToast exToast;//登录进度
	private boolean isHideProgress = false;//登录进度是否消失
	private long startTime = 0;
	private long endTime;
	private float scale;
	/**
	 * 联网成功或失败消息接收
	 */
	private  Handler mHandler = new Handler(Looper.getMainLooper()){
		 
		private GuestLoginResultJsonBean guestLoginResultJsonBean;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.GUESTLOGIN_SUCCESS:
				guestLoginResultJsonBean = (GuestLoginResultJsonBean) msg.obj;
				
				if (guestLoginResultJsonBean.getResult().getCode().equals("0")) {
					SPUtils spUtils = new SPUtils(context, SPConstant.GIFTSTATUS);
					spUtils.putString("giftStatus", guestLoginResultJsonBean.getUserInfo().getGiftStatus()+"");
					//标记未实名注册
					SPUtils spUtils1 = new SPUtils(context, SPConstant.USERINFO);
					spUtils1.putBoolean("realName", false);
					if (basePopWindow != null) {
						basePopWindow.mPopupWindow.dismiss();
					}
					//  本地保存游客信息到本地，以便下次提交时直接获取
					AccountDao accountDao = new AccountDao(context);
					AccountDBBean findBean = accountDao.find(guestLoginResultJsonBean.getUserInfo().getUserId()+"");
					if (null != findBean.getUserId()) {
						accountDao.update(guestLoginResultJsonBean.getUserInfo().getUserId()+"", guestLoginResultJsonBean.getUserInfo().getUserName()
								, "",guestLoginResultJsonBean.getUserInfo().getNickName(),"","", "0","login", System.currentTimeMillis());
					}else{
						accountDao.add(guestLoginResultJsonBean.getUserInfo().getUserId()+"", guestLoginResultJsonBean.getUserInfo().getUserName()
								,"",guestLoginResultJsonBean.getUserInfo().getNickName(),"","","0","login",System.currentTimeMillis());
						//saveGuestLoginPicture(guestLoginResultJsonBean.getUserInfo().getUserName());
						new AccountPictureTipsPopWindow(context, false, guestLoginResultJsonBean.getUserInfo().getGiftStatus()+"", guestLoginResultJsonBean.getUserInfo().getUserName(), loginListener, new OnDismissListener() {
							
							@Override
							public void onDismiss() {
								
							}
						});
						
						
					}
					LoginStatus.status = true;//内存标记当前账户登录状态
					//记录是否成年   游客模式默认未知0
					SPUtils spUtils2 = new SPUtils(context, SPConstant.USERINFO);
					spUtils2.putString("ageStatus", "0");
					//记录未实名制
					SPUtils spUtils3 = new SPUtils(context, SPConstant.USERINFO);
					spUtils3.putBoolean("realName", false);
					sendBroadcast(context, "com.feiliu.flgamesdk.ToolBarReceiver", "toolBarAction", "refrush");
					loginListener.onLoginComplete(guestLoginResultJsonBean.getUserInfo().getUserId()+"", guestLoginResultJsonBean.getUserInfo().getSign().getCode(), guestLoginResultJsonBean.getUserInfo().getSign().getCurrentTimeSeconds()+"");
					//顶部提示
					/*Toast mToast = Toast.makeText(context,"欢迎回来 "+guestLoginResultJsonBean.getUserInfo().getNickName(), Toast.LENGTH_LONG);
					mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
					mToast.show();*/
					if(isHideProgress){
						FlViewToastUtils.show(context, "欢迎回来 "+guestLoginResultJsonBean.getUserInfo().getUserName(),3);
					}
				
				}else {
					//弹出错误提示信息
					ToastUtil.showShort(context, guestLoginResultJsonBean.getResult().getTips());
				}
				break;
			case HandlerConstant.GUESTLOGIN_FAIL:
				ToastUtil.showLong(context,"当前设备无网络，请检查网络设置");
				loginListener.onLoginFailed();
				break;
			case HandlerConstant.GUESTLOGINDELAY:
				exToast.hide();
				isHideProgress = true;
				if(null != guestLoginResultJsonBean && guestLoginResultJsonBean.getResult().getCode().equals("0")){
					FlViewToastUtils.show(context, "欢迎回来 "+guestLoginResultJsonBean.getUserInfo().getUserName(),3);
				}
				
				break;
			}
			
			
		}
		
	};
	public GuestLoginRequest(Context context,FLOnLoginListener loginListener,BasePopWindow basePopWindow) {
		super();
		this.context = context;
		this.loginListener = loginListener;
		this.basePopWindow = basePopWindow;
		scale = UiSizeUtil.getScale(this.context);
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
	public void sendHandlerMessage(int mWhat,Object obj) {
		Message msg = mHandler.obtainMessage();
		msg.what = mWhat;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 展示登录进度提示
	 */
	private void showProgress() {
		View view = createMyUi();
		exToast = new ExToast(context);
		exToast.setDuration(5);//5秒
		exToast.setView(view);
		exToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		exToast.show();
		startTime = System.currentTimeMillis();
	}
	/**
	 * 隐藏进度提示
	 */
	private void hideProgress() {
		endTime = System.currentTimeMillis();
		long betweenTime = endTime - startTime; 
		if (betweenTime >= 1000) {
			if (exToast != null) {
				exToast.hide();
				isHideProgress = true;
			}
		}else {
			 Message msg = mHandler.obtainMessage();
			 msg.what = HandlerConstant.GUESTLOGINDELAY;
	         mHandler.sendMessageDelayed(msg, 1000 - betweenTime);
		}
		
	}
	/**
	 * 创建界面ui并设置其中的点击事件等
	 * @return
	 */
	public View createMyUi() {
		final RelativeLayout totalLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams totalLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,(int)(100*scale));
		totalLayout.setLayoutParams(totalLayoutParams);
		//......................................提示语.............................................................
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(50*scale);
		shape.setColor(Color.WHITE);
		TextView tips = new TextView(context);
		RelativeLayout.LayoutParams tipsParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,(int)(100*scale));
		tipsParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);  
		tips.setLayoutParams(tipsParams);
		Drawable logoIvBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ROUNDLOGO);
		tips.setBackgroundDrawable(shape);
		logoIvBg.setBounds(0, 0, (int)(100*scale), (int)(100*scale));
		tips.setCompoundDrawables(logoIvBg, null, null, null);
		tips.setText("账号登录中…");
		tips.setPadding((int)(0*scale), (int)(0*scale), (int)(50*scale), (int)(0*scale));
		tips.setTextColor(Color.BLACK);
		tips.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		tips.setSingleLine(true);
		tips.setGravity(Gravity.CENTER);//文字居中
		totalLayout.addView(tips);
		return totalLayout;
	}

	/**
	 * 发送广播
	 * @param context
	 * @param action
	 * @param name
	 * @param value
	 */
	private void sendBroadcast(Context context,String action,String name,String value) {
		Intent intent = new Intent();
        intent.setAction(action); 
        intent.putExtra(name, value);
        //发送广播
        context.sendBroadcast(intent);
	}
	
	//.............................................................................................
	/**
	 * 联网游客登录
	 */
	public void post() {
		showProgress();
		PostParameter parameter = new PostParameter(context);
		final JSONObject holder;
		try {
			holder = new JSONObject();
			holder.put("gameInfo", parameter.getGameInfoJson());
			holder.put("sdkInfo", parameter.getSDKInfoJson());
			holder.put("deviceInfo", parameter.getDeviceInfoJson());
			holder.put("actionId", "4002");
			MyLogger.lczLog().i("游客登录请求参数："+holder.toString());
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// 创建一个OkHttpClient对象
					OkHttpClient okHttpClient = new OkHttpClient();
					RequestBody requestBody = new RequestBody() {
				        @Override
				        public MediaType contentType() {
				            return MediaType.parse("text/x-markdown; charset=utf-8");
				        }

				        @Override
				        public void writeTo(BufferedSink sink) throws IOException {
				            sink.writeUtf8(holder.toString());
				        }

				    };

				    Request request = new Request.Builder()
				            .url(URLConstant.ACCOUNT_URL)
				            .post(requestBody)
				            .build();

				  okHttpClient.newCall(request).enqueue(new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						hideProgress();
						String s = arg1.body().string();
						MyLogger.lczLog().i("游客登录请求成功："+s);
						String jsonString = s.substring(7);
						Gson gson = new Gson();
						GuestLoginResultJsonBean bean = gson.fromJson(jsonString, GuestLoginResultJsonBean.class);
						sendHandlerMessage(HandlerConstant.GUESTLOGIN_SUCCESS, bean);//发送handler方式为了cp能在主线程接收回调
						
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						hideProgress();
						MyLogger.lczLog().i("请求失败"+arg1);
						sendHandlerMessage(HandlerConstant.GUESTLOGIN_FAIL, "");
					}
				});
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
