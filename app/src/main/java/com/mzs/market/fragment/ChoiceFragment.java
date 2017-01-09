
package com.mzs.market.fragment;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mzs.market.R;
import com.mzs.market.activity.APKDetailActivity;
import com.mzs.market.adapter.ChoiceListAdapter;
import com.mzs.market.adapter.ChoiceListAdapter.ViewHolder;
import com.mzs.market.bean.AdsImageList;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.bean.ChoiceAPKList;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.db.DownloadDao;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.download.service.DownloadTaskListener;
import com.mzs.market.net.GsonRequest;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.net.ParamsList;
import com.mzs.market.net.RequestParams;
import com.mzs.market.net.UrlUtils;
import com.mzs.market.utils.UIUtils;
import com.mzs.market.view.ChoiceListHeader;
import com.mzs.market.widget.PullToRefreshLayout;
import com.mzs.market.widget.PullToRefreshLayout.OnRefreshListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @category 精选
 * @author wanghao
 * @date 2014.7.12 17:23
 */
public class ChoiceFragment extends Fragment implements OnScrollListener,
        OnItemClickListener, DownloadTaskListener, OnRefreshListener {

    private ListView choice_list;

    private PullToRefreshLayout refreshLayout;

    private ChoiceListHeader headView;

    private ChoiceListAdapter adapter = null;

    private ArrayList<MzsAPK> choiceAPKs = null;

    private int total = -1;

    private boolean isLoading = false;

    private ViewStub loadingStub = null;

    private View footerView = null;

    private ProgressBar footerPb = null;

    private TextView footerTv = null;

    private HashMap<String, Integer> indexs = new HashMap<String, Integer>();

    private DownloadDao mDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choice, container,
                false);
        refreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
        choice_list = (ListView) rootView.findViewById(R.id.pull_choice_list);
        loadingStub = (ViewStub) rootView.findViewById(R.id.vs_loading);
        // list footer
        footerView = inflater.inflate(R.layout.lv_loadmore_footer, choice_list, false);
        footerPb = (ProgressBar) footerView.findViewById(R.id.footer_pb);
        footerTv = (TextView) footerView.findViewById(R.id.tv_footer_tips);
        refreshLayout.setOnRefreshListener(this);
        choice_list.setOnScrollListener(this);
        choice_list.setOnItemClickListener(this);
        headView = new ChoiceListHeader(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDao = new DownloadDao();
        choice_list.addHeaderView(headView);
        getChoiceList(MzsConstant.REQUEST_INIT);
        getAdsImgList();
        DownloadObserverAdapter.registerObserver(this);
    }

    /**
     * 更新listview 进度
     * 
     * @param url
     * @param progress
     * @param speed
     */
    private void updateItemProgress(String url, int progress, int speed) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_DOWNLOADING;
        apk.progress = progress;
        apk.speed = speed;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.VISIBLE);
            holder.downloadCount.setVisibility(View.INVISIBLE);
            holder.size.setVisibility(View.INVISIBLE);
            holder.pbDownload.setProgress(progress);
            holder.tvProgress.setText(progress + "%");
            holder.tvSpeed.setText(speed + " KB/S");
            holder.ibtnDownload.setText("暂停");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    /**
     * 下载完成，更改item状态
     * 
     * @param url
     */
    private void finishItem(String url) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_COMPLETE;
        apk.progress = 100;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.INVISIBLE);
            holder.downloadCount.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.ibtnDownload.setText("安装");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    private void getAdsImgList() {
        ParamsList params = new ParamsList();
        params.add(new RequestParams("deviceType", String.valueOf(0)));
        params.add(new RequestParams("deviceToken", "sex"));
        final String url = UrlUtils.encodeUrl(MzsConstant.ADS_IMG, params);
        Response.Listener<AdsImageList> responseListener = new Response.Listener<AdsImageList>() {
            @Override
            public void onResponse(AdsImageList response) {
                if (response != null) {
                    if (response.errorCode == 0) {
                        headView.setImageData(response.imgList);
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("wh", "get choice list error : " + error.toString());
            }
        };
        GsonRequest<AdsImageList> request = new GsonRequest<AdsImageList>(
                Method.GET, url, AdsImageList.class, responseListener,
                errorListener);
        MzsVolley.getRequestQueue().add(request);
    }

    private void getChoiceList(final int state) {
        if (isLoading)
            return;
        isLoading = true;
        int start = 0;
        if (state == MzsConstant.REQUEST_MORE) {
            if (choiceAPKs.size() >= total) {
                footerPb.setVisibility(View.GONE);
                footerTv.setText(getString(R.string.no_more_data));
                return;
            }
            footerPb.setVisibility(View.VISIBLE);
            footerTv.setText(getString(R.string.loading_data));
            start = choiceAPKs.size();
        } else if (state == MzsConstant.REQUEST_INIT) {
            loadingStub.setVisibility(View.VISIBLE);
            start = 0;
        } else if (state == MzsConstant.REQUEST_REFRESH) {
            start = 0;
        }
        ParamsList params = new ParamsList();
        params.add(new RequestParams("start", String.valueOf(start)));
        params.add(new RequestParams("requestSize", "20"));
        params.add(new RequestParams("deviceType", String.valueOf(0)));
        params.add(new RequestParams("deviceToken", "sex"));
        final String url = UrlUtils.encodeUrl(MzsConstant.CHOICE_LIST, params);
        Response.Listener<ChoiceAPKList> responseListener = new Response.Listener<ChoiceAPKList>() {
            @Override
            public void onResponse(ChoiceAPKList response) {
                if (state == MzsConstant.REQUEST_INIT) {
                    loadingStub.setVisibility(View.GONE);
                } else if (state == MzsConstant.REQUEST_MORE) {
                    footerPb.setVisibility(View.GONE);
                    footerTv.setText(getString(R.string.load_more_data));
                } else if (state == MzsConstant.REQUEST_REFRESH) {
                    refreshLayout.onRefreshSuccess();
                }
                if (response != null) {
                    if (response.errorCode == 0) {
                        if (state == MzsConstant.REQUEST_INIT) {
                            total = response.total;
                            choiceAPKs = response.apkList;
                            setApkStatus(response.apkList);
                            adapter = new ChoiceListAdapter(getActivity(),
                                    choiceAPKs);
                            choice_list.addFooterView(footerView);
                            choice_list.setAdapter(adapter);
                        } else if (state == MzsConstant.REQUEST_MORE) {
                            setApkStatus(response.apkList);
                            choiceAPKs.addAll(response.apkList);
                            adapter.notifyDataSetChanged();
                        } else if (state == MzsConstant.REQUEST_REFRESH) {
                            choiceAPKs.clear();
                            choiceAPKs.addAll(response.apkList);
                            setApkStatus(response.apkList);
                            adapter.notifyDataSetChanged();
                        }
                        indexs.clear();
                        // 将url保存
                        for (int i = 0; i < choiceAPKs.size(); i++) {
                            indexs.put(choiceAPKs.get(i).downloadUrl, i);
                        }
                    }
                }
                isLoading = false;
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("wh", "get choice list error : " + error.toString());
                if (state == MzsConstant.REQUEST_INIT) {
                    loadingStub.setVisibility(View.GONE);
                } else if (state == MzsConstant.REQUEST_MORE) {
                    footerPb.setVisibility(View.GONE);
                    footerTv.setText(getString(R.string.load_more_data));
                } else if (state == MzsConstant.REQUEST_REFRESH) {
                    refreshLayout.onRefreshSuccess();
                }
                isLoading = false;
            }
        };
        GsonRequest<ChoiceAPKList> request = new GsonRequest<ChoiceAPKList>(
                Method.GET, url, ChoiceAPKList.class, responseListener,
                errorListener);
        MzsVolley.getRequestQueue().add(request);
    }

    private void setApkStatus(ArrayList<MzsAPK> response) {
        if (response == null || response.isEmpty())
            return;
        ArrayList<DownloadInfo> infos = mDao.queryAllDownloads();
        for (MzsAPK apk : response) {
            for (DownloadInfo info : infos) {
                if (apk.downloadUrl.equals(info.url)) {
                    apk.downloadStatus = info.status;
                    apk.progress = (int) (info.currentSize * 100 / info.totalSize);
                    apk.currentSize = info.currentSize;
                    apk.totalSize = info.totalSize;
                    break;
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                if (!isLoading) {
                    getChoiceList(MzsConstant.REQUEST_MORE);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("apk", choiceAPKs.get(position - 1));
        UIUtils.openActivity(getActivity(), APKDetailActivity.class, bundle);
    }

    @Override
    public void startDownload(String url) {

    }

    @Override
    public void finishDownload(String url) {
        finishItem(url);
    }

    @Override
    public void errorDownload(String url, int type) {
        Log.d(MzsConstant.LOG_TAG, "出错了-->type : " + type);
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_ERROR;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.VISIBLE);
            holder.downloadCount.setVisibility(View.INVISIBLE);
            holder.size.setVisibility(View.INVISIBLE);
            holder.ibtnDownload.setText("重试");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    @Override
    public void waitDownload(String url) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_WAIT;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.INVISIBLE);
            holder.downloadCount.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.ibtnDownload.setText("等待");
            holder.ibtnDownload.setEnabled(false);
        }
    }

    @Override
    public void updateProgress(String url, long totalSize, long currentSize,
            int progress, int speed) {
        updateItemProgress(url, progress, speed);
    }

    @Override
    public void pauseTask(String url) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_PAUSE;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.VISIBLE);
            holder.downloadCount.setVisibility(View.INVISIBLE);
            holder.size.setVisibility(View.INVISIBLE);
            holder.ibtnDownload.setText("继续");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    @Override
    public void deleteTask(String url) {
        Integer itemIndex = indexs.get(url);
        if (itemIndex == null)
            return;
        MzsAPK apk = choiceAPKs.get(itemIndex);
        apk.downloadStatus = MzsConstant.Status.STATUS_DEFAULT;
        apk.currentSize = 0;
        apk.progress = 0;
        apk.speed = 0;
        int visiblePosition = choice_list.getFirstVisiblePosition();
        if (itemIndex - visiblePosition >= 0) {
            View view = choice_list.getChildAt(itemIndex - visiblePosition + 1);
            if (view == null)
                return;
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.pbView.setVisibility(View.INVISIBLE);
            holder.pbDownload.setProgress(0);
            holder.tvProgress.setText("--%");
            holder.tvSpeed.setText("0KB/S");
            holder.downloadCount.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.ibtnDownload.setText("下载");
            holder.ibtnDownload.setEnabled(true);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getChoiceList(MzsConstant.REQUEST_REFRESH);
    }
}
