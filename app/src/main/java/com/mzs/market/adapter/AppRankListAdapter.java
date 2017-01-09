
package com.mzs.market.adapter;

import com.mzs.market.R;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.bean.MzsAPK;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.manager.DownloadObserverAdapter;
import com.mzs.market.utils.FileUtils;
import com.mzs.market.utils.ImageloaderHelper;
import com.mzs.market.utils.PackageUtils;
import com.mzs.market.utils.UIUtils;
import com.mzs.market.widget.TriangleCornerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class AppRankListAdapter extends BaseAdapter {

    private ArrayList<MzsAPK> beans;

    private Context context;

    private LayoutInflater inflater;

    private DownloadObserverAdapter observerAdapter = DownloadObserverAdapter
            .getInstance();

    public static final String[] COLORS = {
            "#DE5141", "#FFB72D", "#00D8DB", "#CECECE"
    };

    public AppRankListAdapter(Context context, ArrayList<MzsAPK> beans) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans != null ? beans.size() : 0;
    }

    @Override
    public MzsAPK getItem(int position) {
        return beans != null ? beans.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public ImageView icon;
        public TextView nick, desc, downloadCount, size, ibtnDownload;
        public ProgressBar pbDownload;
        public TextView tvSpeed, tvProgress;
        public View pbView;
        public TriangleCornerView cornerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lv_app_rank, parent,
                    false);
            holder.icon = (ImageView) convertView.findViewById(R.id.apk_icon);
            holder.nick = (TextView) convertView.findViewById(R.id.apk_name);
            holder.desc = (TextView) convertView.findViewById(R.id.apk_intro);
            holder.downloadCount = (TextView) convertView
                    .findViewById(R.id.download_count);
            holder.size = (TextView) convertView.findViewById(R.id.apk_size);
            holder.ibtnDownload = (TextView) convertView
                    .findViewById(R.id.btn_download);
            holder.pbView = convertView.findViewById(R.id.rl_progress);
            holder.tvProgress = (TextView) convertView
                    .findViewById(R.id.tv_progress);
            holder.tvSpeed = (TextView) convertView.findViewById(R.id.tv_speed);
            holder.pbDownload = (ProgressBar) convertView
                    .findViewById(R.id.pb_download);
            holder.cornerView = (TriangleCornerView) convertView.findViewById(R.id.corner_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MzsAPK apk = getItem(position);
        holder.nick.setText(apk.nick);
        holder.size.setText(apk.size + "M");
        holder.desc.setText(apk.desc);
        holder.downloadCount.setText(apk.downloadCount + "次下载");
        ImageLoader.getInstance().displayImage(apk.iconUrl, holder.icon,
                ImageloaderHelper.getOptions());
        if (position > 4) {
            holder.cornerView.setCornerColorAndText(0, "");
        } else {
            holder.cornerView.setCornerColorAndText(
                    position > 2 ? Color.parseColor(COLORS[3]) : Color
                            .parseColor(COLORS[position]),
                    String.valueOf(position + 1));
        }
        setBtnState(apk, holder);
        return convertView;
    }

    private void setBtnState(final MzsAPK apk, final ViewHolder holder) {
        switch (apk.downloadStatus) {
            case MzsConstant.Status.STATUS_DEFAULT: // 初始状态
                holder.ibtnDownload.setText("下载");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.downloadCount.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.VISIBLE);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_DOWNLOADING: // 下载中
                holder.ibtnDownload.setText("暂停");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText(apk.speed + " KB/S");
                holder.pbDownload.setProgress(apk.progress);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_ERROR: // 点击重新开始任务
                holder.ibtnDownload.setText("重试");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText("0 KB/S");
                holder.pbDownload.setProgress(apk.progress);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_PAUSE: // 点击继续任务
                holder.ibtnDownload.setText("继续");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText(apk.speed + " KB/S");
                holder.pbDownload.setProgress(apk.progress);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_COMPLETE: // 点击安装
                holder.ibtnDownload.setText("安装");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.downloadCount.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.VISIBLE);
                holder.ibtnDownload.setEnabled(true);
                break;
            case MzsConstant.Status.STATUS_WAIT:
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.downloadCount.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.VISIBLE);
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

    private void onDownBtnClick(MzsAPK apk, ViewHolder holder) {
        switch (apk.downloadStatus) {
            case MzsConstant.Status.STATUS_DEFAULT: // 点击下载
                observerAdapter.getDownloadService().addTask(
                        DownloadInfo.convertInfo(apk));
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.INVISIBLE);
                holder.downloadCount.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.VISIBLE);
                apk.downloadStatus = MzsConstant.Status.STATUS_WAIT;
                holder.ibtnDownload.setEnabled(false);
                break;
            case MzsConstant.Status.STATUS_DOWNLOADING: // 点击暂停任务
                observerAdapter.getDownloadService().pauseTask(
                        apk.downloadUrl);
                holder.ibtnDownload.setText("继续");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText("0KB/S");
                holder.pbDownload.setProgress(apk.progress);
                apk.downloadStatus = MzsConstant.Status.STATUS_PAUSE;
                break;
            case MzsConstant.Status.STATUS_ERROR: // 点击重新开始任务
                observerAdapter.getDownloadService().continueTask(
                        DownloadInfo.convertInfo(apk));
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText("0KB/S");
                holder.pbDownload.setProgress(apk.progress);
                apk.downloadStatus = MzsConstant.Status.STATUS_WAIT;
                holder.ibtnDownload.setEnabled(false);
                break;
            case MzsConstant.Status.STATUS_PAUSE: // 点击继续任务
                observerAdapter.getDownloadService().continueTask(
                        DownloadInfo.convertInfo(apk));
                holder.ibtnDownload.setText("等待");
                holder.pbView.setVisibility(View.VISIBLE);
                holder.downloadCount.setVisibility(View.INVISIBLE);
                holder.size.setVisibility(View.INVISIBLE);
                holder.tvProgress.setText(apk.progress + "%");
                holder.tvSpeed.setText("0KB/S");
                holder.pbDownload.setProgress(apk.progress);
                apk.downloadStatus = MzsConstant.Status.STATUS_WAIT;
                holder.ibtnDownload.setEnabled(false);
                break;
            case MzsConstant.Status.STATUS_COMPLETE: // 点击安装
                UIUtils.showShortToast("安装应用");
                PackageUtils.install(context, FileUtils.getApkLocalPath(apk.downloadUrl));
                break;
        }
    }
}
