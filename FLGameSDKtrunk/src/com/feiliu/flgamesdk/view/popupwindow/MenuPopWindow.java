package com.feiliu.flgamesdk.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.activity.toolbar.FlToolBar3;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 飞流球菜单API调用展示的pop
 * @author 刘传政
 *
 */
public class MenuPopWindow extends BasePopWindow implements  OnDismissListener {

	public MenuPopWindow(Context context) {
		this.context = context;
		scale = UiSizeUtil.getScale(this.context);
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 创建界面ui并设置其中的点击事件等
	 * @return
	 */
	public View createMyUi() {
		
		//..........................................飞流球菜单.......................................................................
		
		RelativeLayout menuLayoutLeft = new RelativeLayout(context);
		RelativeLayout.LayoutParams menuLayoutLeftParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (150 * scale));
		menuLayoutLeftParams.topMargin = (int) (0 * scale);
		menuLayoutLeftParams.leftMargin = (int) (0 * scale);
		menuLayoutLeft.setLayoutParams(menuLayoutLeftParams);
		menuLayoutLeft.setBackgroundColor(Color.TRANSPARENT);
		//........................................logo...........................................................
		ImageView logoViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams logoViewLeftParams = new RelativeLayout.LayoutParams((int) (136 * scale), (int) (150* scale));
		logoViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		logoViewLeftParams.topMargin = (int) (0 * scale);
		logoViewLeftParams.leftMargin = (int) (0 * scale);
		logoViewLeft.setLayoutParams(logoViewLeftParams);
		Drawable logoViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.LOGO_LEFT);// 得到背景图片
		logoViewLeft.setBackgroundDrawable(logoViewLeftBg);
		//.........................................中间长白条............................................................
		RelativeLayout middleViewLeftRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams middleViewLeftRlParams = new RelativeLayout.LayoutParams((int) (815 * scale), (int) (150 * scale));
		middleViewLeftRlParams.topMargin = (int) (0 * scale);
		middleViewLeftRlParams.leftMargin = (int) (136 * scale);
		middleViewLeftRl.setLayoutParams(middleViewLeftRlParams);
		Drawable middleViewLeftRlBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MENUMIDDLE);// 得到背景图片
		middleViewLeftRl.setBackgroundDrawable(middleViewLeftRlBg);
		//..........................................账户..............................................................
		final ImageView accountViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams accountViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		accountViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		accountViewLeftParams.topMargin = (int) (0 * scale);
		accountViewLeftParams.leftMargin = (int) (0 * scale);
		accountViewLeft.setLayoutParams(accountViewLeftParams);
		final Drawable accountViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT);// 得到背景图片
		final Drawable accountViewLeftBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT_PRESS);// 按压
		accountViewLeft.setBackgroundDrawable(accountViewLeftBg);
		accountViewLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					accountViewLeft.setBackgroundDrawable(accountViewLeftBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					accountViewLeft.setBackgroundDrawable(accountViewLeftBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		accountViewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				new AccountManagerPopWindow(context, false, "", null,null,new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						
					}
				});
			}
		});
		
		//..........................................消息.............................................................
		final ImageView messageViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams messageViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		messageViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		messageViewLeftParams.topMargin = (int) (0 * scale);
		messageViewLeftParams.leftMargin = (int) (163 * scale);
		messageViewLeft.setLayoutParams(messageViewLeftParams);
		final Drawable messageViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MESSAGE);// 得到背景图片
		final Drawable messageViewLeftBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MESSAGE_PRESS);// 得到背景图片
		messageViewLeft.setBackgroundDrawable(messageViewLeftBg);
		messageViewLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					messageViewLeft.setBackgroundDrawable(messageViewLeftBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					messageViewLeft.setBackgroundDrawable(messageViewLeftBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		messageViewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				ToastUtil.showShort(context, "暂无新消息");
			}
		});
		//............................................订单......................................................................
		final ImageView orderViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams orderViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		orderViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		orderViewLeftParams.topMargin = (int) (0 * scale);
		orderViewLeftParams.leftMargin = (int) (326 * scale);
		orderViewLeft.setLayoutParams(orderViewLeftParams);
		final Drawable orderViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ORDER);// 得到背景图片
		final Drawable orderViewLeftBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ORDER_PRESS);// 得到背景图片
		orderViewLeft.setBackgroundDrawable(orderViewLeftBg);
		orderViewLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					orderViewLeft.setBackgroundDrawable(orderViewLeftBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					orderViewLeft.setBackgroundDrawable(orderViewLeftBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		orderViewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				showClauseActivity(context,1);
			}
		});
		//............................................社区............................................................................
		final ImageView communityViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams communityViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		communityViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		communityViewLeftParams.topMargin = (int) (0 * scale);
		communityViewLeftParams.leftMargin = (int) (489* scale);
		communityViewLeft.setLayoutParams(communityViewLeftParams);
		final Drawable communityViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.COMMUNITY);// 得到背景图片
		final Drawable communityViewLeftBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.COMMUNITY_PRESS);// 得到背景图片
		communityViewLeft.setBackgroundDrawable(communityViewLeftBg);
		communityViewLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					communityViewLeft.setBackgroundDrawable(communityViewLeftBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					communityViewLeft.setBackgroundDrawable(communityViewLeftBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		communityViewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				showClauseActivity(context,2);
			}
		});
		//.............................................客服......................................................................
		final ImageView serviceViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams serviceViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		serviceViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		serviceViewLeftParams.topMargin = (int) (0 * scale);
		serviceViewLeftParams.leftMargin = (int) (652 * scale);
		serviceViewLeft.setLayoutParams(serviceViewLeftParams);
		final Drawable serviceViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.SERVICE);// 得到背景图片
		final Drawable serviceViewLeftBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.SERVICE_PRESS);// 得到背景图片
		serviceViewLeft.setBackgroundDrawable(serviceViewLeftBg);
		serviceViewLeft.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					serviceViewLeft.setBackgroundDrawable(serviceViewLeftBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					serviceViewLeft.setBackgroundDrawable(serviceViewLeftBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		serviceViewLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				showClauseActivity(context,3);
			}
		});
		//...............................................右半圆...................................................................
		ImageView roundRightView = new ImageView(context);
		RelativeLayout.LayoutParams roundRightViewParams = new RelativeLayout.LayoutParams((int) (74 * scale), (int) (150* scale));
		roundRightViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		roundRightViewParams.topMargin = (int) (0 * scale);
		roundRightViewParams.leftMargin = (int) (951 * scale);
		roundRightView.setLayoutParams(roundRightViewParams);
		Drawable roundRightViewBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.WHITEROUND_RIGHT);// 得到背景图片
		roundRightView.setBackgroundDrawable(roundRightViewBg);
		menuLayoutLeft.addView(logoViewLeft);
		
		middleViewLeftRl.addView(accountViewLeft);
		middleViewLeftRl.addView(messageViewLeft);
		middleViewLeftRl.addView(orderViewLeft);
		middleViewLeftRl.addView(communityViewLeft);
		middleViewLeftRl.addView(serviceViewLeft);
		
		menuLayoutLeft.addView(middleViewLeftRl);
		
		menuLayoutLeft.addView(roundRightView);
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		menuLayoutLeftParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		menuLayoutLeft.setLayoutParams(menuLayoutLeftParams);
		rootView.addView(menuLayoutLeft);
		return rootView;
	}

	

	/**
	 * 展示界面
	 */
	protected void showClauseActivity(Context activityContext,int intentType) {
		Intent intent = new Intent(activityContext,FLSdkActivity.class);
		Bundle bundle = new Bundle();
	    intent.putExtra("fromtype",intentType);//表明是隐私条款意图
	    intent.putExtras(bundle);
	    ((Activity)activityContext).startActivity(intent);
	}
	@Override
	public void onDismiss() {
		
	}

	@Override
	public void showWindow() {
		MyLogger.lczLog().i("展示：MenuPopWindow");
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
		//mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);//pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
	}


	
}
