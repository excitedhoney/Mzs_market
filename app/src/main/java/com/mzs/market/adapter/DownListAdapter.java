
package com.mzs.market.adapter;

import com.mzs.market.R;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.utils.FileUtils;
import com.mzs.market.utils.ImageloaderHelper;
import com.mzs.market.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class DownListAdapter extends BaseAdapter {

    private ArrayList<DownloadInfo> items;

    private LayoutInflater inflater;

    private static final int DOWNLOAD_INFO = 0;

    private static final int DOWNLOADING_LABEL = 1;

    private static final int DOWNLOADED_LABEL = 2;

    private static final int ITEM_MAX_COUNT = 3;

    private DownloadObserverAdapter observerAdapter = DownloadObserverAdapter.getInstance();

    public DownListAdapter(Context context, ArrayList<DownloadInfo> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public DownloadInfo getItem(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).item_type;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_MAX_COUNT;
    }

    public static class ViewHolder {
        public ImageView ivApkIcon;
        public TextView tvApkName, tvApkSize, tvApkSpeed, tvApkProgress, ibtnDownload;
        public ProgressBar pbProgress;
        public View pbView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final int item_type = getItemViewType(position);
        if (convertView == null) {
            switch (item_type) {
                case DOWNLOAD_INFO:
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_down_list, parent, false);
                    holder.ivApkIcon = (ImageView) convertView.findViewById(R.id.apk_icon);
                    holder.tvApkName = (TextView) convertView.findViewById(R.id.apk_name);
                    holder.tvApkSize = (TextView) convertView.findViewById(R.id.apk_size);
                    holder.tvApkSpeed = (TextView) convertView.findViewById(R.id.tv_speed);
                    holder.tvApkProgress = (TextView) convertView.findViewById(R.id.tv_progress);
                    holder.pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_download);
                    holder.pbView = convertView.findViewById(R.id.rl_progress);
                    holder.ibtnDownload = (TextView) convertView.findViewById(R.id.btn_download);
                    convertView.setTag(holder);
                    break;
                case DOWNLOADING_LABEL:
                case DOWNLOADED_LABEL:
                    convertView = inflater.inflate(R.layout.item_down_list_label, parent, false);
                    break;
            }
        } else {
            if (item_type == DOWNLOAD_INFO) {
                holder = (ViewHolder) convertView.getTag();
            }
        }
        switch (item_type) {
            case DOWNLOAD_INFO:
                final DownloadInfo info = getItem(position);
                ImageLoader.getInstance().displayImage(info.iconUrl, holder.ivApkIcon,
                        ImageloaderHelper.getOptions());
                holder.tvApkName.setText(info.name);
                holder.tvApkSize.setText(FileUtils.setFileSize(info.totalSize));
                setBtnState(info, holder);
                break;
            case DOWNLOADING_LABEL: // 正在下载标签
                ((TextView) (convertView.findViewById(R.id.tv_label_name))).setText("下载中");
                break;
            case DOWNLOADED_LABEL: // 下载完成标签
                ((TextView) (convertView.findViewById(R.id.tv_label_name))).setText("下载完成");
                break;
        }
        return convertView;
    }

    private void setBtnState(final DownloadInfo apk, final ViewHolder holder) {
        switch (apk.status) {
            case MzsConstant.Status.STATUS_DOWNLOADING: // 下载中
                holder.ibtnDownload.setText("暂停");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.tvApkSize.setVisibility(View.INVISIBLE);
                holder.tvApkProgress.setText((int) (apk.currentSize * 100 / apk.totalSize) + "%");
                holder.pbProgress.setProgress((int) (apk.currentSize * 100 / apk.totalSize));
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_ERROR: // 点击重新开始任务
                holder.ibtnDownload.setText("重试");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.tvApkSize.setVisibility(View.INVISIBLE);
                holder.tvApkProgress.setText((int) (apk.currentSize * 100 / apk.totalSize) + "%");
                holder.pbProgress.setProgress((int) (apk.currentSize * 100 / apk.totalSize));
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_PAUSE: // 点击继续任务
                holder.ibtnDownload.setText("继续");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.tvApkSize.setVisibility(View.INVISIBLE);
                holder.tvApkProgress.setText((int) (apk.currentSize * 100 / apk.totalSize) + "%");
                holder.pbProgress.setProgress((int) (apk.currentSize * 100 / apk.totalSize));
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_COMPLETE: // 点击安装
                holder.ibtnDownload.setText("安装");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.tvApkSize.setVisibility(View.VISIBLE);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_WAIT:
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.tvApkSize.setVisibility(View.VISIBLE);
                holder.ibtnDownload.setEnabled(false);
                break;
        }
        holder.ibtnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownBtnClick(apk, holder);
            }
        });
    }

    private void onDownBtnClick(DownloadInfo apk, ViewHolder holder) {
        switch (apk.status) {
            case MzsConstant.Status.STATUS_DOWNLOADING: // 点击暂停任务
                observerAdapter.getDownloadService().pauseTask(apk.url);
                holder.ibtnDownload.setText("继续");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.tvApkSize.setVisibility(View.INVISIBLE);
                apk.status = MzsConstant.Status.STATUS_PAUSE;
                break;
            case MzsConstant.Status.STATUS_ERROR: // 点击重新开始任务
                observerAdapter.getDownloadService().continueTask(apk);
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.tvApkSize.setVisibility(View.VISIBLE);
                apk.status = MzsConstant.Status.STATUS_WAIT;
                holder.ibtnDownload.setEnabled(false);
                break;
            case MzsConstant.Status.STATUS_PAUSE: // 点击继续任务
                observerAdapter.getDownloadService().continueTask(apk);
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.tvApkSize.setVisibility(View.VISIBLE);
                apk.status = MzsConstant.Status.STATUS_WAIT;
                holder.ibtnDownload.setEnabled(false);
                break;
            case MzsConstant.Status.STATUS_COMPLETE: // 点击安装
                UIUtils.showShortToast("安装应用");
                break;
        }

    }

}
