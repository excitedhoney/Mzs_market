
package com.mzs.market.download.manager;

import com.mzs.market.config.AppConfig;
import com.mzs.market.download.service.DownloadService;
import com.mzs.market.download.service.DownloadTaskListener;
import android.util.Log;
import java.util.ArrayList;

public class DownloadObserverAdapter {

    private final ArrayList<DownloadTaskListener> mObservers = new ArrayList<DownloadTaskListener>();

    private static DownloadObserverAdapter mInstance;

    private DownloadService mService;

    private DownloadObserverAdapter() {
    }

    public static DownloadObserverAdapter getInstance() {
        if (mInstance == null) {
            synchronized (DownloadObserverAdapter.class) {
                if (mInstance == null)
                    mInstance = new DownloadObserverAdapter();
            }
        }
        return mInstance;
    }

    public static void registerObserver(DownloadTaskListener observer) {
        DownloadObserverAdapter.getInstance().registerDownloadObserver(observer);
    }

    public static void unregisterObserver(DownloadTaskListener observer) {
        DownloadObserverAdapter.getInstance().unregisterDownloadObserver(observer);
    }

    public void setDownloadService(DownloadService mService) {
        this.mService = mService;
        if (mService != null) {
            mService.setDownloadTaskListener(listener);
        }
    }

    public DownloadService getDownloadService() {
        return this.mService;
    }

    private DownloadTaskListener listener = new DownloadTaskListener() {
        @Override
        public void waitDownload(final String url) {
            notifyWaitDownload(url);
        }

        @Override
        public void updateProgress(String url, long totalSize, long currentSize,
                final int progress, final int speed) {
            notifyUpdateProgress(url, totalSize, currentSize, progress, speed);
        }

        @Override
        public void startDownload(String url) {
            notifyStartDownload(url);
        }

        @Override
        public void finishDownload(String url) {
            notifyFinishDownload(url);
        }

        @Override
        public void errorDownload(final String url, final int type) {
            notifyErrorDownload(url, type);
        }
        @Override
        public void pauseTask(String url) {
            notifyPauseDownload(url);
        }
        @Override
        public void deleteTask(String url) {
            notifyDeleteDownload(url);
        }
    };

    /**
     * 注册联网观察者
     * 
     * @param observer
     */
    public void registerDownloadObserver(DownloadTaskListener observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        Log.d("wh", "registerDownloadObserver : " + observer);
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                Log.w("wh", observer + "observer is already registered");
                return;
            }
            mObservers.add(observer);
        }
    }

    /**
     * 注销联网观察者
     * 
     * @param observer
     */
    public void unregisterDownloadObserver(DownloadTaskListener observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        Log.d("wh", "unregisterDownloadObserver : " + observer);
        synchronized (mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                Log.w("wh", observer + "observer is not register");
                return;
            }
            mObservers.remove(index);
        }
    }

    /**
     * 注销所有联网观察者
     */
    public void unregisterAllObserver() {
        synchronized (mObservers) {
            mObservers.clear();
        }
    }

    private void notifyStartDownload(final String url) {
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).startDownload(url);
                    }
                }
            });
        }
    }

    private void notifyUpdateProgress(final String url, final long totalSize,
            final long currentSize, final int progress, final int speed) {
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).updateProgress(url, totalSize, currentSize, progress,
                                speed);
                    }
                }
            });
        }
    }

    private void notifyFinishDownload(final String url) {
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).finishDownload(url);
                    }
                }
            });
        }
    }

    private void notifyErrorDownload(final String url, final int type) {
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).errorDownload(url, type);
                    }
                }
            });
        }
    }

    private void notifyWaitDownload(final String url) {
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).waitDownload(url);
                    }
                }
            });
        }
    }
    
    private void notifyPauseDownload(final String url){
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).pauseTask(url);
                    }
                }
            });
        }
    }
    
    private void notifyDeleteDownload(final String url){
        synchronized (mObservers) {
            AppConfig.uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).deleteTask(url);
                    }
                }
            });
        }
    }
    
}
