
package com.mzs.market.activity;

import com.mzs.market.R;
import com.mzs.market.adapter.MainTabAdapter;
import com.mzs.market.dialog.CommonDialog;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.download.service.DownloadService;
import com.mzs.market.fragment.CategoryFragment;
import com.mzs.market.fragment.MoreFragment;
import com.mzs.market.fragment.RankFragment;
import com.mzs.market.fragment.RecommendFragment;
import com.mzs.market.utils.UIUtils;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>(4);

    private RadioGroup rgs;

    private DownloadObserverAdapter observerAdapter = DownloadObserverAdapter.getInstance();

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindDownloadService();
        initFragments();
        initView();
    }

    private void initView() {
        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        MainTabAdapter adapter = new MainTabAdapter(this, fragments, R.id.realtabcontent, rgs);
        adapter.setup();
    }

    private void bindDownloadService() {
        msApp.startService(new Intent(context, DownloadService.class));
        msApp.bindService(new Intent(context, DownloadService.class), connection,
                Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            observerAdapter.setDownloadService(null);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            observerAdapter
                    .setDownloadService(((DownloadService.ServiceStub) service).getService());
        }
    };

    private void initFragments() {
        fragments.add(new RecommendFragment());
        fragments.add(new CategoryFragment());
        fragments.add(new RankFragment());
        fragments.add(new MoreFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msApp.unbindService(connection);
        msApp.stopService(new Intent(context, DownloadService.class));
    }

    @Override
    public void onBackPressed() {
        int size = observerAdapter.getDownloadService().getDownloadingSize();
        if (size != 0) {  //有正在下载任务
            new CommonDialog(context).setTitle("确认退出").setMsg(size+"个任务正在下载，是否退出？")
            .setLeftBtn("取消",new CommonDialog.BtnClickListener() {
                @Override
                public void onBtnClick(Dialog dialog, int id) {
                    dialog.dismiss();
                }
            }).setRightBtn("退出", new CommonDialog.BtnClickListener() {
                @Override
                public void onBtnClick(Dialog dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
                UIUtils.showShortToast("再按一次退出程序");
            } else {
                finish();
            }
        }
    }
}
