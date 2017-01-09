
package com.mzs.market.download.service;

import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.download.db.DownloadDao;
import com.mzs.market.utils.MzsStorageUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.text.TextUtils;
import android.util.Log;

/**
 * @category 下载队列管理
 * @author 王浩
 * @date 2014/12/21 AM 11:45
 */
public class DownloadController extends Thread {

    private static final String TAG = "DownloadController";

    private static final int MAX_DOWNLOAD_THREAD_COUNT = 2;

    /** 正在下载的任务 */
    private ConcurrentLinkedQueue<DownloadTask> mDownloadingTasks;

    /** 已经暂停的任务 */
    private ConcurrentLinkedQueue<DownloadTask> mPausedTasks;

    /** 等待状态的任务 */
    private ConcurrentLinkedQueue<DownloadTask> mWaitedTasks;

    private volatile boolean isRunning = true;

    private static final Object mlock = new Object();

    private DownloadTaskListener listener;

    private ExecutorService pool = Executors.newCachedThreadPool();

    private DownloadDao mDao;

    public DownloadController() {
        mDownloadingTasks = new ConcurrentLinkedQueue<DownloadTask>();
        mPausedTasks = new ConcurrentLinkedQueue<DownloadTask>();
        mWaitedTasks = new ConcurrentLinkedQueue<DownloadTask>();
        mDao = new DownloadDao();
        MzsStorageUtils.mkdir();
    }

