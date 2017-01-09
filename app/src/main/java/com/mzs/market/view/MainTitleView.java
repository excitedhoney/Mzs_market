
package com.mzs.market.view;

import com.mzs.market.R;
import com.mzs.market.activity.DownListActivity;
import com.mzs.market.activity.SearchApkActivity;
import com.mzs.market.utils.UIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MainTitleView extends LinearLayout implements View.OnClickListener {

    public MainTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.main_title_view, this);
        findViewById(R.id.btn_download_manager).setOnClickListener(this);
        findViewById(R.id.rl_search).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_manager:
                UIUtils.openActivity(getContext(), DownListActivity.class);
                break;
            case R.id.rl_search:
                UIUtils.openActivity(getContext(), SearchApkActivity.class);
                break;
        }
    }
}
