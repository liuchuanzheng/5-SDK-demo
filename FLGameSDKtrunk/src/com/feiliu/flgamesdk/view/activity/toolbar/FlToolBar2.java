package com.feiliu.flgamesdk.view.activity.toolbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.FLBallPlace;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.popupwindow.toolBarMenuPopWindow;

/**
 * @author 刘传政
 * 飞流球
 * 不使用
 */
public class FlToolBar2 {

	private int toolBarPlace;
	private Context context;
	public float scale;//尺寸百分比
	private RelativeLayout toolbarView=null;//飞流球的view
	private WindowManager mWindowManager=null;
	private WindowManager.LayoutParams wmParams=null;
	private boolean isShowing=false;//飞流球是否显示
	private CountDownTimer grayCountDownTimer=null;//计时器，用于彩显变灰显判断
	private CountDownTimer halfCountDownTimer=null;//计时器，用于灰显变半隐藏
	private Drawable toolbarViewBgGray;//灰显图片
	private Drawable toolbarViewBgColor;//彩显图片
	private Drawable toolbarViewBgGrayTranslate;//彩显图片
	private boolean isGray = false;//是否是灰显状态
	private boolean isHalf = false;//是否半隐藏
	private boolean isleft;//飞流球靠边时的左右位置
	private toolBarMenuPopWindow toolBarMenuPopWindow;//条形菜单
	private boolean isMenuShow = false;
	private int leftMargin = 0;
	private int rightMargin = 0;
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
	private RelativeLayout toolbarLeftInView;
	private RelativeLayout toolbarRightInView;
	private android.widget.RelativeLayout.LayoutParams toolbarInLeftViewParams;
	private android.widget.RelativeLayout.LayoutParams toolbarInRightViewParams;
	
	public FlToolBar2(Context context,int toolBarPlace) {
		this.context = context;
		this.toolBarPlace = toolBarPlace;
		scale = UiSizeUtil.getScale(this.context);
		iconWidth = (int) (150 * scale);
		iconHeight = (int) (150 * scale);
		minMoveLeng = (int) (5*scale);
		if (0 == toolBarPlace) {
			isleft = true;
		}else if (1 == toolBarPlace) {
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
	 * 由于才有弹出菜单再隐藏飞流的方式不如直接让飞流球覆盖菜单效果好
	 */
	private void showAfterMenuHide(){
		/*if (!isShowing) {
			isShowing = true;
			cancleActions();
			toolbarView = null;
			initView();
			mWindowManager.addView(toolbarView,wmParams);
			reSetLocation();
		}*/
		if (!isShowing) {
			isShowing = true;
			toolbarView.setVisibility(View.VISIBLE);
			if (!isHalf) {
				reSetLocation();
			}
			
		}
		
	}
	/**
	 * 设置windowmannager
	 */
	private void initWindowManager() {
		mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);			
		wmParams = new WindowManager.LayoutParams(iconWidth, iconWidth);
		//type跟优先级有关.之前为TYPE_TOAST的时候再魅族mx2和魅族mx3手机上飞流球无法获取焦点
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;//等于API19或API19以下需要指定窗口参数type值为TYPE_TOAST才可以作为悬浮控件显示出来
        } else {
        	wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;//API19以上侧只需指定为TYPE_PHONE即可
        }
        wmParams.format = PixelFormat.RGBA_8888;//当前窗口的像素格式为RGBA_8888,即为最高质量
        //NOT_FOCUSABLE可以是悬浮控件可以响应事件，LAYOUT_IN_SCREEN可以指定悬浮球指定在屏幕内，部分虚拟按键的手机，虚拟按键隐藏时，虚拟按键的位置则属于屏幕内，此时悬浮球会出现在原虚拟按键的位置
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		wmParams.width=iconWidth;
		wmParams.height=iconHeight;
		wmParams.packageName = context.getPackageName();
		wmParams.horizontalWeight = 0;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
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
		if (null == toolbarView ) {
			toolbarView = new RelativeLayout(context);
		}
		
		toolbarViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		toolbarView.setLayoutParams(toolbarViewParams);
		//...........................................左...........................................................
		toolbarLeftInView = new  RelativeLayout(context);
		toolbarInLeftViewParams = new RelativeLayout.LayoutParams(iconWidth, iconHeight);
		toolbarInLeftViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		toolbarInLeftViewParams.topMargin = (int) (0 * scale);
		toolbarInLeftViewParams.leftMargin = (int) (0 * scale);
		toolbarLeftInView.setLayoutParams(toolbarInLeftViewParams);
		toolbarLeftInView.setBackgroundDrawable(toolbarViewBgColor);
		if (isleft) {
			toolbarLeftInView.setVisibility(View.VISIBLE);
		}else {
			toolbarLeftInView.setVisibility(View.GONE);
		}
		//............................................右............................................................
		toolbarRightInView = new  RelativeLayout(context);
		toolbarInRightViewParams = new RelativeLayout.LayoutParams(iconWidth, iconHeight);
		toolbarInRightViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
		toolbarInRightViewParams.topMargin = (int) (0 * scale);
		toolbarInRightViewParams.rightMargin = (int) (0 * scale);
		toolbarRightInView.setLayoutParams(toolbarInRightViewParams);
		toolbarRightInView.setBackgroundDrawable(toolbarViewBgColor);
		if (isleft) {
			toolbarRightInView.setVisibility(View.GONE);
		}else {
			toolbarRightInView.setVisibility(View.VISIBLE);
		}
		
		toolbarView.addView(toolbarRightInView);
		toolbarView.addView(toolbarLeftInView);
		
		if (null == toolBarTouchListener) {
			toolBarTouchListener = new ToolBarTouchListener();
		}
		toolbarLeftInView.setOnTouchListener(toolBarTouchListener);
		toolbarRightInView.setOnTouchListener(toolBarTouchListener);
		if (null == toolBarClickListener) {
			toolBarClickListener = new ToolBarClickListener();
		}
		
		toolbarLeftInView.setOnClickListener(toolBarClickListener);
		toolbarRightInView.setOnClickListener(toolBarClickListener);
	}
	
