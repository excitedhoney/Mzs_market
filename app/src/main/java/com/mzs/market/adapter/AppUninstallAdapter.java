
package com.mzs.market.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mzs.market.R;
import com.mzs.market.bean.AppInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.utils.PackageUtils;
import com.mzs.market.utils.UIUtils;
import java.util.ArrayList;

public class AppUninstallAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<AppInfo> appsList;
    private Context context;

    public AppUninstallAdapter(Context context, ArrayList<AppInfo> appsList) {
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
        public TextView tvAppUninstall;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AppHolder holder=null;
        if(convertView==null){
            holder=new AppHolder();
            convertView=inflater.inflate(R.layout.item_app_list,parent,false);
            holder.imAppIcon=(ImageView) convertView.findViewById(R.id.app_icon);
            holder.tvAppName=(TextView) convertView.findViewById(R.id.app_name);
            holder.tvAppSpace=(TextView) convertView.findViewById(R.id.app_size);
            holder.tvAppUninstall=(TextView) convertView.findViewById(R.id.btn_uninstall);
            convertView.setTag(holder);
        }else{
            holder=(AppHolder) convertView.getTag();
        }
        final AppInfo appInfo=appsList.get(position);
        holder.imAppIcon.setBackgroundDrawable(appInfo.drawable);
        holder.tvAppName.setText(appInfo.appName);
        holder.tvAppSpace.setText("版本："+appInfo.appVersion);
        switch (appInfo.status) {
            case MzsConstant.UninstallStatus.STATUS_DEFAULT:
                holder.tvAppUninstall.setText("卸载");
                holder.tvAppUninstall.setEnabled(true);
                break;
            case MzsConstant.UninstallStatus.STATUS_UNINSTALLING:
                holder.tvAppUninstall.setText("卸载中");
                holder.tvAppUninstall.setEnabled(false);
                break;
            case MzsConstant.UninstallStatus.STATUS_UNINSTALLED:
                holder.tvAppUninstall.setText("完成");
                holder.tvAppUninstall.setEnabled(false);
                break;
        }
        holder.tvAppUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=((TextView)v);
                uninstallAPK(position, appInfo.packageName);
                appInfo.status=MzsConstant.UninstallStatus.STATUS_UNINSTALLING;
                tv.setText("卸载中");
                tv.setEnabled(false);
            }
        });
        return convertView;
    }
    
    
    private void uninstallAPK(final int position,final String pkgName){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.arg1) {
                    case PackageUtils.DELETE_SUCCEEDED:
                        UIUtils.showShortToast("卸载成功");
                        appsList.remove(position);
                        notifyDataSetChanged();
                        break;
                    default:
                        UIUtils.showShortToast("卸载失败");
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result=PackageUtils.uninstall(context, pkgName);
                Message msg=handler.obtainMessage();
                msg.arg1=result;
                msg.sendToTarget();
            }
        }).start();
    }
}
