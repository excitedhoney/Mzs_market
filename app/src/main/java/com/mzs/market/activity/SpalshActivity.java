package com.mzs.market.activity;

import com.mzs.market.R;
import com.mzs.market.config.AppConfig;
import com.mzs.market.utils.UIUtils;
import android.os.Bundle;

public class SpalshActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spalsh);
		AppConfig.uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				UIUtils.openActivity(SpalshActivity.this,MainActivity.class);
				finish();
			}
		},2000);
	}
}
