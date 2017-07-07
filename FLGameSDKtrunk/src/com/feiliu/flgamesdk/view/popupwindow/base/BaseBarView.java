package com.feiliu.flgamesdk.view.popupwindow.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.UiSizeUtil;

/**
 * @author 刘传政
 * <br/>各个窗口的bar 可以设置标题文字和是否显示关闭按钮。
 */
public class BaseBarView extends RelativeLayout{
	private String title;
	private Boolean haveCloseView;
	private Boolean haveBackView;
	private Context context;
	public float scale;//尺寸百分比
	private BaseBarListener baseBarListener;
	/**
	 * @param context
	 * @param title 标题文字
	 * @param haveBackView 是否需要返回按钮
	 * @param haveCloseView 是否需要关闭按钮
	 * @param baseBarListener 返回按钮和关闭按钮的监听
	 */
	public BaseBarView(Context context,String title,Boolean haveBackView,Boolean haveCloseView,BaseBarListener baseBarListener) {
		super(context);
		this.title = title;
		this.context = context;
		this.haveBackView = haveBackView;
		this.haveCloseView = haveCloseView;
		this.baseBarListener = baseBarListener;
		scale = UiSizeUtil.getScale(this.context);
		initBar();
		
	}

	private void initBar() {
		Drawable titleDw =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TITLEBG);
		this.setBackgroundDrawable(titleDw);
		//.................................标题..................................................
		TextView textView = new TextView(context);
		textView.setText(title);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		textView.setTextColor(Color.WHITE);
		RelativeLayout.LayoutParams barViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		barViewParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		textView.setLayoutParams(barViewParams);
		textView.setGravity(Gravity.CENTER);//文字居中
		if (haveBackView) {
			//.......................................返回按钮........................................................
			View backView = new View(context);
			RelativeLayout.LayoutParams backViewParams=new RelativeLayout.LayoutParams((int)(150*scale),(int)(100*scale));
			backViewParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE); //居中 
			backViewParams.leftMargin=(int)(0*scale);
			backViewParams.topMargin=(int) (0*scale);
			backView.setLayoutParams(backViewParams);
			final Drawable backViewDw =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.BACKVIEW);
			final Drawable backViewClickDw =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.BACKVIEWCLICK);
			backView.setBackgroundDrawable(backViewDw);
			backView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction()==MotionEvent.ACTION_DOWN){
						v.setBackgroundDrawable(backViewClickDw);
					}else if(event.getAction()==MotionEvent.ACTION_UP){
						v.setBackgroundDrawable(backViewDw);
					}
					return false;
				}
			});
			backView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//  返回按钮的点击事件
					MyLogger.lczLog().i("点击返回按钮");
					baseBarListener.backbuttonclick();
				}
			});
			this.addView(backView);
		}
		
		
		if (haveCloseView) {
			//添加关闭按钮
			View closeView = new View(context);//关闭按钮
			RelativeLayout.LayoutParams viewCloseParams=new RelativeLayout.LayoutParams((int)(80*scale),(int)(70*scale));
			viewCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE); //右对齐 
			//viewCloseParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
			viewCloseParams.topMargin=(int) (0*scale);
			viewCloseParams.rightMargin = (int) (10*scale);
			closeView.setLayoutParams(viewCloseParams);
			Drawable closeViewBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.CLOSEVIEW);//关闭按钮图片
			closeView.setBackgroundDrawable(closeViewBg);
			closeView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 关闭按钮的点击事件
					MyLogger.lczLog().i("点击关闭按钮");
					baseBarListener.closewindow();
				}
			});
			this.addView(closeView);
		}
		
		this.addView(textView);
	}

}
