
package com.mzs.market.fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.mzs.market.R;
import com.mzs.market.adapter.ChoiceListAdapter;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.bean.ChoiceAPKList;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.net.GsonRequest;
import com.mzs.market.net.MzsVolley;
import com.mzs.market.net.ParamsList;
import com.mzs.market.net.RequestParams;
import com.mzs.market.net.UrlUtils;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import java.util.ArrayList;

public class OnlineFragment extends Fragment implements OnScrollListener ,OnRefreshListener{

    private ListView online_list;
    private ChoiceListAdapter adapter = null;
    private ArrayList<MzsAPK> onlineAPKs = null;
    private PullToRefreshLayout refreshLayout;
    private int total = -1;
    private boolean isLoading = false;
    private ViewStub loadingStub = null;
    private View footerView = null;
    private ProgressBar footerPb = null;
    private TextView footerTv = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_online, container, false);
        refreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_view);
        online_list = (ListView) rootView.findViewById(R.id.pull_online_list);
        online_list.addHeaderView(inflater.inflate(R.layout.divider_header, online_list, false));
        // list footer
        footerView = inflater.inflate(R.layout.lv_loadmore_footer, online_list, false);
        footerPb = (ProgressBar) footerView.findViewById(R.id.footer_pb);
        footerTv = (TextView) footerView.findViewById(R.id.tv_footer_tips);

        loadingStub = (ViewStub) rootView.findViewById(R.id.vs_loading);
        online_list.setOnScrollListener(this);
        refreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && onlineAPKs == null && !isLoading) {
            getOnlineList(MzsConstant.REQUEST_INIT);
        }
    }

    private void getOnlineList(final int state) {
        if (state == MzsConstant.REQUEST_MORE) {
            if (onlineAPKs.size() >= total) {
                footerPb.setVisibility(View.GONE);
                footerTv.setText(getString(R.string.no_more_data));
                return;
            }
            footerPb.setVisibility(View.VISIBLE);
            footerTv.setText(getString(R.string.loading_data));
        } else if (state == MzsConstant.REQUEST_INIT) {
            loadingStub.setVisibility(View.VISIBLE);
        }
        if (isLoading)
            return;
        isLoading = true;
        ParamsList params = new ParamsList();
        params.add(new RequestParams("start", state == MzsConstant.REQUEST_MORE ? String
                .valueOf(onlineAPKs.size()):"0"));
        params.add(new RequestParams("requestSize", "20"));
        params.add(new RequestParams("deviceType", String.valueOf(0)));
        params.add(new RequestParams("deviceToken", "sex"));
        final String url = UrlUtils.encodeUrl(MzsConstant.ONLINE_LIST, params);
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
                            onlineAPKs = response.apkList;
                            online_list.addFooterView(footerView);
                            adapter = new ChoiceListAdapter(getActivity(), onlineAPKs);
                            online_list.setAdapter(adapter);
                        } else if (state == MzsConstant.REQUEST_MORE) {
                            onlineAPKs.addAll(response.apkList);
                            adapter.notifyDataSetChanged();
                        }else if (state==MzsConstant.REQUEST_REFRESH) {
                            onlineAPKs.clear();
                            onlineAPKs.addAll(response.apkList);
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
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                if (!isLoading) {
                    getOnlineList(MzsConstant.REQUEST_MORE);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getOnlineList(MzsConstant.REQUEST_REFRESH);
    }
}
