
package com.mzs.market.activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.mzs.market.R;
import com.mzs.market.adapter.DetailRelativeAdapter;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.bean.GameDetailBean;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.download.service.DownloadTaskListener;
import com.mzs.market.net.GsonRequest;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.net.ParamsList;
import com.mzs.market.net.RequestParams;
import com.mzs.market.net.UrlUtils;
import com.mzs.market.utils.FileUtils;
import com.mzs.market.utils.ImageloaderHelper;
import com.mzs.market.utils.PackageUtils;
import com.mzs.market.widget.ProgressTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @category APK 详情页
 * @author wanghao
 * @date 2014.8.23 15:13
 */
public class APKDetailActivity extends BaseActivity implements View.OnClickListener,
        DownloadTaskListener {

    private MzsAPK apk = null;

    private ScrollView detailScrollView;

    private ImageView imApkIcon;

    private TextView tvApkName, tvApkType, tvDownloadCount,tvDownload;

    private ViewStub loadingStub;

    private Button ibtnBack;

    private TextView tvApkDesc, tvApkUpdate;

    private ProgressTextView pbTextView;

    private RecyclerView rvRelativeApk;
    
    private DownloadObserverAdapter downAdapter;
    
    private ArrayList<MzsAPK> relativeApks;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apk = (MzsAPK) getIntent().getSerializableExtra("apk");
        if (apk == null) {
            Log.e(MzsConstant.LOG_TAG, "apk is null");
            finish();
            return;
        }
        downAdapter = DownloadObserverAdapter.getInstance();
        DownloadObserverAdapter.registerObserver(this);
        setContentView(R.layout.activity_apkdetail);
        initView();
        getApkDetail();
    }

    private void initView() {
        detailScrollView = (ScrollView) findViewById(R.id.scrollview_detail);
        imApkIcon = (ImageView) findViewById(R.id.apk_icon);
        tvApkName = (TextView) findViewById(R.id.tv_apk_name);
        tvApkType = (TextView) findViewById(R.id.tv_apk_type);
        tvDownloadCount = (TextView) findViewById(R.id.apk_download_count);
        loadingStub = (ViewStub) findViewById(R.id.vs_loading);
        ibtnBack = (Button) findViewById(R.id.ibtn_back);
        // apk简介，更新内容
        tvApkDesc = (TextView) findViewById(R.id.tv_apk_desc);
        tvApkUpdate = (TextView) findViewById(R.id.tv_apk_update_tip);
        // progress
        tvDownload=(TextView) findViewById(R.id.tv_download);
        pbTextView = (ProgressTextView) findViewById(R.id.pb_tv);
        //Relative apks
        rvRelativeApk=(RecyclerView) findViewById(R.id.rv_relative_apk);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);  
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);  
        rvRelativeApk.setLayoutManager(linearLayoutManager);  
        
        detailScrollView.setVisibility(View.GONE);
        loadingStub.setVisibility(View.VISIBLE);
        ibtnBack.setOnClickListener(this);
        pbTextView.setOnClickListener(this);
        tvDownload.setOnClickListener(this);
        // 设置默认状态
        setDefaultStatus(apk);
    }

    private void getApkDetail() {
        ParamsList params = new ParamsList();
        params.add(new RequestParams("apk_id", String.valueOf(apk.apkId)));
        params.add(new RequestParams("deviceType", String.valueOf(0)));
        params.add(new RequestParams("deviceToken", "sex"));
        final String url = UrlUtils.encodeUrl(MzsConstant.GAME_DETAIL, params);
        Response.Listener<GameDetailBean> responseListener = new Response.Listener<GameDetailBean>() {
            @Override
            public void onResponse(GameDetailBean response) {
                if (response != null) { // 正常返回
                    setApkDetailUI(response);
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("wh", "get choice list error : " + error.toString());
            }
        };
        GsonRequest<GameDetailBean> request = new GsonRequest<GameDetailBean>(Method.GET, url,
                GameDetailBean.class, responseListener, errorListener);
        MzsVolley.getRequestQueue().add(request);
    }

    private void setApkDetailUI(GameDetailBean bean) {
        loadingStub.setVisibility(View.GONE);
        ImageLoader.getInstance().displayImage(apk.iconUrl, imApkIcon,
                ImageloaderHelper.getOptions());
        tvApkName.setText(apk.nick);
        tvApkType.setText(bean.developer + "  |  " + bean.lastest_version);
        tvDownloadCount.setText(apk.size + "M  |  " + apk.downloadCount + " 次下载");
        tvApkDesc.setText(bean.desc);
        relativeApks=bean.relate_apks;
        rvRelativeApk.setAdapter(new DetailRelativeAdapter(context,relativeApks));
        detailScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.pb_tv:
                onProgressBtnClick();
                break;
            case R.id.tv_download:
                onDownloadBtnClick();
                break;
        }
    }

    /**
     * 进度点击
     */
    private void onProgressBtnClick() {
        if (apk.downloadStatus == MzsConstant.Status.STATUS_DOWNLOADING) { // 暂停下载
            downAdapter.getDownloadService().pauseTask(apk.downloadUrl);
            pbTextView.setText("继续");
            apk.downloadStatus=MzsConstant.Status.STATUS_PAUSE;
        } else if (apk.downloadStatus == MzsConstant.Status.STATUS_PAUSE) { // 点击继续
            downAdapter.getDownloadService().continueTask(DownloadInfo.convertInfo(apk));
            pbTextView.setEnabled(false);
            pbTextView.setText("等待");
            apk.downloadStatus=MzsConstant.Status.STATUS_WAIT;
        } else if (apk.downloadStatus == MzsConstant.Status.STATUS_ERROR) { // 点击继续
            downAdapter.getDownloadService().continueTask(DownloadInfo.convertInfo(apk));
            pbTextView.setEnabled(false);
            pbTextView.setText("等待");
            apk.downloadStatus=MzsConstant.Status.STATUS_WAIT;
        }
    }
    
    /**
     * 下载按钮点击
     */
    private void onDownloadBtnClick(){
        if (apk.downloadStatus == MzsConstant.Status.STATUS_DEFAULT) { // 点击下载
            downAdapter.getDownloadService().addTask(DownloadInfo.convertInfo(apk));
            tvDownload.setVisibility(View.GONE);
            pbTextView.setVisibility(View.VISIBLE);
            pbTextView.setEnabled(false);
            pbTextView.setText("等待");
        } else if (apk.downloadStatus == MzsConstant.Status.STATUS_COMPLETE) { // 安装
            PackageUtils.install(context, FileUtils.getApkLocalPath(apk.downloadUrl));
        }
    }

    /**
     * 设置按钮默认状态
     */
    private void setDefaultStatus(MzsAPK apk) {
        switch (apk.downloadStatus) {
            case MzsConstant.Status.STATUS_DEFAULT:
                tvDownload.setVisibility(View.VISIBLE);
                pbTextView.setVisibility(View.GONE);
                tvDownload.setText("下载  "+apk.size+"M");
                break;
            case MzsConstant.Status.STATUS_DOWNLOADING:
                tvDownload.setVisibility(View.GONE);
                pbTextView.setVisibility(View.VISIBLE);
                pbTextView.setText(apk.progress + "%");
                pbTextView.setProgress(apk.progress);
                break;
            case MzsConstant.Status.STATUS_ERROR:
                tvDownload.setVisibility(View.GONE);
                pbTextView.setVisibility(View.VISIBLE);
                pbTextView.setText("点击重试");
                pbTextView.setProgress(apk.progress);
                break;
            case MzsConstant.Status.STATUS_COMPLETE:
                tvDownload.setVisibility(View.VISIBLE);
                pbTextView.setVisibility(View.GONE);
                tvDownload.setText("安装");
                break;
            case MzsConstant.Status.STATUS_PAUSE:
                tvDownload.setVisibility(View.GONE);
                pbTextView.setVisibility(View.VISIBLE);
                pbTextView.setText("继续");
                pbTextView.setProgress(apk.progress);
                break;
            case MzsConstant.Status.STATUS_WAIT:
                tvDownload.setVisibility(View.VISIBLE);
                pbTextView.setVisibility(View.GONE);
                tvDownload.setText("等待");
                tvDownload.setEnabled(false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadObserverAdapter.unregisterObserver(this);
    }

    @Override
    public void startDownload(String url) {
        waitDownload(url);
    }

    @Override
    public void updateProgress(String url, long totalSize, long currentSize, int progress, int speed) {
        if (!url.equals(apk.downloadUrl))
            return;
        pbTextView.setVisibility(View.VISIBLE);
        tvDownload.setVisibility(View.GONE);
        pbTextView.setProgress(progress);
        pbTextView.setText(progress + "%");
        pbTextView.setEnabled(true);
        apk.downloadStatus = MzsConstant.Status.STATUS_DOWNLOADING;
    }

    @Override
    public void finishDownload(String url) {
        if (!url.equals(apk.downloadUrl))
            return;
        pbTextView.setVisibility(View.GONE);
        tvDownload.setVisibility(View.VISIBLE);
        tvDownload.setEnabled(true);
        tvDownload.setText("安装");
        apk.downloadStatus = MzsConstant.Status.STATUS_COMPLETE;
    }

    @Override
    public void errorDownload(String url, int type) {
        if (!url.equals(apk.downloadUrl))
            return;
        pbTextView.setText("继续");
        pbTextView.setEnabled(true);
        apk.downloadStatus = MzsConstant.Status.STATUS_ERROR;
    }

    @Override
    public void waitDownload(String url) {
        if (!url.equals(apk.downloadUrl))
            return;
        pbTextView.setVisibility(View.VISIBLE);
        tvDownload.setVisibility(View.GONE);
        pbTextView.setText("等待");
        pbTextView.setEnabled(false);
        apk.downloadStatus = MzsConstant.Status.STATUS_WAIT;
    }

    @Override
    public void pauseTask(String url) {
        
    }

    @Override
    public void deleteTask(String url) {
        
    }
}
