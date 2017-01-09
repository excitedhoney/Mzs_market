package com.mzs.market;

import com.mzs.market.config.AppConfig;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.utils.ImageloaderHelper;
import android.app.Application;

public class MzsApplication extends Application {

	public static MzsApplication APP = null;

	@Override
	public void onCreate() {
		super.onCreate();
		APP=this;
		AppConfig.initScreenSize(APP);
		ImageloaderHelper.init(APP);
		MzsVolley.init(APP);
	}

}
