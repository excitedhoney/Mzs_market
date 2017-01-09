
package com.mzs.market.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mzs.market.R;
import com.mzs.market.bean.AppInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.utils.PackageUtils;
import com.mzs.market.utils.UIUtils;
import java.util.ArrayList;

public class ManagerAppAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<AppInfo> appsList;
    private Context context;

    public ManagerAppAdapter(Context context, ArrayList<AppInfo> appsList) {
        this.context=context;
        this.appsList = appsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appsList != null ? appsList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return appsList != null ? appsList.get(position) :null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public static class AppHolder{
        public ImageView imAppIcon;
        public TextView tvAppName;
        public TextView tvAppSpace;
        public TextView tvInstall,tvDelete;
        public View rlInstall,rlDelete;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AppHolder holder=null;
        if(convertView==null){
            holder=new AppHolder();
            convertView=inflater.inflate(R.layout.item_manager_app_list,parent,false);
            holder.imAppIcon=(ImageView) convertView.findViewById(R.id.app_icon);
            holder.tvAppName=(TextView) convertView.findViewById(R.id.app_name);
            holder.tvAppSpace=(TextView) convertView.findViewById(R.id.app_size);
            holder.tvInstall=(TextView) convertView.findViewById(R.id.tv_apk_install);
            holder.tvDelete=(TextView) convertView.findViewById(R.id.tv_apk_delete);
            holder.rlInstall=convertView.findViewById(R.id.rl_install_apk);
            holder.rlDelete= convertView.findViewById(R.id.rl_delete_apk);
            convertView.setTag(holder);
        }else{
            holder=(AppHolder) convertView.getTag();
        }
        final AppInfo appInfo=appsList.get(position);
        holder.imAppIcon.setBackgroundDrawable(appInfo.drawable);
        holder.tvAppName.setText(appInfo.appName);
        holder.tvAppSpace.setText(appInfo.appSpace+"  |  版本："+appInfo.appVersion);
        if(appInfo.status==MzsConstant.UninstallStatus.STATUS_DEFAULT){
            holder.tvInstall.setText("安装");
            holder.rlInstall.setEnabled(true);
        }else if (appInfo.status==MzsConstant.UninstallStatus.STATUS_INSTALLING) {
            holder.tvInstall.setText("安装中");
            holder.rlInstall.setEnabled(false);
        }
        return convertView;
    }
}
