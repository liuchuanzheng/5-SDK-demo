package com.feiliu.flgamesdk.view.activity.toolbar;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.db.AccountDao;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SPConstant;
import com.feiliu.flgamesdk.listener.FLOnAccountManagerListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.SPUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.popupwindow.AccountManagerPopWindow;

/**
 * @author 刘传政
 * 飞流球  自定义view
 */
public class FlToolBar3 extends RelativeLayout implements OnTouchListener{
	private int toolBarPlace;
	private Context context;
	private Context activityContext;
	public float scale;//尺寸百分比
	private RelativeLayout toolbarView=null;//飞流球的view
	private WindowManager mWindowManager=null;
	private WindowManager.LayoutParams wmParams=null;
	private boolean isShowing=false;//飞流球是否显示
	private CountDownTimer grayCountDownTimer=null;//计时器，用于彩显变灰显判断
	private Drawable toolbarViewBgGray;//灰显图片
	private Drawable toolbarViewBgColor;//彩显图片
	private Drawable toolbarViewBgGrayTranslate;//彩显图片
	private ImageView accountViewLeft;//账户布局左
	private ImageView accountViewRight;//账户布局右
	private Drawable accountViewLeftBg;//账户左图片
	private Drawable accountViewRightBg;//账户右图片
	private boolean isGray = false;//是否是灰显状态
	private boolean isHalf = false;//是否半隐藏
	private boolean isleft;//飞流球靠边时的左右位置
	private boolean isMenuShow = false;
	private ObjectAnimator animatorLeft;
	private ObjectAnimator animatorRight;
	private boolean isOnpause = false;
	private boolean isInnerHide = true;//是否是内部正常隐藏
	private TranslatePopWindow translatePopWindow;//全屏透明pop
	private ToolBarReceiver toolBarReceiver;

	/**
	 * 记录飞流球触摸的坐标。为了记录飞流球的上次位置
	 */
	private float iniTouchX, iniTouchY;
	/**
	 * 飞流球是否是拖动状态
	 */
	private boolean isMoving = false;
	/**
	 * 记录抬起时触摸坐标。
	 */
	private float upTouchX,upTouchY;
	/**
	 * 飞流球图标合适的宽高
	 */
	private int iconWidth, iconHeight;
	/**
	 * 飞流球拖动的最小距离。防止拖动事件和点击事件冲突
	 */
	private  int minMoveLeng;
	private ToolBarTouchListener toolBarTouchListener;//飞流球触摸监听
	private ToolBarClickListener toolBarClickListener;//飞流球点击监听
	private RelativeLayout.LayoutParams toolbarViewParams;
	private RelativeLayout rootView;
	private LayoutParams rootViewParams;
	private RelativeLayout menuLayoutLeft;
	private RelativeLayout menuLayoutRight;
	private boolean hasBindPhone;//是否绑定了手机号
	private boolean hasBindEmail;//是否绑定了邮箱
	private HasBindPhoneReceiver receiver;//手机号绑定的广播接收者
	private FLOnLoginListener loginListener;
	private FLOnAccountManagerListener fLOnAccountManagerListener;
	class ToolBarReceiver extends BroadcastReceiver {

		public void onReceive(Context arg0, Intent intent) {
			unregisteMe(toolBarReceiver);
			String toolBarAction = intent.getStringExtra("toolBarAction");
			switch (toolBarAction) {
			case "refrush":
				//刷新
				show();
				break;

			default:
				break;
			}
			MyLogger.lczLog().i("接收到了飞流球状态广播"+toolBarAction);
		}

		public void registeMe(ToolBarReceiver receiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.feiliu.flgamesdk.ToolBarReceiver");
			context.registerReceiver(receiver, filter);
		}

		public void unregisteMe(ToolBarReceiver receiver) {
			context.unregisterReceiver(receiver);
		}


	}
	
	public FlToolBar3(Context context) {
		super(context);
	}
    public FlToolBar3(Context context,int toolBarPlace, FLOnLoginListener loginListener, FLOnAccountManagerListener fLOnAccountManagerListener) {
    	super(context);
    	this.context = context.getApplicationContext();
    	this.activityContext = context;
    	this.toolBarPlace = toolBarPlace;
    	this.loginListener = loginListener;
    	this.fLOnAccountManagerListener = fLOnAccountManagerListener;
    	init();
		
		
    }
    class HasBindPhoneReceiver extends BroadcastReceiver {

