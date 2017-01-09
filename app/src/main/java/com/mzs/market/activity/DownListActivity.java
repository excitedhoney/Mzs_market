
package com.mzs.market.activity;

import com.mzs.market.R;
import com.mzs.market.adapter.DownListAdapter;
import com.mzs.market.adapter.DownListAdapter.ViewHolder;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.db.DownloadDao;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.download.service.DownloadTaskListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @category 下载管理
 * @author 王浩
 * @date 2014/12/21 AM 11:31
 */
public class DownListActivity extends BaseActivity implements View.OnClickListener,
        DownloadTaskListener {

    private ListView lvDownList;

    private Button btnBack;

    private DownloadDao mdDao;

    private DownListAdapter listAdapter;

    private ArrayList<DownloadInfo> infos;

    private HashMap<String, Integer> indexs = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_list);
        lvDownList = (ListView) findViewById(R.id.lv_down_list);
        btnBack = (Button) findViewById(R.id.ibtn_back);
        btnBack.setOnClickListener(this);
        queryDownList();
    }

    private void queryDownList() {
        mdDao = new DownloadDao();
        ArrayList<DownloadInfo> dbInfos = mdDao.queryAllDownloads();
        assortDownInfo(dbInfos);
        infos = dbInfos;
        listAdapter = new DownListAdapter(context, infos);
        lvDownList.setAdapter(listAdapter);
        if (!infos.isEmpty()) {
            DownloadObserverAdapter.registerObserver(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
        }
    }

    /**
     * 将DownloadInfo 分类
     * 
     * @param infos
     * @return
     */
    private void assortDownInfo(ArrayList<DownloadInfo> infos) {
        ArrayList<DownloadInfo> downing = new ArrayList<DownloadInfo>();
        ArrayList<DownloadInfo> downed = new ArrayList<DownloadInfo>();
        for (DownloadInfo info : infos) {
            if (info.item_type == 0) {
                if (info.status == MzsConstant.Status.STATUS_COMPLETE) {
                    downed.add(info);
                } else {
                    downing.add(info);
                }
            }
        }
        infos.clear();
        if (!downing.isEmpty()) {
            DownloadInfo info = new DownloadInfo();
            info.item_type = 1;
            infos.add(info);
            infos.addAll(downing);
        }
        if (!downed.isEmpty()) {
            DownloadInfo info = new DownloadInfo();
            info.item_type = 2;
            infos.add(info);
            infos.addAll(downed);
        }
        indexs.clear();
        for (int i = 0; i < infos.size(); i++) {
            indexs.put(infos.get(i).url, i);
        }
        downed.clear();
        downed = null;
        downing.clear();
        downing = null;
    }

    @Override
    public void startDownload(String url) {
        waitDownload(url);
    }

    @Override
    public void updateProgress(String url, long totalSize, long currentSize, int progress, int speed) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        DownloadInfo info = infos.get(itemIndex);
        info.status = MzsConstant.Status.STATUS_DOWNLOADING;
        info.currentSize = currentSize;
        info.totalSize = totalSize;
        int visiblePosition = lvDownList.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = lvDownList.getChildAt(itemIndex - visiblePosition);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.VISIBLE);
            holder.tvApkSize.setVisibility(View.INVISIBLE);
            holder.pbProgress.setProgress(progress);
            holder.tvApkProgress.setText(progress + "%");
            holder.tvApkSpeed.setText(speed + "KB/S");
            holder.ibtnDownload.setText("暂停");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    @Override
    public void finishDownload(String url) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        DownloadInfo info = infos.get(itemIndex);
        info.status = MzsConstant.Status.STATUS_COMPLETE;
        assortDownInfo(infos);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorDownload(String url, int type) {
        pauseTask(url);
    }

    @Override
    public void waitDownload(String url) {
    }

    @Override
    public void pauseTask(String url) { // 暂停成功回调
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        DownloadInfo info = infos.get(itemIndex);
        info.status = MzsConstant.Status.STATUS_PAUSE;
        int visiblePosition = lvDownList.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = lvDownList.getChildAt(itemIndex - visiblePosition);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.VISIBLE);
            holder.tvApkSize.setVisibility(View.INVISIBLE);
            holder.ibtnDownload.setText("继续");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    @Override
    public void deleteTask(String url) { // 删除成功回调
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        infos.remove(itemIndex);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadObserverAdapter.unregisterObserver(this);
    }

}
