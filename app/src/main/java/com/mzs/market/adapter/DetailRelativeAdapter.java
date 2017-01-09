
package com.mzs.market.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mzs.market.R;
import com.mzs.market.activity.APKDetailActivity;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.utils.ImageloaderHelper;
import com.mzs.market.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class DetailRelativeAdapter extends
        RecyclerView.Adapter<DetailRelativeAdapter.RelativeHolder> {

    private LayoutInflater inflater;
    private ArrayList<MzsAPK> mData;
    private Context context;

    public DetailRelativeAdapter(Context context, ArrayList<MzsAPK> mData) {
        this.inflater = LayoutInflater.from(context);
        this.mData = mData;
        this.context=context;
    }

    public static class RelativeHolder extends ViewHolder {
        public ImageView ivApkIcon;
        public TextView tvApkName;

        public RelativeHolder(View view) {
            super(view);
            initView();
        }

        private void initView() {
            ivApkIcon = (ImageView) itemView.findViewById(R.id.iv_relative_apk_icon);
            tvApkName = (TextView) itemView.findViewById(R.id.tv_relative_apk_name);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public void onBindViewHolder(RelativeHolder holder, int position) {
        final MzsAPK apk = mData.get(position);
        ImageLoader.getInstance().displayImage(apk.iconUrl, holder.ivApkIcon,
                ImageloaderHelper.getOptions());
        holder.tvApkName.setText(apk.nick);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("apk",apk);
                UIUtils.openActivity(context, APKDetailActivity.class, bundle);
            }
        });
    }

    @Override
    public RelativeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_relative_apk, parent, false);
        RelativeHolder holder = new RelativeHolder(view);
        return holder;
    }
}
