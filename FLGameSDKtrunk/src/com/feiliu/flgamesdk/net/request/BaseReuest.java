package com.feiliu.flgamesdk.net.request;

import android.content.Context;

import com.feiliu.flgamesdk.listener.FLOnLoginListener;
import com.feiliu.flgamesdk.listener.FLOnNetFinishListener;
import com.feiliu.flgamesdk.view.popupwindow.base.BasePopWindow;

public abstract class BaseReuest {
	public Context context;
	public FLOnLoginListener loginListener;
	public BasePopWindow basePopWindow;
	public String popFrom;//记录打开此请求的来源pop，便于请求成功后做不同的处理
	public FLOnNetFinishListener fLOnNetFinishListener;//联网完成
	public abstract void sendHandlerMessage(int mWhat,Object obj);
	public abstract void post();
}