    public void setDownloadListener(DownloadTaskListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            DownloadTask task = null;
            if ((task = mWaitedTasks.peek()) != null) { // 有任务等待
                if (mDownloadingTasks.size() < MAX_DOWNLOAD_THREAD_COUNT) { // 下载队列小于3个
                    boolean removeFromWait = mWaitedTasks.remove(task);
                    boolean success = mDownloadingTasks.offer(task);
                    Log.i(TAG, "从等待队列移除 ：" + removeFromWait + "   加入下载是否成功 : " + success);
                    pool.execute(task);
                } else { // 没有
                    Log.i(TAG, "下载队列已满，任务等待中");
                    if (listener != null) {
                        listener.waitDownload(task.getUrl());
                    }
                }
            } else {
                Log.i(TAG, "等待队列没有任务");
            }
            Log.d(TAG, "download : " + mDownloadingTasks + "  wait : " + mWaitedTasks
                    + "  pause : " + mPausedTasks);
            try {
                synchronized (mlock) {
                    mlock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 退出所有
     */
    public void exit() {
        for (DownloadTask task : mDownloadingTasks) {
            task.pause();
        }
        for (DownloadTask task : mWaitedTasks) {
            task.pause();
        }
        mDownloadingTasks.clear();
        mPausedTasks.clear();
        mWaitedTasks.clear();
        isRunning = false;
        pool.shutdown();
        notifyManagerThread();
    }

    private void notifyManagerThread() {
        synchronized (mlock) {
            mlock.notify();
        }
    }

    private DownloadTask newDownloadThread(DownloadInfo info) {
        return new DownloadTask(info, mDao, new DownloadTaskListener() {
            @Override
            public void waitDownload(String url) {
                if (listener != null) {
                    listener.waitDownload(url);
                }
            }

            @Override
            public void updateProgress(String url, long totalSize, long currentSize, int progress,
                    int speed) {
                if (listener != null) {
                    listener.updateProgress(url, totalSize, currentSize, progress, speed);
                }
            }

            @Override
            public void startDownload(String url) {
                if (listener != null) {
                    listener.startDownload(url);
                }
            }

            @Override
            public void finishDownload(String url) {
                if (listener != null) {
                    listener.finishDownload(url);
                }
                DownloadTask task = null;
                for (DownloadTask t : mDownloadingTasks) {
                    if (t.getUrl().equals(url)) {
                        task = t;
                        break;
                    }
                }
                if (task != null) {
                    boolean removeSuccess = mDownloadingTasks.remove(task);
                    Log.i(TAG, "下载完成，从下载列表移除 ： " + removeSuccess);
                } else {
                    Log.i(TAG, "下载完成，未在下载列表找到任务");
                }
                notifyManagerThread();
            }

            @Override
            public void errorDownload(String url, int type) {
                if (listener != null) {
                    listener.errorDownload(url, type);
                }
                DownloadTask task = null;
                for (DownloadTask t : mDownloadingTasks) {
                    if (t.getUrl().equals(url)) {
                        task = t;
                        break;
                    }
                }
                if (task != null) {
                    boolean removeSuccess = mDownloadingTasks.remove(task);
                    boolean offerSuccess = mPausedTasks.offer(task);
                    Log.i(TAG, "出错了，从下载列表移除 ： " + removeSuccess + "  加入暂停队列 ：" + offerSuccess);
                } else {
                    Log.i(TAG, "出错了，未在下载列表找到任务");
                }
                notifyManagerThread();
            }
            @Override
            public void pauseTask(String url) {
                listener.pauseTask(url);
            }
            @Override
            public void deleteTask(String url) {
                listener.deleteTask(url);
            }
        });
    }

    private boolean isTaskAdded(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        for (DownloadTask task : mDownloadingTasks) {
            if (task.getUrl().equals(url)) {
                return true;
            }
        }
        for (DownloadTask task : mPausedTasks) {
            if (task.getUrl().equals(url)) {
                return true;
            }
        }
        for (DownloadTask task : mWaitedTasks) {
            if (task.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    /** 添加一个下载任务 */
    public void addTask(DownloadInfo info) {
        if (isTaskAdded(info.url))
            return;
        DownloadTask task = newDownloadThread(info);
        mWaitedTasks.offer(task);
        notifyManagerThread();
    }

    /** 暂停下载任务 */
    public void pauseTask(String url) {
        DownloadTask task = null;
        for (DownloadTask t : mDownloadingTasks) {
            if (t.getUrl().equals(url)) {
                task = t;
                break;
            }
        }
        if (task != null) { // 找到任务
            boolean isDownSuccess = mDownloadingTasks.remove(task);
            boolean isPausedSuccess = mPausedTasks.offer(task);
            task.pause();
            Log.i(TAG, "暂停任务： 从下载队列移除 ： " + isDownSuccess + "  放入暂停队列 ： " + isPausedSuccess);
        } else {
            Log.e(TAG, "未在下载队列找到task");
        }
        notifyManagerThread();
    }

    /** 删除一个任务 */
    public void deleteTask(String url) {
        DownloadTask task = null;
        for (DownloadTask t : mDownloadingTasks) {
            if (t.getUrl().equals(url)) {
                task = t;
                break;
            }
        }
        if (task != null) {
            mDownloadingTasks.remove(task);
            task.delete();
            notifyManagerThread();
            return;
        }
        for (DownloadTask t : mPausedTasks) {
            if (t.getUrl().equals(url)) {
                task = t;
                break;
            }
        }
        if (task != null) {
            mPausedTasks.remove(task);
            task.delete();
            notifyManagerThread();
            return;
        }

        for (DownloadTask t : mWaitedTasks) {
            if (t.getUrl().equals(url)) {
                task = t;
                break;
            }
        }
        if (task != null) {
            mWaitedTasks.remove(task);
            task.delete();
            notifyManagerThread();
            return;
        }
    }

    public void continueTask(DownloadInfo info) {
        DownloadTask task = null;
        for (DownloadTask t : mPausedTasks) {
            if (t.getUrl().equals(info.url)) {
                task = t;
                break;
            }
        }
        if (task != null) { // 已在暂停队列
            mPausedTasks.remove(task);
            mWaitedTasks.offer(task);
            notifyManagerThread();
        } else { // 未在暂停列表，重新创建
            addTask(info);
        }
    }
    
    
    public int getDownloadingSize(){
        return mDownloadingTasks.size();
    }
    
}
