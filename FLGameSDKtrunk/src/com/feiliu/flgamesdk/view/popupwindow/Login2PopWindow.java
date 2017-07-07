package com.feiliu.flgamesdk.view.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.feiliu.flgamesdk.db.AccountDBBean;
import com.feiliu.flgamesdk.global.PictureNameConstant;
import com.feiliu.flgamesdk.global.SDKConfigure;
import com.feiliu.flgamesdk.listener.BaseBarListener;
import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.log.MyLogger;
import com.feiliu.flgamesdk.net.request.NormalLoginRequest;
import com.feiliu.flgamesdk.utils.GetSDKPictureUtils;
import com.feiliu.flgamesdk.utils.ToastUtil;
import com.feiliu.flgamesdk.utils.UiSizeUtil;
import com.feiliu.flgamesdk.view.FlEditText;
import com.feiliu.flgamesdk.view.activity.FLSdkActivity;
import com.feiliu.flgamesdk.view.popupwindow.base.BaseBarView;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

/**
 * 登录二级界面
 * @author 刘传政
 *
 */
public class Login2PopWindow extends BasePopWindow implements  OnDismissListener, BaseBarListener {
	private FLOnLoginListener loginListener;
	private EditText accountEt;//账号输入框
	private EditText passwordEt;//密码输入框
	private Boolean isRead = false;//记录密码是否是直接从本地读取的。true代表本地读取（如果登录失败会弹出安全提示）。false代表手动输入（如果登录错误只简单提示）
	private List<AccountDBBean> accountDBBeans;//从不需要密码输入框传过来的数据库查询出来的账号集合。已经截取了前5个
	private int position = 0;//账号历史显示条目序号。默认显示0
	private OnDismissListener lastPopOnDismissListener;//上一个弹出框传过来的。为了配合返回键的功能
	private RelativeLayout accountHistoryRl;
	private ListView accountHistoryLv;//账号历史的listview
	private AccountHistoryAdapter accountHistoryAdapter;
	private TextView accountHistoryShowView;//账号历史显示按钮
	private Drawable arrowDown;//向下箭头
	private Drawable arrowUp;//向上箭头
	private String fromPop;//记录是上一个pop
	
	public Login2PopWindow(Context context, List<AccountDBBean> accountDBBeans,
			FLOnLoginListener loginListener, OnDismissListener onDismissListener,String fromPop) {
		this.context = context;
		this.fromPop = fromPop;
		accountDBBeans = cutAccount(accountDBBeans);
		this.accountDBBeans = accountDBBeans;
		scale = UiSizeUtil.getScale(this.context);
		this.loginListener = loginListener;
		lastPopOnDismissListener = onDismissListener;
		currentWindowView = createMyUi();
		showWindow();
	}

	/**
	 * 如何集合长度大于5，截取到5.如果不大于5，保留原集合
	 * @param accountDBBeans2
	 * @return
	 */
	public List<AccountDBBean> cutAccount(List<AccountDBBean> accountDBBeans2) {
		 List<AccountDBBean> list = new ArrayList<AccountDBBean>();
		 if (accountDBBeans2 != null) {
			 if (accountDBBeans2.size() > 5) {
					for(int i = 0;i<5;i++){
						list.add(accountDBBeans2.get(i));
					}
				}else {
					list = accountDBBeans2;
				}
		}else {
			list = null;
		}
		
		return list;
	}

