package com.mzs.market.bean;

import android.graphics.drawable.Drawable;

import com.mzs.market.config.MzsConstant;

public class AppInfo {
	public String appName;
	public String appVersion;
	public Drawable drawable;
	public Boolean isUserApp;
	public String packageName;
	public String appSpace="- -";
	public String appPath;
	public int status=MzsConstant.UninstallStatus.STATUS_DEFAULT;
}
