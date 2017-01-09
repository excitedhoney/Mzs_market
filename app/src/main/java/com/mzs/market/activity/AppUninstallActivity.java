package com.mzs.market.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ListView;
import com.mzs.market.R;
import com.mzs.market.adapter.AppUninstallAdapter;
import com.mzs.market.bean.AppInfo;
import com.mzs.market.utils.PackageUtils;
import java.util.ArrayList;
import android.view.View;

/**
 * @category 应用卸载
 * @author wanghao
 * @date 20151.7 AM 11:05
 */
public class AppUninstallActivity extends BaseActivity implements View.OnClickListener{
    
    private ListView lvAppList;
    private Button btnBack;
    private AppUninstallAdapter uninstallAdapter;
    private ArrayList<AppInfo> appInfos=new ArrayList<AppInfo>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_uninstall);
        btnBack=(Button) findViewById(R.id.ibtn_back);
        lvAppList=(ListView) findViewById(R.id.lv_app_list);
        btnBack.setText("应用卸载");
        btnBack.setOnClickListener(this);
        uninstallAdapter=new AppUninstallAdapter(context, appInfos);
        lvAppList.setAdapter(uninstallAdapter);
        getAppList();
    }
    
    
    @SuppressWarnings("unchecked")
    private void getAppList(){
        final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
              ArrayList<AppInfo> infos =(ArrayList<AppInfo>) msg.obj;
              appInfos.clear();
              appInfos.addAll(infos);
              uninstallAdapter.notifyDataSetChanged();
              infos.clear();
              infos=null;
          }  
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AppInfo> infos=PackageUtils.getUserAppList(msApp);
                Message msg=handler.obtainMessage();
                msg.obj=infos;
                msg.sendToTarget();
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
    
    
}
