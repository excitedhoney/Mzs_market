package com.mzs.market.config;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

public class AppConfig {

	public static int width;
	public static int height;
	public static float density;
	public static final Handler uiHandler=new Handler(Looper.getMainLooper());
	
	public static void initScreenSize(Context context){
		final DisplayMetrics dm=context.getResources().getDisplayMetrics();
		width=dm.widthPixels;
		height=dm.heightPixels;
		density=dm.density;
	}
	
}
