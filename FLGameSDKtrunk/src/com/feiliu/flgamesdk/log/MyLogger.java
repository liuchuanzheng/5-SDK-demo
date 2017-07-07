package com.feiliu.flgamesdk.log;

import java.util.Hashtable;

import com.feiliu.flgamesdk.global.SDKConfigure;
import android.util.Log;

/**
 * The class for print log
 * 可以显示行号,类名,方法名.提供双重过滤,第一层是应用名,第二层是开发者名.
 * 应用名和开发者名需要提前配置好.
 * @author 刘传政
 *
 */
public class MyLogger
{
	public static boolean				logFlag			= SDKConfigure.ISDEBUG; // 是否打开LOG
	
	public final static String					tag				= "[飞流SDK]";
	private final static int					logLevel		= Log.VERBOSE;
	private static Hashtable<String, MyLogger>	sLoggerTable	= new Hashtable<String, MyLogger>();
	private String								mClassName;
	
	private static MyLogger						jlog;
	private static MyLogger						klog;
	private static MyLogger                     lczlog;
	private static MyLogger                     lmflog;
	private static MyLogger                     tylog;
	private static MyLogger                     lylog;
	
	private static final String					JAMES			= "@james@ ";
	private static final String					KESEN			= "@kesen@ ";
	private static final String					LIUCHUANZHENG	= "@liuchuanzheng@ ";//开发者刘传政
	private static final String					LIUMINGFEI	= "@liumingfei@ ";//开发者刘明飞
	private static final String					TIANYU	= "@tianyu@ ";//开发者田宇
	private static final String					LIYAN	= "@liyan@ ";//开发者李炎
	
	private MyLogger(String name)
	{
		mClassName = name;
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unused")
	private static MyLogger getLogger(String className)
	{
		MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
		if(classLogger == null)
		{
			classLogger = new MyLogger(className);
			sLoggerTable.put(className, classLogger);
		}
		return classLogger;
	}
	
	/**
	 * Purpose:Mark user one
	 * @return
	 */
	public static MyLogger kLog()
	{
		if(klog == null)
		{
			klog = new MyLogger(KESEN);
		}
		return klog;
	}
	/**
	 * Purpose:Mark user two
	 * @return
	 */
	public static MyLogger jLog()
	{
		if(jlog == null)
		{
			jlog = new MyLogger(JAMES);
		}
		return jlog;
	}
	/**
	 * Purpose:Mark user three
	 * @return
	 */
	public static MyLogger lczLog()
	{
		if(lczlog == null)
		{
			lczlog = new MyLogger(LIUCHUANZHENG);
		}
		return lczlog;
	}
	/**
	 * Purpose:Mark user three
	 * 刘明飞
	 * @return
	 */
	public static MyLogger lmfLog()
	{
		if(lmflog == null)
		{
			lmflog = new MyLogger(LIUMINGFEI);
		}
		return lmflog;
	}
	/**
	 * Purpose:Mark user three
	 * 田宇
	 * @return
	 */
	public static MyLogger tyLog()
	{
		if(tylog == null)
		{
			tylog = new MyLogger(TIANYU);
		}
		return tylog;
	}
	/**
	 * Purpose:Mark user three
	 * 李炎
	 * @return
	 */
	public static MyLogger lyLog()
	{
		if(lylog == null)
		{
			lylog = new MyLogger(LIYAN);
		}
		return lylog;
	}
	
	/**
	 * Get The Current Function Name
	 * @return
	 */
	private String getFunctionName()
	{
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if(sts == null)
		{
			return null;
		}
		for(StackTraceElement st : sts)
		{
			if(st.isNativeMethod())
			{
				continue;
			}
			if(st.getClassName().equals(Thread.class.getName()))
			{
				continue;
			}
			if(st.getClassName().equals(this.getClass().getName()))
			{
				continue;
			}
			return mClassName + "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}
		return null;
	}
	
	/**
	 * The Log Level:i
	 * @param str
	 */
	public void i(Object str)
	{
		if(logFlag)
		{
			if(logLevel <= Log.INFO)
			{
				String name = getFunctionName();
				if(name != null)
				{
					Log.i(tag, name + " - " + str);
				}
				else
				{
					Log.i(tag, str.toString());
				}
			}
		}
		
	}
	
	/**
	 * The Log Level:d
	 * @param str
	 */
	public void d(Object str)
	{
		if(logFlag)
		{
			if(logLevel <= Log.DEBUG)
			{
				String name = getFunctionName();
				if(name != null)
				{
					Log.d(tag, name + " - " + str);
				}
				else
				{
					Log.d(tag, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:V
	 * @param str
	 */
	public void v(Object str)
	{
		if(logFlag)
		{
			if(logLevel <= Log.VERBOSE)
			{
				String name = getFunctionName();
				if(name != null)
				{
					Log.v(tag, name + " - " + str);
				}
				else
				{
					Log.v(tag, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:w
	 * @param str
	 */
	public void w(Object str)
	{
		if(logFlag)
		{
			if(logLevel <= Log.WARN)
			{
				String name = getFunctionName();
				if(name != null)
				{
					Log.w(tag, name + " - " + str);
				}
				else
				{
					Log.w(tag, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:e
	 * @param str
	 */
	public void e(Object str)
	{
		if(logFlag)
		{
			if(logLevel <= Log.ERROR)
			{
				String name = getFunctionName();
				if(name != null)
				{
					Log.e(tag, name + " - " + str);
				}
				else
				{
					Log.e(tag, str.toString());
				}
			}
		}
	}
	
	/**
	 * The Log Level:e
	 * @param ex
	 */
	public void e(Exception ex)
	{
		if(logFlag)
		{
			if(logLevel <= Log.ERROR)
			{
				Log.e(tag, "error", ex);
			}
		}
	}
	
	/**
	 * The Log Level:e
	 * @param log
	 * @param tr
	 */
	public void e(String log, Throwable tr)
	{
		if(logFlag)
		{
			String line = getFunctionName();
			Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
					+ "[" + mClassName + line + ":] " + log + "\n", tr);
		}
	}
}

