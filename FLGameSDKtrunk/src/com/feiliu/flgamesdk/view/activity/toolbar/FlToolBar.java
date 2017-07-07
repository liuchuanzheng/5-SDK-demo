package com.feiliu.flgamesdk.view.activity.toolbar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.FLBallPlace;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.toolBarMenuPopWindow;

/**
 * @author 刘传政
 * 飞流球
 * 不使用
 */
public class FlToolBar {

	private int toolBarPlace;
	private Context context;
	public float scale;//尺寸百分比
	private LinearLayout toolbarView=null;//飞流球的view
	private WindowManager mWindowManager=null;
	private WindowManager.LayoutParams wmParams=null;
	private boolean isShowing=false;//飞流球是否显示
	private CountDownTimer countDownTimer=null;//计时器，用于彩显变灰显判断
	private Drawable toolbarViewBgGray;//灰显图片
	private Drawable toolbarViewBgColor;//彩显图片
	private Drawable toolbarViewBgGrayTranslate;//彩显图片
	private boolean isGray = false;//是否是灰显状态
	private boolean isHalf = false;//是否半隐藏
	private boolean isleft;//飞流球靠边时的左右位置
	private ObjectAnimator animatorLeft;
	private ObjectAnimator animatorRight;
	private toolBarMenuPopWindow toolBarMenuPopWindow;//条形菜单
	private boolean isMenuShow = false;
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
	public FlToolBar(Context context,int toolBarPlace) {
		this.context = context;
		this.toolBarPlace = toolBarPlace;
		scale = UiSizeUtil.getScale(this.context);
		iconWidth = (int) (150 * scale);
		iconHeight = (int) (150 * scale);
		minMoveLeng = (int) (5*scale);
		if (0 == toolBarPlace) {
			isleft = true;
		}else if (0 == toolBarPlace) {
			isleft = false;
		}
		toolbarViewBgGray = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GARYTOOLBAR);
		toolbarViewBgColor = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.COLORTOOLBAR);
		toolbarViewBgGrayTranslate = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.GRAYTRANSLATE_TOOBAR);
	}
	public void show(){
		if (!isShowing) {
			isShowing = true;
			cancleActions();
			if(toolbarView == null){
				initView();
				initWindowManager();
				mWindowManager.addView(toolbarView,wmParams);
			}else {
				toolbarView.setVisibility(View.VISIBLE);
			}
			reSetLocation();
			
			
		}
		
	}
	/**
	 * 仅用于飞流球菜单控制显示
	 * 由于才有弹出菜单再隐藏飞流的方式不如直接让飞流球覆盖菜单效果好 ,暂不使用此方法
	 */
	private void showAfterMenuHide(){
		if (!isShowing) {
			isShowing = true;
			cancleActions();
			toolbarView = null;
			initView();
			mWindowManager.addView(toolbarView,wmParams);
			reSetLocation();
		}
		
	}
	/**
	 * 设置windowmannager
	 */
	private void initWindowManager() {
		mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);			
		wmParams = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//wmParams.type= WindowManager.LayoutParams.TYPE_TOAST;//TYPE_SYSTEM_OVERLAY;//TYPE_SYSTEM_ALERT;//TYPE_SYSTEM_ALERT;//TYPE_PHONE;// //type是关键，这里的TYPE_PHONE 2002表示系统级窗口，你也可以试试2003( TYPE_SYSTEM_ALERT)。需要android.permission.SYSTEM_ALERT_WINDOW权限
		//type跟优先级有关.之前为TYPE_TOAST的时候再魅族mx2和魅族mx3手机上飞流球无法获取焦点
		//判断是否是小米手机,决定type类型.因为小米手机弹窗需要手动开启权限
				
		wmParams.type= WindowManager.LayoutParams.TYPE_PHONE;
		wmParams.format = 1;
		/*
	      * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
	      */
	    //wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		wmParams.flags=LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.width=iconWidth;
		wmParams.height=iconHeight;
		wmParams.packageName = context.getPackageName();
		wmParams.horizontalWeight = 0;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至右侧中间
		initLocation(toolBarPlace);
	}
	/**
	 * 设置飞流球的位置
	 * @param toolBarPlace
	 * 
	 */
	private void initLocation(int toolBarPlace) {
		int x=0,y=0;
		switch(toolBarPlace){
			case FLBallPlace.Left:
		        x = 0;
		        y=getScreenHeight(context)/2;
				break;
			case FLBallPlace.Right:
				x=getScreenWidth(context)-toolbarView.getMeasuredWidth()/2;
				y=getScreenHeight(context)/2;
				break;
		}
		wmParams.x =x;
		wmParams.y =y;
	}
	/**
	 * 创建飞流球的view
	 */
	private void initView() {
		if (null == toolbarView) {
			toolbarView = new LinearLayout(context.getApplicationContext());
		}
		toolbarView.setBackgroundDrawable(toolbarViewBgColor);
		if (null == toolBarTouchListener) {
			toolBarTouchListener = new ToolBarTouchListener();
		}
		toolbarView.setOnTouchListener(toolBarTouchListener);
		if (null == toolBarClickListener) {
			toolBarClickListener = new ToolBarClickListener();
		}
		
		toolbarView.setOnClickListener(toolBarClickListener);
	}
	
	/**
	 * 取消灰显和半隐藏等一系列动作
	 */
	private void cancleActions() {
		if (null != countDownTimer) {
			countDownTimer.cancel();//取消倒计时灰显
			countDownTimer = null;
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
	public void hide(){
		cancleActions();
		if (toolbarView!=null)
		{
			try{
				//mWindowManager.removeViewImmediate(toolbarView);
				if (toolbarView!=null){
					toolbarView.setVisibility(View.GONE);
				}
				
				isShowing=false;
			}catch(Exception e){
			}
		}
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
			x = getScreenHeight(context)-toolbarView.getMeasuredWidth();
			isleft = false;
		}
		wmParams.x = x;
		mWindowManager.updateViewLayout(toolbarView, wmParams);
		isHalf = false;
		startTimeCoutDown(2000);
	}
	private void startTimeCoutDown(long totalTime) {
		countDownTimer=	new CountDownTimer(totalTime,1000) {
			
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				countDownTimer = null;
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
	public class ToolBarClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (!isMoving) {
				Toast.makeText(context, "飞流球被点击了", Toast.LENGTH_SHORT).show();
				if (null == toolBarMenuPopWindow) {
					isMenuShow = true;
					toolbarView.setVisibility(View.GONE);
					toolBarMenuPopWindow = new toolBarMenuPopWindow(context,wmParams.x,wmParams.y,isleft, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							toolBarMenuPopWindow = null;
							isMenuShow = false;
							//showAfterMenuHide();
							toolbarView.setVisibility(View.VISIBLE);
							reSetLocation();
							if (null == toolBarTouchListener) {
								toolBarTouchListener = new ToolBarTouchListener();
							}
							toolbarView.setOnTouchListener(toolBarTouchListener);
							if (null == toolBarClickListener) {
								toolBarClickListener = new ToolBarClickListener();
							}
							toolbarView.setOnClickListener(toolBarClickListener);
						}
					});
					//hide();
					/*toolbarView.setOnTouchListener(null);
					toolbarView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "飞流球菜单里被点击了", Toast.LENGTH_SHORT).show();
						}
					});*/
					
					
				}
				
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
					toolbarView.setBackgroundDrawable(toolbarViewBgColor);
					
					wmParams.x = (int) event.getRawX() -  toolbarView.getMeasuredWidth()/2;
					wmParams.y = (int) event.getRawY() -  toolbarView.getMeasuredHeight()/2;
				    mWindowManager.updateViewLayout(toolbarView, wmParams);
				}
				
			}
			return false;  //此处必须返回false，否则OnClickListener获取不到监听
		}
	}
}
