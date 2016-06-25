package com.coolweather.app.util;

import android.util.Log;

public class LogUtil {

	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	private static final int NOTHING = 6;
	private static final int LEVEL = VERBOSE;  //需要打印日志时
//	private static final int LEVEL = NOTHING;  //不需要打印日志时
	
	public static void verbose(String tag,String msg){
		if(LEVEL<=VERBOSE){
			Log.v(tag, msg);
		}
	}
	public static void debug(String tag,String msg){
		if(LEVEL<=DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void info(String tag,String msg){
		if(LEVEL<=INFO){
			Log.i(tag, msg);
		}
	}
	public static void warn(String tag,String msg){
		if(LEVEL<=WARN){
			Log.w(tag, msg);
		}
	}
	public static void error(String tag,String msg){
		if(LEVEL<=ERROR){
			Log.e(tag, msg);
		}
	}
}