	/**
	 * 取消灰显和半隐藏等一系列动作
	 */
	private void cancleActions() {
		if (null != grayCountDownTimer) {
			grayCountDownTimer.cancel();//取消倒计时灰显
			grayCountDownTimer = null;
		}
		if (null != halfCountDownTimer) {
			halfCountDownTimer.cancel();
			halfCountDownTimer = null;
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
			x = getScreenWidth(context)-toolbarView.getMeasuredWidth();
			isleft = false;
		}
		wmParams.x = x;
		if (isleft) {
			toolbarLeftInView.setVisibility(View.VISIBLE);
			toolbarRightInView.setVisibility(View.GONE);
		}else {
			toolbarLeftInView.setVisibility(View.GONE);
			toolbarRightInView.setVisibility(View.VISIBLE);
		}
		mWindowManager.updateViewLayout(toolbarView, wmParams);
		isHalf = false;
		startGrayTimeCoutDown(2000);
	}
	private void startGrayTimeCoutDown(long totalTime) {
		grayCountDownTimer=	new CountDownTimer(totalTime,1000) {
			
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				toolbarLeftInView.setBackgroundDrawable(toolbarViewBgGray);
				toolbarRightInView.setBackgroundDrawable(toolbarViewBgGray);
				isGray = true;
				if (isleft) {
					leftMargin = 0;
					halfCountDownTimer = new CountDownTimer(500,20) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							leftMargin = leftMargin - toolbarLeftInView.getMeasuredWidth()/2/25;
							toolbarInLeftViewParams.leftMargin = leftMargin;
							toolbarLeftInView.setLayoutParams(toolbarInLeftViewParams);
						}
						
						@Override
						public void onFinish() {
							toolbarLeftInView.setBackgroundDrawable(toolbarViewBgGrayTranslate);
							isHalf = true;
						}
					}.start();
					
					
				}else {
					rightMargin = 0;
					halfCountDownTimer = new CountDownTimer(500,20) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							rightMargin = rightMargin - toolbarRightInView.getMeasuredWidth()/2/25;
							toolbarInRightViewParams.rightMargin = rightMargin;
							toolbarRightInView.setLayoutParams(toolbarInRightViewParams);
						}
						
						@Override
						public void onFinish() {
							toolbarRightInView.setBackgroundDrawable(toolbarViewBgGrayTranslate);
							isHalf = true;
						}
					}.start();
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
					toolBarMenuPopWindow = new toolBarMenuPopWindow(context,wmParams.x,wmParams.y,isleft, new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							toolBarMenuPopWindow = null;
							isMenuShow = false;
							showAfterMenuHide();
							
						}
					});
					hide();
					
					
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
					toolbarInLeftViewParams.leftMargin = 0;
					toolbarLeftInView.setLayoutParams(toolbarInLeftViewParams);
					toolbarInRightViewParams.rightMargin = 0;
					toolbarRightInView.setLayoutParams(toolbarInRightViewParams);
					toolbarLeftInView.setBackgroundDrawable(toolbarViewBgColor);
					toolbarRightInView.setBackgroundDrawable(toolbarViewBgColor);
					wmParams.x = (int) event.getRawX() -  toolbarView.getMeasuredWidth()/2;
					wmParams.y = (int) event.getRawY() -  toolbarView.getMeasuredHeight()/2;
				    mWindowManager.updateViewLayout(toolbarView, wmParams);
				}
				
			}
			return false;  //此处必须返回false，否则OnClickListener获取不到监听
		}
	}
}