		public void onReceive(Context arg0, Intent intent) {
			String bindphone = intent.getStringExtra("bindphone");
			switch (bindphone) {
			case "yes":
				
				show();
				break;
			case "realNameCompleted":
				show();
				break;
			
			default:
				break;
			}
			MyLogger.lczLog().i("接收到了HasBindPhoneReceiver广播"+bindphone);
		}

		public void registeMe(HasBindPhoneReceiver receiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("com.feiliu.flgamesdk.HasBindPhoneReceiver");
			context.registerReceiver(receiver, filter);
		}

		public void unregisteMe(HasBindPhoneReceiver receiver) {
			context.unregisterReceiver(receiver);
		}


	}
    
    private void init() {
    	scale = UiSizeUtil.getScale(this.context);
    	iconWidth = (int) (143 * scale);
		iconHeight = (int) (150 * scale);
		minMoveLeng = (int) (5*scale);
		if (0 == toolBarPlace) {
			isleft = true;
		}else if (1 == toolBarPlace) {
			isleft = false;
		}
		receiver = new HasBindPhoneReceiver();
		receiver.registeMe(receiver);
		initPictures();
		initView();
		initWindowManager();
	}
	/**
	 * 根据账号类型决定是否显示带红点的图片
	 */
	public void initPictures() {
		AccountDao accountDao = new AccountDao(context);
		List<AccountDBBean> accountDBBeans = accountDao.findAllIncludeGuest();
		hasBindPhone = false;
		hasBindEmail = false;
		//是否实名认证   控制小红点显示
		SPUtils spUtils = new SPUtils(context, SPConstant.USERINFO);
		boolean isRealName = spUtils.getBoolean("realName", false);
		MyLogger.lczLog().i("是否已经实名制"+isRealName);
		if (accountDBBeans.size() > 0) {
			AccountDBBean recentAccountDBBean = accountDBBeans.get(0);
			String phone = recentAccountDBBean.getPhone();
			String email = recentAccountDBBean.getEmail();
			if (phone != null && !phone.equals("")) {
				hasBindPhone = true;
			}
			
			if (email != null && !email.equals("") ) {
				hasBindEmail = true;
			}
		}
		if (hasBindPhone) {
			toolbarViewBgGray = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GARYTOOLBAR);
			toolbarViewBgColor = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.COLORTOOLBAR);
			toolbarViewBgGrayTranslate = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GRAYTRANSLATE_TOOBAR);
			
			if (hasBindEmail && isRealName) {
				accountViewLeftBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT);
				accountViewRightBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT);
			}else {
				accountViewLeftBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED);
				accountViewRightBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED);
			}
			
		}else {
			toolbarViewBgGray = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GARYTOOLBAR_POINT);
			toolbarViewBgColor = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.COLORTOOLBAR_POINT);
			toolbarViewBgGrayTranslate = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GRAYTRANSLATE_TOOBAR_POINT);
			accountViewLeftBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED);
			accountViewRightBg = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ACCOUNT_RED);
		}
		
	}
    //部分手机，隐藏时当前view的成员变量可能会为空
    /**
     * 所有SDK内部隐藏用这个方法
     */
    public void hide() {
    	isInnerHide = true;
        try {
            this.setVisibility(View.GONE);
            menuLayoutRight.setVisibility(View.GONE);
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        }
        if (translatePopWindow != null) {
				translatePopWindow.mPopupWindow.dismiss();
				translatePopWindow = null;
     	 }
        MyLogger.lczLog().i("hide");
    }
    public void show() {
    	toolBarReceiver = new ToolBarReceiver();
    	toolBarReceiver.registeMe(toolBarReceiver);
    	initPictures();
		toolbarView.setBackgroundDrawable(toolbarViewBgColor);
		if (null != accountViewLeft) {
			accountViewLeft.setBackgroundDrawable(accountViewLeftBg);
		}
		if (null != accountViewRight) {
			accountViewRight.setBackgroundDrawable(accountViewRightBg);
		}
    	if (isInnerHide) {
    		 setVisibility(View.VISIBLE);
    		 if (isMenuShow) {
				if (!isleft) {
					menuLayoutRight.setVisibility(View.VISIBLE);
				}
    		 }
    	     mWindowManager.updateViewLayout(this, wmParams);
    	     reSetLocation();
    	     isInnerHide = false;
		}
    	 MyLogger.lczLog().i("show");
       
    }
    /**
     * 对应onpause方法。为了区分是cp隐藏还是，代码正常隐藏，以便onresume时决定是否显示飞流球。否则
     * 会显示很多个飞流球，导致能点击出多个popwindow
     */
    public void handHide() {
    	hideMenu();//隐藏菜单，为了菜单显示情况下，锁屏和home键都不会影响菜单收起动画。
    	//如果飞流球正在显示
    	if (!isInnerHide) {
    		isOnpause = true;
            try {
                setVisibility(View.GONE);
                menuLayoutRight.setVisibility(View.GONE);
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            }
		}
    	MyLogger.lczLog().i("handHide");
    	
    }

    public void handShow() {
    	if (isOnpause && !isInnerHide) {
    		 setVisibility(View.VISIBLE);
    		 if (isMenuShow) {
 				if (!isleft) {
 					menuLayoutRight.setVisibility(View.VISIBLE);
 				}
     		 }
    	     mWindowManager.updateViewLayout(this, wmParams);
    	     isOnpause = false;
		}
    	MyLogger.lczLog().i("handShow");
    }
    
    /**
	 * 创建飞流球的view  此view的子view，真正用于触摸点击的view。
	 */
	private void initView() {
		//.......................................根布局.......................................................................
		
		if (null == rootView ) {
			rootView = new  RelativeLayout(context);
		}
		rootViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rootView.setLayoutParams(rootViewParams);
		
		//........................................飞流球......................................................................
		
		if (null == toolbarView ) {
			toolbarView = new RelativeLayout(context);
		}
		toolbarViewParams = new RelativeLayout.LayoutParams(iconWidth, iconHeight);
		toolbarViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		toolbarView.setLayoutParams(toolbarViewParams);
		toolbarView.setBackgroundDrawable(toolbarViewBgColor);
		
		//..........................................飞流球菜单左.......................................................................
		
		menuLayoutLeft = new RelativeLayout(context);
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
		accountViewLeft = new ImageView(context);
		RelativeLayout.LayoutParams accountViewLeftParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		accountViewLeftParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		accountViewLeftParams.topMargin = (int) (0 * scale);
		accountViewLeftParams.leftMargin = (int) (0 * scale);
		accountViewLeft.setLayoutParams(accountViewLeftParams);
		//final Drawable accountViewLeftBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT);// 得到背景图片
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
				if (!isMoving) {
		             if (menuLayoutLeft.getVisibility() == View.VISIBLE || menuLayoutRight.getVisibility() == View.VISIBLE) {
		            	 isMenuShow = false;
		            	 if (isleft) {
		            		wmParams.width = LayoutParams.WRAP_CONTENT;
		             		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
		            		menuLayoutLeft.setVisibility(View.GONE);
		                	menuLayoutRight.setVisibility(View.GONE);
		                	toolbarView.setVisibility(View.VISIBLE);
		            	 }else {
		            		wmParams.width = LayoutParams.WRAP_CONTENT;
		              		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
		            		menuLayoutLeft.setVisibility(View.GONE);
		            		menuLayoutRight.setVisibility(View.GONE);
							toolbarView.setVisibility(View.VISIBLE);
		            	 }
		             } 
		         }
				hide();
				new AccountManagerPopWindow(activityContext, false, "", loginListener,fLOnAccountManagerListener,new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						show();
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
				hideMenu();
				showClauseActivity(activityContext,1);
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
				hideMenu();
				showClauseActivity(activityContext,2);
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
				hideMenu();
				showClauseActivity(activityContext,3);
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
		
		//............................................飞流球菜单右........................................................................
		menuLayoutRight = new RelativeLayout(context);
		RelativeLayout.LayoutParams menuLayoutRithtParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (150 * scale));
		menuLayoutRithtParams.topMargin = (int) (0 * scale);
		menuLayoutRithtParams.leftMargin = (int) (0 * scale);
		menuLayoutRight.setLayoutParams(menuLayoutRithtParams);
		/*GradientDrawable shapeRight = new GradientDrawable();
		shapeRight.setCornerRadius(80*scale);
		shapeRight.setColor(Color.WHITE);
		menuLayoutRight.setBackgroundDrawable(shapeRight);*/
		menuLayoutRight.setBackgroundColor(Color.TRANSPARENT);
		//........................................左半圆...........................................................
		ImageView roundLeftView = new ImageView(context);
		RelativeLayout.LayoutParams roundLeftViewParams = new RelativeLayout.LayoutParams((int) (74 * scale), (int) (150* scale));
		roundLeftViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		roundLeftViewParams.topMargin = (int) (0 * scale);
		roundLeftViewParams.leftMargin = (int) (0 * scale);
		roundLeftView.setLayoutParams(roundLeftViewParams);
		Drawable roundLeftViewBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.WHITEROUND_LEFT);// 得到背景图片
		roundLeftView.setBackgroundDrawable(roundLeftViewBg);
		//.........................................中间长白条............................................................
		RelativeLayout middleViewRightRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams middleViewRightRlParams = new RelativeLayout.LayoutParams((int) (815 * scale), (int) (150 * scale));
		middleViewRightRlParams.topMargin = (int) (0 * scale);
		middleViewRightRlParams.leftMargin = (int) (74 * scale);
		middleViewRightRl.setLayoutParams(middleViewRightRlParams);
		Drawable middleViewRightRlBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MENUMIDDLE);// 得到背景图片
		middleViewRightRl.setBackgroundDrawable(middleViewRightRlBg);
		//..........................................账户..............................................................
		accountViewRight = new ImageView(context);
		RelativeLayout.LayoutParams accountViewRightParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		accountViewRightParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		accountViewRightParams.topMargin = (int) (0 * scale);
		accountViewRightParams.leftMargin = (int) (0 * scale);
		accountViewRight.setLayoutParams(accountViewRightParams);
		//final Drawable accountViewRightBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT);// 得到背景图片
		final Drawable accountViewRightBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ACCOUNT_PRESS);// 得到背景图片
		accountViewRight.setBackgroundDrawable(accountViewRightBg);
		accountViewRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					accountViewRight.setBackgroundDrawable(accountViewRightBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					accountViewRight.setBackgroundDrawable(accountViewRightBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		accountViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isMoving) {
		             if (menuLayoutLeft.getVisibility() == View.VISIBLE || menuLayoutRight.getVisibility() == View.VISIBLE) {
		            	 isMenuShow = false;
		            	 if (isleft) {
		            		wmParams.width = LayoutParams.WRAP_CONTENT;
		             		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
		            		menuLayoutLeft.setVisibility(View.GONE);
		                	menuLayoutRight.setVisibility(View.GONE);
		                	toolbarView.setVisibility(View.VISIBLE);
		            	 }else {
		            		wmParams.width = LayoutParams.WRAP_CONTENT;
		              		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
		            		menuLayoutLeft.setVisibility(View.GONE);
		            		menuLayoutRight.setVisibility(View.GONE);
							toolbarView.setVisibility(View.VISIBLE);
		            	 }
		             } 
		         }
				hide();
				new AccountManagerPopWindow(activityContext, false, "", loginListener,fLOnAccountManagerListener,new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						show();
					}
				});
			}
		});
		//..........................................消息.............................................................
		final ImageView messageViewRight = new ImageView(context);
		RelativeLayout.LayoutParams messageViewRightParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		messageViewRightParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		messageViewRightParams.topMargin = (int) (0 * scale);
		messageViewRightParams.leftMargin = (int) (163 * scale);
		messageViewRight.setLayoutParams(messageViewRightParams);
		final Drawable messageViewRightBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MESSAGE);// 得到背景图片
		final Drawable messageViewRightBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.MESSAGE_PRESS);// 得到背景图片
		messageViewRight.setBackgroundDrawable(messageViewRightBg);
		messageViewRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					messageViewRight.setBackgroundDrawable(messageViewRightBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					messageViewRight.setBackgroundDrawable(messageViewRightBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		messageViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToastUtil.showShort(context, "暂无新消息");
			}
		});
		//............................................订单......................................................................
		final ImageView orderViewRight = new ImageView(context);
		RelativeLayout.LayoutParams orderViewRightParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		orderViewRightParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		orderViewRightParams.topMargin = (int) (0 * scale);
		orderViewRightParams.leftMargin = (int) (326 * scale);
		orderViewRight.setLayoutParams(orderViewRightParams);
		final Drawable orderViewRightBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ORDER);// 得到背景图片
		final Drawable orderViewRightBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.ORDER_PRESS);// 得到背景图片
		orderViewRight.setBackgroundDrawable(orderViewRightBg);
		orderViewRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					orderViewRight.setBackgroundDrawable(orderViewRightBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					orderViewRight.setBackgroundDrawable(orderViewRightBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		orderViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu();
				showClauseActivity(activityContext,1);
			}
		});
		//............................................社区............................................................................
		final ImageView communityViewRight = new ImageView(context);
		RelativeLayout.LayoutParams communityViewRightParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		communityViewRightParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		communityViewRightParams.topMargin = (int) (0 * scale);
		communityViewRightParams.leftMargin = (int) (489 * scale);
		communityViewRight.setLayoutParams(communityViewRightParams);
		final Drawable communityViewRightBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.COMMUNITY);// 得到背景图片
		final Drawable communityViewRightBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.COMMUNITY_PRESS);// 得到背景图片
		communityViewRight.setBackgroundDrawable(communityViewRightBg);
		communityViewRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					communityViewRight.setBackgroundDrawable(communityViewRightBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					communityViewRight.setBackgroundDrawable(communityViewRightBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		communityViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu();
				showClauseActivity(activityContext,2);
			}
		});
		//.............................................客服......................................................................
		final ImageView serviceViewRight = new ImageView(context);
		RelativeLayout.LayoutParams serviceViewRightParams = new RelativeLayout.LayoutParams((int) (163 * scale), (int) (150* scale));
		serviceViewRightParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		serviceViewRightParams.topMargin = (int) (0 * scale);
		serviceViewRightParams.leftMargin = (int) (652 * scale);
		serviceViewRight.setLayoutParams(serviceViewRightParams);
		final Drawable serviceViewRightBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.SERVICE);// 得到背景图片
		final Drawable serviceViewRightBg_press = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.SERVICE_PRESS);// 得到背景图片
		serviceViewRight.setBackgroundDrawable(serviceViewRightBg);
		serviceViewRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()==MotionEvent.ACTION_DOWN){
					serviceViewRight.setBackgroundDrawable(serviceViewRightBg_press);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					serviceViewRight.setBackgroundDrawable(serviceViewRightBg);
				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
		serviceViewRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu();
				showClauseActivity(activityContext,3);
			}
		});
		//...............................................右logo...................................................................
		ImageView logoRightView = new ImageView(context);
		RelativeLayout.LayoutParams logoRightViewParams = new RelativeLayout.LayoutParams((int) (136 * scale), (int) (150* scale));
		logoRightViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
		logoRightViewParams.topMargin = (int) (0 * scale);
		logoRightViewParams.leftMargin = (int) (889 * scale);
		logoRightView.setLayoutParams(logoRightViewParams);
		Drawable logoRightViewBg = GetSDKPictureUtils.getDrawable(context,PictureNameConstant.LOGO_RIGHT);// 得到背景图片
		logoRightView.setBackgroundDrawable(logoRightViewBg);
		
		//.................................................左.........................................................
		menuLayoutLeft.addView(logoViewLeft);
		
		middleViewLeftRl.addView(accountViewLeft);
		middleViewLeftRl.addView(messageViewLeft);
		middleViewLeftRl.addView(orderViewLeft);
		middleViewLeftRl.addView(communityViewLeft);
		middleViewLeftRl.addView(serviceViewLeft);
		
		menuLayoutLeft.addView(middleViewLeftRl);
		
		menuLayoutLeft.addView(roundRightView);
		
		menuLayoutLeft.setVisibility(View.GONE);
		
		//...................................................右....................................................
		menuLayoutRight.addView(roundLeftView);
		
		middleViewRightRl.addView(accountViewRight);
		middleViewRightRl.addView(messageViewRight);
		middleViewRightRl.addView(orderViewRight);
		middleViewRightRl.addView(communityViewRight);
		middleViewRightRl.addView(serviceViewRight);
		
		menuLayoutRight.addView(middleViewRightRl);
		
		menuLayoutRight.addView(logoRightView);
		
		menuLayoutRight.setVisibility(View.GONE);
		if (null == toolBarClickListener) {
			toolBarClickListener = new ToolBarClickListener();
		}
		menuLayoutRight.setOnClickListener(toolBarClickListener);
		//...................................................................
		rootView.addView(menuLayoutLeft);
		//rootView.addView(menuLayoutRight);//如果加到rootView上，右侧收起菜单时会有视觉差。  决定将右侧菜单加到mWindowManager上
		rootView.addView(toolbarView);
		
		//使飞流球可以超出父容器之外
		rootView.setClipChildren(false);
		rootView.setClipToPadding(false);
		toolbarView.setClipChildren(false);
		toolbarView.setClipToPadding(false);
		
		//设置当前飞流球的touchListener事件，此listerner设置后，ontouchevent方法失效
		if (null == toolBarTouchListener) {
			toolBarTouchListener = new ToolBarTouchListener();
		}
		rootView.setOnTouchListener(this);
		if (null == toolBarClickListener) {
			toolBarClickListener = new ToolBarClickListener();
		}
		rootView.setOnClickListener(toolBarClickListener);
		 //悬浮菜单的父容器设置的测量模式为大小完全不能确定
        rootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	}
	/**
	 * 设置windowmannager
	 */
	private void initWindowManager() {
		mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);			
		wmParams = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//type跟优先级有关.之前为TYPE_TOAST的时候再魅族mx2和魅族mx3手机上飞流球无法获取焦点
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	 if(Build.VERSION.SDK_INT > 24){
        		 	//7.1.1以上手机必须这样.否则崩溃
        		 	wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        		 	
        	 }else{
        	    	wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;//大部分手机不需要权限
        	 }
        } else {
        	wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;//如果仍为toast，获取不到点击事件。
        }
        wmParams.format = PixelFormat.RGBA_8888;//当前窗口的像素格式为RGBA_8888,即为最高质量
        //NOT_FOCUSABLE可以是悬浮控件可以响应事件，LAYOUT_IN_SCREEN可以指定悬浮球指定在屏幕内，部分虚拟按键的手机，虚拟按键隐藏时，虚拟按键的位置则属于屏幕内，此时悬浮球会出现在原虚拟按键的位置
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
		wmParams.packageName = context.getPackageName();
		wmParams.horizontalWeight = 0;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
		addView(rootView);
		//将当前view设置为前置窗口
        bringToFront();
        this.setVisibility(View.GONE);
        initXY();
        mWindowManager.addView(menuLayoutRight, wmParams);
        mWindowManager.addView(this, wmParams);
        mWindowManager.updateViewLayout(this, wmParams);
        
	}
	/**
	 * 初始化飞流球位置，默认左侧中间。下次会读取sp，显示上次位置
	 */
	private void initXY() {
		SPUtils mSpUtils = new SPUtils(context, SPConstant.TOOLBARXY);
		int toolbarx = mSpUtils.getInt("toolbarx",0);
		int toolbary = mSpUtils.getInt("toolbary",getScreenHeight(context)/2);
		wmParams.x = toolbarx;
		wmParams.y = toolbary;
	}
	@Override
    protected void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	//这里获取的是切换屏幕或的方向
    	 if (getVisibility() == View.GONE) {
             return;
         }
    	 hideMenu();
    	 
         DisplayMetrics dm = new DisplayMetrics();
         mWindowManager.getDefaultDisplay().getMetrics(dm);
         int oldX = wmParams.x;
         int oldY = wmParams.y;
         switch (newConfig.orientation) {
             case Configuration.ORIENTATION_LANDSCAPE://横屏
                 if (!isleft) {
                	 //右
                     wmParams.x = getScreenWidth(context);
                     wmParams.y = oldY;
                 } else {
                	 wmParams.x = oldX;
                	 wmParams.y = oldY;
                 }
                 break;
             case Configuration.ORIENTATION_PORTRAIT://竖屏
                 if (!isleft) {
                	 wmParams.x = getScreenWidth(context);
                	 wmParams.y = oldY;
                 } else {
                	 wmParams.x = oldX;
                	 wmParams.y = oldY;
                 }
                 break;
         }
         mWindowManager.updateViewLayout(this, wmParams);
    }
	
	public class ToolBarClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			MyLogger.lczLog().i("点击了");
			 showOrHideMenu();
		}

		
	}
	/**
	 * 显示菜单
	 */
	private void showMenu() {
		if (!isMoving) {
            if (menuLayoutLeft.getVisibility() == View.VISIBLE || menuLayoutRight.getVisibility() == View.VISIBLE) {
            	
            } else {
           	 	isMenuShow = true;
           	 	toolbarView.setVisibility(View.INVISIBLE);
           	 	if (isleft) {
						//换左边图片
           	 		wmParams.width = (int) (1025 * scale);//这样做是为了能让菜单显示完全。否则菜单的右侧有一部分显示不全
           	 		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
           	 		menuLayoutLeft.setVisibility(View.VISIBLE);
           	 		menuLayoutRight.setVisibility(View.GONE);
           	 	}else {
						//换右边图片
           	 		menuLayoutLeft.setVisibility(View.GONE);
           	 		menuLayoutRight.setVisibility(View.VISIBLE);
           	 		wmParams.width = (int) (1025 * scale);//这样做是为了能让菜单显示完全。否则菜单的右侧有一部分显示不全
           	 		mWindowManager.updateViewLayout(menuLayoutRight, wmParams);
           	 	}
           	 
           }
        }
	}
	/**
	 * 隐藏菜单
	 */
	private void hideMenu() {
		if (!isMoving) {
             if (menuLayoutLeft.getVisibility() == View.VISIBLE || menuLayoutRight.getVisibility() == View.VISIBLE) {
            	 isMenuShow = false;
            	 if (isleft) {
            		wmParams.width = LayoutParams.WRAP_CONTENT;
             		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
            		menuLayoutLeft.setVisibility(View.GONE);
                	menuLayoutRight.setVisibility(View.GONE);
                	toolbarView.setVisibility(View.VISIBLE);
            	 }else {
            		wmParams.width = LayoutParams.WRAP_CONTENT;
              		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
            		menuLayoutLeft.setVisibility(View.GONE);
            		menuLayoutRight.setVisibility(View.GONE);
					toolbarView.setVisibility(View.VISIBLE);
            	 }
            	 if (translatePopWindow != null) {
 					translatePopWindow.mPopupWindow.dismiss();
 					translatePopWindow = null;
             	 }
            	 reSetLocation();
            	
             } 
         }
	}
	/**
	 * 显示或隐藏菜单。对应点击事件
	 */
	private void showOrHideMenu() {
		if (!isMoving) {
             if (menuLayoutLeft.getVisibility() == View.VISIBLE || menuLayoutRight.getVisibility() == View.VISIBLE) {
            	 isMenuShow = false;
            	 if (isleft) {
            		wmParams.width = LayoutParams.WRAP_CONTENT;
             		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
            		menuLayoutLeft.setVisibility(View.GONE);
                	menuLayoutRight.setVisibility(View.GONE);
                	toolbarView.setVisibility(View.VISIBLE);
            	 }else {
            		wmParams.width = LayoutParams.WRAP_CONTENT;
              		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
            		menuLayoutLeft.setVisibility(View.GONE);
            		menuLayoutRight.setVisibility(View.GONE);
					toolbarView.setVisibility(View.VISIBLE);
            	 }
            	 if (translatePopWindow != null) {
					translatePopWindow.mPopupWindow.dismiss();
					translatePopWindow = null;
            	 }
            	 reSetLocation();
            	
             } else {
            	 isMenuShow = true;
            	 toolbarView.setVisibility(View.INVISIBLE);
            	 if (isleft) {
						//换左边图片
            		wmParams.width = (int) (1025 * scale);//这样做是为了能让菜单显示完全。否则菜单的右侧有一部分显示不全
            		mWindowManager.updateViewLayout(FlToolBar3.this, wmParams);
             		menuLayoutLeft.setVisibility(View.VISIBLE);
             		menuLayoutRight.setVisibility(View.GONE);
				}else {
						//换右边图片
					menuLayoutLeft.setVisibility(View.GONE);
             		menuLayoutRight.setVisibility(View.VISIBLE);
             		wmParams.width = (int) (1025 * scale);//这样做是为了能让菜单显示完全。否则菜单的右侧有一部分显示不全
             		mWindowManager.updateViewLayout(menuLayoutRight, wmParams);
				}
            	 translatePopWindow = new TranslatePopWindow(activityContext, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						hideMenu();
					}
				});

             }
         }
	}
	/**
	 * 飞流球的触摸监听，飞流球的核心
	 * @author 刘传政
	 *
	 */
	public class ToolBarTouchListener implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			//getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
			if (event.getAction()==MotionEvent.ACTION_DOWN){
				
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				
			}else if(event.getAction()==MotionEvent.ACTION_MOVE){
				
			}
			return false;  //此处必须返回false，否则OnClickListener获取不到监听
		}
	}
	/**
	 * 取消灰显和半隐藏等一系列动作
	 */
	private void cancleActions() {
		if (null != grayCountDownTimer) {
			grayCountDownTimer.cancel();//取消倒计时灰显
			grayCountDownTimer = null;
		}
		if (null != animatorLeft) {
			animatorLeft.end();
			animatorLeft = null;
		}
		if (null != animatorRight) {
			animatorRight.end();
			animatorRight = null;
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isMenuShow) {
			//菜单不显示时才拦截
			//getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
			if (event.getAction()==MotionEvent.ACTION_DOWN){
				iniTouchX = event.getRawX();
				iniTouchY = event.getRawY();
				isMoving = false;
				cancleActions();
				toolbarView.setBackgroundDrawable(toolbarViewBgColor);
				if (isleft) {
					animatorLeft = ObjectAnimator.ofFloat(toolbarView,"translationX",-toolbarView.getMeasuredWidth()/2,0);
					animatorLeft.setDuration(0);  
					animatorLeft.start();
					animatorLeft = null;
				
			    }else {
					animatorRight = ObjectAnimator.ofFloat(toolbarView,"translationX",toolbarView.getMeasuredWidth()/2,0);  
					animatorRight.setDuration(0);  
					animatorRight.start();
					animatorRight = null;
				
			    }
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				upTouchX = event.getRawX();
				upTouchY = event.getRawY();
				if (Math.abs(iniTouchX - (int) event.getRawX()) > minMoveLeng 
						|| (Math.abs(iniTouchY - (int) event.getRawY()) > minMoveLeng)) {
					reSetLocation();
				}
				
				
			}else if(event.getAction()==MotionEvent.ACTION_MOVE){
				
				if (Math.abs(iniTouchX - (int) event.getRawX()) > minMoveLeng 
						|| (Math.abs(iniTouchY - (int) event.getRawY()) > minMoveLeng)) {
					isMoving = true;
					isGray = false;
					wmParams.x = (int) event.getRawX() -  toolbarView.getMeasuredWidth()/2;
					wmParams.y = (int) event.getRawY() -  toolbarView.getMeasuredHeight()/2;
				    mWindowManager.updateViewLayout(this, wmParams);
				}
				
			}
			return false;  //此处必须返回false，否则OnClickListener获取不到监听
		}
		return false;
	}
	/**
	 * 重置飞流球的位置
	 */
	private void reSetLocation(){
		int x = wmParams.x;
		// 比较到到哪个边近，就靠哪个边  目前只能靠近左边和右边
		int left = x + toolbarView.getMeasuredWidth() / 2;
		int right = getScreenWidth(context) - left;
		if (left <= right) {
			//靠左
			x = 0;
			isleft = true;
		}else {
			//靠右
			x = getScreenWidth(context)-toolbarView.getMeasuredWidth();
			isleft = false;
		}
		wmParams.x = x;
		mWindowManager.updateViewLayout(this, wmParams);
		storageXY(wmParams.x,wmParams.y);
		isHalf = false;
		startGrayTimeCoutDown(2000);
	}
	/**
	 * 存储当前坐标位置，以便下次启动的时候能显示在上次的位置
	 * @param y 
	 * @param x 
	 */
	private void storageXY(int x, int y) {
		SPUtils mSpUtils = new SPUtils(context, SPConstant.TOOLBARXY);
		mSpUtils.putInt("toolbarx",x);
		mSpUtils.putInt("toolbary",y);
	}
	private void startGrayTimeCoutDown(long totalTime) {
		grayCountDownTimer=	new CountDownTimer(totalTime,1000) {
			
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				toolbarView.setBackgroundDrawable(toolbarViewBgGray);
				isGray = true;
				if (isleft) {
					animatorLeft = ObjectAnimator.ofFloat(toolbarView,"translationX",0,-toolbarView.getMeasuredWidth()/2);  
					animatorLeft.setDuration(500);  
					animatorLeft.start();
					animatorLeft.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator animation) {
							
						}
						
						@Override
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							toolbarView.setBackgroundDrawable(toolbarViewBgGrayTranslate);
							isHalf = true;
							animatorLeft = null;
						}
						
						@Override
						public void onAnimationCancel(Animator animation) {
							
						}
					});
					
					
				}else {
					animatorRight = ObjectAnimator.ofFloat(toolbarView,"translationX",0,toolbarView.getMeasuredWidth()/2);  
					animatorRight.setDuration(500);  
					animatorRight.start();
					animatorRight.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator animation) {
							
						}
						
						@Override
						public void onAnimationRepeat(Animator animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							toolbarView.setBackgroundDrawable(toolbarViewBgGrayTranslate);
							animatorRight = null;
							isHalf = true;
						}
						
						@Override
						public void onAnimationCancel(Animator animation) {
							
						}
					});
				}
				
			}
		}.start();
	}
	/**
	 * 展示隐私条款界面
	 */
	protected void showClauseActivity(Context activityContext,int intentType) {
		Intent intent = new Intent(activityContext,FLSdkActivity.class);
		Bundle bundle = new Bundle();
	    intent.putExtra("fromtype",intentType);//表明是隐私条款意图
	    intent.putExtras(bundle);
	    ((Activity)activityContext).startActivity(intent);
	}
	/**
	 * 获取屏幕宽
	 * 
	 * @param context
	 * @return
	 */
	public int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		return width;
	}

	/**
	 * 获取屏幕高
	 * 
	 * @param context
	 * @return
	 */
	public int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int height = dm.heightPixels;
		return height;
	}

}