	public View createMyUi() {
		final RelativeLayout retAbsoluteLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams retlayoutParams = new RelativeLayout.LayoutParams(
				(int) (designWidth * scale), (int) (designHeight * scale));
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		Drawable retAbsoluteLayoutBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		retAbsoluteLayout.setBackgroundDrawable(retAbsoluteLayoutBg);
		//..........................................title............................................................
		boolean haveBackView = true;
		boolean havaCloseView = false;
		if ("LoginFailPopWindow".equals(fromPop)) {
			haveBackView = false;
		}
		if (null == lastPopOnDismissListener) {
			haveBackView = false;
		}
		if (null == lastPopOnDismissListener && SDKConfigure.haveCloseButton) {
			haveBackView = false;
			havaCloseView = true;
		}
		
		BaseBarView baseBarView = new BaseBarView(context, "用户登录", haveBackView,havaCloseView, this);
		RelativeLayout.LayoutParams baseBarViewParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(100*scale));
		baseBarViewParams.topMargin=(int) (0*scale);
		baseBarView.setLayoutParams(baseBarViewParams);
		//............................................账号输入框总背景.........................................................
		RelativeLayout accountRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams accountRlParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		accountRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		accountRlParams.topMargin=(int) (185*scale);
		accountRl.setLayoutParams(accountRlParams);
		Drawable accountRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		accountRl.setBackgroundDrawable(accountRlBg);
		//..........................................账号输入框..............................................................
		accountEt = new FlEditText(context);
		RelativeLayout.LayoutParams accountEtParams=new RelativeLayout.LayoutParams((int)(620*scale),(int)(100*scale));
		//accountEtParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		//accountEtParams.topMargin=(int) (180*scale);
		accountEt.setLayoutParams(accountEtParams);
		Drawable accountEtBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTAREA);//方形边界框
		//accountEt.setBackgroundDrawable(accountEtBg);
		accountEt.setBackgroundDrawable(null);
		accountEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		accountEt.setHint("手机号或邮箱");
		accountEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		accountEt.setSingleLine(true);  
		if (accountDBBeans != null) {
			accountEt.setText(accountDBBeans.get(position).getUserName());
		}
		accountEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//让账号改变的时候就隐藏列表
				if (accountDBBeans == null) {
					return;
				}
				if (accountDBBeans != null) {
					if (accountDBBeans.size() <= 1) {
						isRead = false;//记录密码来源
						return;
					}
				}
				accountHistoryRl.setVisibility(View.GONE);
				accountHistoryShowView.setBackgroundDrawable(arrowDown);
				isRead = false;//记录密码来源
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		accountEt.setOnEditorActionListener(new OnEditorActionListener() { 
            
           

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				 if (actionId == EditorInfo.IME_ACTION_NEXT) { 
					
					//让账号改变的时候就隐藏列表
						if (accountDBBeans == null) {
							return false;
						}
						if (accountDBBeans != null) {
							if (accountDBBeans.size() <= 1) {
								return false;
							}
						}
						accountHistoryRl.setVisibility(View.GONE);
						accountHistoryShowView.setBackgroundDrawable(arrowDown);
	             } 
				return false;
			} 
        }); 
		//............................................账号历史显示按钮..............................................................
		accountHistoryShowView = new TextView(context);
		RelativeLayout.LayoutParams accountHistoryShowViewParams=new RelativeLayout.LayoutParams((int)(80*scale),(int)(70*scale));
		accountHistoryShowViewParams.leftMargin = (int) (700*scale);
		accountHistoryShowViewParams.topMargin=(int) (200*scale);
		accountHistoryShowView.setLayoutParams(accountHistoryShowViewParams);
		arrowDown = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWDOWN);
		arrowUp = GetSDKPictureUtils.getDrawable(context, PictureNameConstant.ARROWUP);
		accountHistoryShowView.setBackgroundDrawable(arrowDown);
		if (accountDBBeans == null) {
			accountHistoryShowView.setVisibility(View.GONE);
		}
		if (accountDBBeans != null) {
			if (accountDBBeans.size() <= 1) {
				accountHistoryShowView.setVisibility(View.GONE);
			}
		}
		
		accountHistoryShowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//  显示账号历史
				showAndHideAccountList();
			}
		});
		
		//...........................................历史账号列表....................................................................
		//此view默认不显示。只有点击后才显示
		accountHistoryRl = new RelativeLayout(context);
		RelativeLayout.LayoutParams accountHistoryRlParams = new RelativeLayout.LayoutParams(
				 LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		accountHistoryRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		accountHistoryRlParams.topMargin=(int) (285*scale);
		accountHistoryRl.setLayoutParams(accountHistoryRlParams);
		Drawable accountHistoryRlBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
		accountHistoryRl.setBackgroundDrawable(accountHistoryRlBg);
		accountHistoryRl.setVisibility(View.GONE);
				//......................................listview.................................................
		accountHistoryLv = new ListView(context);
		if (accountDBBeans != null) {
			if (accountHistoryAdapter == null) {
				accountHistoryAdapter = new AccountHistoryAdapter();
			}
			
			RelativeLayout.LayoutParams accountHistoryLvParams = new RelativeLayout.LayoutParams(
					(int)(700*scale), LayoutParams.WRAP_CONTENT);
			accountHistoryLv.setLayoutParams(accountHistoryLvParams);
			accountHistoryLv.setAdapter(accountHistoryAdapter);
			accountHistoryLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					showAndHideAccountList();
					changeShowAccount(arg2);
					isRead = true;
				}
			});
		}
		
		
		
		
		//.................................................密码输入框.................................................
		passwordEt = new FlEditText(context);
		RelativeLayout.LayoutParams passwordEtParams=new RelativeLayout.LayoutParams((int)(700*scale),(int)(100*scale));
		passwordEtParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE); //居中 
		passwordEtParams.topMargin=(int) (320*scale);
		passwordEt.setLayoutParams(passwordEtParams);
		passwordEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		passwordEt.setHint("密码");
		passwordEt.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
		passwordEt.setSingleLine(true);  
		passwordEt.setBackgroundDrawable(accountEtBg);//公用账号输入框的边框
		passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		if (accountDBBeans != null) {
			isRead = true;//记录密码来源
			passwordEt.setText(accountDBBeans.get(position).getPassword());
		}
		//监听文本输入状态，判断是手动输入密码还是数据库读取的密码。当登录失败时，会根据不同的密码来源弹出不同的错误提示。
		passwordEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				isRead = false;//记录密码来源
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		//............................................用户注册.................................................................
		TextView registerView = new TextView(context);//用户注册按钮
		RelativeLayout.LayoutParams registerViewParams=new RelativeLayout.LayoutParams((int)(325*scale),(int)(110*scale));
		registerViewParams.leftMargin = (int) (90*scale);
		registerViewParams.topMargin=(int) (450*scale);
		registerView.setLayoutParams(registerViewParams);
		Drawable registerBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.BLUESHORTBG);//用户注册按钮图片
		registerView.setBackgroundDrawable(registerBg);
		registerView.setText("用户注册");
		registerView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		registerView.setTextColor(Color.WHITE);
		registerView.setGravity(Gravity.CENTER);//文字居中
		registerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//   注册点击事件
				mPopupWindow.dismiss();
				new RegisterPopWindow(context, "", "", loginListener,new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						showWindow();//下一个pop关闭了。自己显示出来
					}
				});
				
			}
		});
		//..............................................登录...................................................................
		TextView loginView = new TextView(context);//登录按钮
		RelativeLayout.LayoutParams loginViewParams=new RelativeLayout.LayoutParams((int)(325*scale),(int)(110*scale));
		loginViewParams.leftMargin = (int) (460*scale);
		loginViewParams.topMargin=(int) (450*scale);
		loginView.setLayoutParams(loginViewParams);
		Drawable loginBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.BLUESHORTBG);
		loginView.setBackgroundDrawable(loginBg);
		loginView.setText("登录");
		loginView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		loginView.setTextColor(Color.WHITE);
		loginView.setGravity(Gravity.CENTER);//文字居中
		loginView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(accountEt.getText().toString())) {
					ToastUtil.showShort(context, "用户名不能为空");
					return;
				}
				if (TextUtils.isEmpty(passwordEt.getText().toString())) {
					ToastUtil.showShort(context, "密码不能为空");
					return;
				}
				//   检查账号和密码的正确性
				MyLogger.lczLog().i("isRead:"+isRead);
				NormalLoginRequest normalLoginRequest = new NormalLoginRequest(context, loginListener
						,isRead, accountEt.getText().toString(),passwordEt.getText().toString(),Login2PopWindow.this);
				normalLoginRequest.post();
			}
		});
		
		//.............................................隐私条款........................................................
		TextView clauseView = new TextView(context);
		RelativeLayout.LayoutParams clauseViewParams=new RelativeLayout.LayoutParams((int)(325*scale),(int)(100*scale));
		clauseViewParams.leftMargin = (int) (90*scale);
		clauseViewParams.topMargin=(int) (585*scale);
		clauseView.setLayoutParams(clauseViewParams);
		clauseView.setText("隐私条款");
		clauseView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		clauseView.setTextColor(Color.GRAY);
		clauseView.setGravity(Gravity.CENTER);//文字居中
		clauseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开隐私条款activity，加载网页
				showClauseActivity();
			}
		});
		//...............................................竖线...........................................................
		TextView textWall = new TextView(context);
		RelativeLayout.LayoutParams textWallParams=new RelativeLayout.LayoutParams((int)(5*scale),(int)(40*scale));
		textWallParams.leftMargin = (int) (440*scale);
		textWallParams.topMargin=(int) (615*scale);
		textWall.setLayoutParams(textWallParams);
		Drawable textWallBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.TEXTWALL);
		textWall.setBackgroundDrawable(textWallBg);
		//.............................................忘记密码........................................................
		TextView forgetView = new TextView(context);
		RelativeLayout.LayoutParams forgetViewParams=new RelativeLayout.LayoutParams((int)(325*scale),(int)(100*scale));
		forgetViewParams.leftMargin = (int) (460*scale);
		forgetViewParams.topMargin=(int) (585*scale);
		forgetView.setLayoutParams(forgetViewParams);
		forgetView.setText("忘记密码?");
		forgetView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		forgetView.setTextColor(Color.GRAY);
		forgetView.setGravity(Gravity.CENTER);//文字居中
		forgetView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//  忘记密码点击事件
				mPopupWindow.dismiss();
				new FindPasswordPopWindow(context, "", "", loginListener, new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						showWindow();//下一个pop关闭了。自己显示出来
					}
				});
			}
		});
		
		retAbsoluteLayout.addView(baseBarView);
		accountRl.addView(accountEt);
		retAbsoluteLayout.addView(accountRl);
		retAbsoluteLayout.addView(passwordEt);
		retAbsoluteLayout.addView(registerView);
		retAbsoluteLayout.addView(loginView);
		retAbsoluteLayout.addView(clauseView);
		retAbsoluteLayout.addView(textWall);
		retAbsoluteLayout.addView(forgetView);
		accountHistoryRl.addView(accountHistoryLv);
		retAbsoluteLayout.addView(accountHistoryRl);//必须最后添加，才能盖住其他view。防止点击不到或显示混乱
		retAbsoluteLayout.addView(accountHistoryShowView);//有两个以上历史时才显示
		//添加全屏透明布局
		RelativeLayout rootView = creatRootTranslateView();
		retlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE); //居中 
		retAbsoluteLayout.setLayoutParams(retlayoutParams);
		rootView.addView(retAbsoluteLayout);
		return rootView;
	}
	
	/**
	 * 根据listview点击改变账号和密码显示框的内容
	 * @param position
	 */
	public void changeShowAccount(int position) {
		accountEt.setText(accountDBBeans.get(position).getUserName());
		passwordEt.setText(accountDBBeans.get(position).getPassword());
	}

	/**
	 * 显示或隐藏账号历史view
	 */
	public  void showAndHideAccountList() { 
		if (accountHistoryRl.getVisibility() == View.GONE) {
			accountHistoryRl.setVisibility(View.VISIBLE);
			accountHistoryShowView.setBackgroundDrawable(arrowUp);
		}else if (accountHistoryRl.getVisibility() == View.VISIBLE) {
			accountHistoryRl.setVisibility(View.GONE);
			accountHistoryShowView.setBackgroundDrawable(arrowDown);
		}
	}

	/**
	 * 展示隐私条款界面
	 */
	protected void showClauseActivity() {
		Intent intent = new Intent(context,FLSdkActivity.class);
		Bundle bundle = new Bundle();
	    intent.putExtra("fromtype",0);//表明是隐私条款意图
	    intent.putExtras(bundle);
	    ((Activity)context).startActivity(intent);
	}

	

	public void showWindow() {
		MyLogger.lczLog().i("展示：Login2PopWindow");
		new Runnable() {
			@Override
			public void run() {
				TextView tv;
				tv = new TextView(context);
				createPopWindow(tv);
			}
		}.run();
	}
	/**
	 * 创建出popupwindow对象，并设置相应参数
	 * @param parent
	 */
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
		//mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);//pop弹出动画
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);
		mPopupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0,
				0);
	}
	/**
	 * 账号历史的适配器
	 * @author Administrator
	 *
	 */
	public class AccountHistoryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return accountDBBeans.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(context);
			//这里只能是ListView.LayoutParams。否则部分机型会报错
			ListView.LayoutParams tvParams=new ListView.LayoutParams((int)(700*scale),(int)(80*scale));
			tv.setLayoutParams(tvParams);
			/*Drawable tvBg =GetSDKPictureUtils.getDrawable(context, PictureNameConstant.WINDOWBG);//得到背景图片
			tv.setBackgroundDrawable(tvBg);*/
			tv.setBackgroundColor(0xFFF7F7F7);
			tv.setGravity(Gravity.CENTER_VERTICAL);//文字居中
			tv.setPadding((int)(35*scale), 0, 0, 0);
			tv.setText(accountDBBeans.get(position).getUserName());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
			tv.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
			tv.setSingleLine(true);  
			return tv;
		}
		
	}

	@Override
	public void onDismiss() {
		
	}

	@Override
	public void backbuttonclick() {
		mPopupWindow.dismiss();
		if (null != lastPopOnDismissListener) {
			lastPopOnDismissListener.onDismiss();//通知上一个pop，此弹出框关闭了
		}
	}

	@Override
	public void closewindow() {
		mPopupWindow.dismiss();
		loginListener.onLoginCancel();//通知cp，关闭了窗口。表示取消登录
	}


	
}
