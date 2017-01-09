
package com.mzs.market.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.mzs.market.R;
import com.mzs.market.adapter.AppRankListAdapter;
import com.mzs.market.bean.ChoiceAPKList;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.net.GsonRequest;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.net.ParamsList;
import com.mzs.market.net.RequestParams;
import com.mzs.market.net.UrlUtils;
import com.mzs.market.widget.PullToRefreshLayout;
import com.mzs.market.widget.PullToRefreshLayout.OnRefreshListener;
import java.util.ArrayList;

public class AppRankFragment extends Fragment implements OnScrollListener,OnRefreshListener{

    private ArrayList<MzsAPK> rankApps = new ArrayList<MzsAPK>();
    private ListView lvAppRank;
    private PullToRefreshLayout refreshLayout;
    private AppRankListAdapter adapter = null;
    private int total = -1;
    private boolean isLoading = false;
    private ViewStub loadingStub = null;
    private View footerView = null;
    private ProgressBar footerPb = null;
    private TextView footerTv = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_app_rank, container, false);
        refreshLayout=(PullToRefreshLayout) root.findViewById(R.id.refresh_view);
        lvAppRank = (ListView) root.findViewById(R.id.lv_app_rank);
        lvAppRank.addHeaderView(inflater.inflate(R.layout.divider_header, lvAppRank, false));
        loadingStub = (ViewStub) root.findViewById(R.id.vs_loading);
        footerView = inflater.inflate(R.layout.lv_loadmore_footer, lvAppRank, false);
        footerPb = (ProgressBar) footerView.findViewById(R.id.footer_pb);
        footerTv = (TextView) footerView.findViewById(R.id.tv_footer_tips);
        lvAppRank.setOnScrollListener(this);
        refreshLayout.setOnRefreshListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rankApps.isEmpty() && !isLoading) {
            getAppRankList(MzsConstant.REQUEST_INIT);
        }
    }

    private void getAppRankList(final int state) {
        if (isLoading)
            return;
        isLoading = true;
        int start=0;
        if (state == MzsConstant.REQUEST_MORE) {
            if (rankApps.size() >= total) {
                footerPb.setVisibility(View.GONE);
                footerTv.setText(getString(R.string.no_more_data));
                return;
            }
            footerPb.setVisibility(View.VISIBLE);
            footerTv.setText(getString(R.string.loading_data));
            start=rankApps.size();
        } else if (state == MzsConstant.REQUEST_INIT) {
            loadingStub.setVisibility(View.VISIBLE);
            start=0;
        }else if (state==MzsConstant.REQUEST_REFRESH) {
            start=0;
        }
        ParamsList params = new ParamsList();
        params.add(new RequestParams("start",String.valueOf(start)));
        params.add(new RequestParams("requestSize", "20"));
        params.add(new RequestParams("deviceType", String.valueOf(0)));
        params.add(new RequestParams("deviceToken", "sex"));
        final String url = UrlUtils.encodeUrl(MzsConstant.MUST_HAVE_LIST, params);
        Response.Listener<ChoiceAPKList> responseListener = new Response.Listener<ChoiceAPKList>() {
            @Override
            public void onResponse(ChoiceAPKList response) {
                if (state == MzsConstant.REQUEST_INIT) {
                    loadingStub.setVisibility(View.GONE);
                } else if(state==MzsConstant.REQUEST_MORE){
                    footerPb.setVisibility(View.GONE);
                    footerTv.setText(getString(R.string.load_more_data));
                }else if (state==MzsConstant.REQUEST_REFRESH) {
                    refreshLayout.onRefreshSuccess();
                }
                if (response != null) {
                    if (response.errorCode == 0) {
                        if (state == MzsConstant.REQUEST_INIT) {
                            total = response.total;
                            rankApps.addAll(response.apkList);
                            lvAppRank.addFooterView(footerView);
                            adapter = new AppRankListAdapter(getActivity(), rankApps);
                            lvAppRank.setAdapter(adapter);
                        } else if (state == MzsConstant.REQUEST_MORE) {
                            rankApps.addAll(response.apkList);
                            adapter.notifyDataSetChanged();
                        }else if (state==MzsConstant.REQUEST_REFRESH) {
                            rankApps.clear();
                            rankApps.addAll(response.apkList);
                            adapter.notifyDataSetChanged();
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
                }else if (state==MzsConstant.REQUEST_REFRESH) {
                    refreshLayout.onRefreshFailed();
                }
                isLoading = false;
            }
        };
        GsonRequest<ChoiceAPKList> request = new GsonRequest<ChoiceAPKList>(Method.GET, url,
                ChoiceAPKList.class, responseListener, errorListener);
        MzsVolley.getRequestQueue().add(request);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getAppRankList(MzsConstant.REQUEST_REFRESH);
    }
}
