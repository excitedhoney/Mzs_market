
package com.mzs.market.download.service;

public interface DownloadTaskListener {
    public void startDownload(String url);
    public void updateProgress(String url,long totalSize,long currentSize,int progress,int speed);
    public void finishDownload(String url);
    public void errorDownload(String url, int type);
    public void waitDownload(String url);
    public void pauseTask(String url);
    public void deleteTask(String url);
}
