
package com.mzs.market.download.service;

import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.db.DownloadDBHelper;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class DownloadService extends Service {

    private DownloadController mControl;
    private DownloadTaskListener downloadTaskListener;
    
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceStub();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("wh", "service oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    
    
    
    public int getDownloadingSize(){
        return mControl.getDownloadingSize();
    }
    

    @Override
    public void onDestroy() {
        if (mControl != null) {
            mControl.exit();
        }
        DownloadDBHelper.closeDb();
    }

    public void setDownloadTaskListener(DownloadTaskListener listener) {
        this.downloadTaskListener = listener;
        mControl = new DownloadController();
        mControl.setDownloadListener(downloadTaskListener);
        mControl.start();
    }

    public void addTask(DownloadInfo info) {
        if (downloadTaskListener == null) {
            Log.e(MzsConstant.LOG_TAG,
                    "you must set downloadTaskListener first");
            return;
        }
        if (info != null) {
            mControl.addTask(info);
        }
    }

    public void continueTask(DownloadInfo info) {
        mControl.continueTask(info);
    }

    public void removeTask(String url) {
        if (!TextUtils.isEmpty(url)) {
            mControl.deleteTask(url);
        }
    }

    public void pauseTask(String url) {
        if (!TextUtils.isEmpty(url)) {
            mControl.pauseTask(url);
        }
    }

    public class ServiceStub extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

}
