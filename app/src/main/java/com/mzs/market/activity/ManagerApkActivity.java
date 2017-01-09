
package com.mzs.market.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import com.mzs.market.R;
import com.mzs.market.adapter.ManagerAppAdapter;
import com.mzs.market.bean.AppInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.utils.FileUtils;
import com.mzs.market.utils.PackageUtils;
import com.mzs.market.utils.PackageUtils.InstallListener;
import com.mzs.market.utils.UIUtils;
import com.mzs.market.widget.slidelistview.ActionSlideExpandableListView;
import com.mzs.market.widget.slidelistview.ActionSlideExpandableListView.OnActionClickListener;
import java.io.File;
import java.util.ArrayList;

/**
 * @category 安装包管理
 * @author wanghao
 * @date 2015.1.9
 */
public class ManagerApkActivity extends BaseActivity implements OnClickListener,
        OnActionClickListener {

    private ActionSlideExpandableListView lvAppManagerList;

    private Button btnBack;

    private ViewStub vsLoading;

    private ArrayList<AppInfo> infos = new ArrayList<AppInfo>();

    private ManagerAppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_apk);
        btnBack = (Button) findViewById(R.id.ibtn_back);
        lvAppManagerList = (ActionSlideExpandableListView) findViewById(R.id.lv_apk_manager_list);
        vsLoading = (ViewStub) findViewById(R.id.vs_loading);
        btnBack.setText("安装包管理");
        btnBack.setOnClickListener(this);
        adapter = new ManagerAppAdapter(context, infos);
        lvAppManagerList.setAdapter(adapter);
        getApkFromSD();
        lvAppManagerList.enableExpandOnItemClick();
        lvAppManagerList.setItemActionListener(this, new int[] {
                R.id.rl_install_apk, R.id.rl_delete_apk
        });
    }

    private void getApkFromSD() {
        final Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                vsLoading.setVisibility(View.GONE);
                AppInfo info = (AppInfo) msg.obj;
                infos.add(info);
                adapter.notifyDataSetChanged();
            }
        };
        vsLoading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // String[] files =
                // SearchApk(Environment.getExternalStorageDirectory().getPath(),
                // 1,
                // ".apk");
                ArrayList<String> files = FileUtils.searchFile(Environment
                        .getExternalStorageDirectory().getPath(), ".apk");
                // 根据路径获取apk所有信息
                for (String s : files) {
                    AppInfo info = PackageUtils.getUninstallApkInfo(s);
                    Message msg = handler.obtainMessage();
                    msg.obj = info;
                    msg.sendToTarget();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View itemView, final View clickedView, final int position) {
        final AppInfo info = infos.get(position);
        final TextView tvInstall = (TextView) clickedView.findViewById(R.id.tv_apk_install);
        switch (clickedView.getId()) {
            case R.id.rl_install_apk:
                info.status = MzsConstant.UninstallStatus.STATUS_INSTALLING;
                tvInstall.setText("安装中");
                clickedView.setEnabled(false);
                PackageUtils.installApkSync(info.appPath, new InstallListener() {
                    @Override
                    public void installSuccess(String path) {
                        infos.remove(position);
                        adapter.notifyDataSetChanged();
                        UIUtils.showShortToast("安装成功");
                        lvAppManagerList.collapse();
                    }

                    @Override
                    public void installFailed(String path) {
                        info.status = MzsConstant.UninstallStatus.STATUS_DEFAULT;
                        tvInstall.setText("安装");
                        clickedView.setEnabled(true);
                        UIUtils.showShortToast("安装失败");
                    }
                });
                break;
            case R.id.rl_delete_apk:
                infos.remove(position);
                adapter.notifyDataSetChanged();
                new File(info.appPath).delete();
                lvAppManagerList.collapse();
                break;
        }
    }

    static {
        System.loadLibrary("searchapk");
    }

    public native String[] SearchApk(String path, int recursive, String suffix);

}
