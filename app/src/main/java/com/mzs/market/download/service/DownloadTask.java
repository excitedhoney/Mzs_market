
package com.mzs.market.download.service;

import com.mzs.market.MzsApplication;
import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.config.ErrorCode;
import com.mzs.market.config.MzsConstant;
import com.mzs.market.download.db.DownloadDao;
import com.mzs.market.exception.DownloadException;
import com.mzs.market.utils.FileUtils;
import com.mzs.market.utils.MzsStorageUtils;
import com.mzs.market.utils.NetworkUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import android.accounts.NetworkErrorException;

public class DownloadTask implements Runnable {

    private static final String TEMP_SUFFIX = ".download";

    private static final int BUFFER_SIZE = 8 * 1024;

    private DownloadTaskListener mListener;

    private String mUrl;

    private File mFile;

    private File mTempFile;

    /** 文件大小 */
    private long mTotalSize;

    /** 之前已经没有下载完的文件大小 */
    private long mPreviousFileSize;

    /** 下载的大小 */
    private long mDownloadSize;

    /** 下载百分比 */
    private long mDownloadPercent;

    /** 下载速度 */
    private long mDownloadSpeed;

    /** 开始下载的时间 */
    private long mStartTime;

    private volatile boolean mInterrupt = false;

    private DownloadDao mDao;

    public static final int TIME_OUT = 1000 * 20;

    public DownloadTask(DownloadInfo info, DownloadDao mDao, DownloadTaskListener l) {
        this.mUrl = info.url;
        this.mListener = l;
        this.mDao = mDao;
        String name = FileUtils.getFileName(mUrl);
        mFile = new File(MzsConstant.DOWNLOAD_PATH, name);
        mTempFile = new File(MzsConstant.DOWNLOAD_PATH, name + TEMP_SUFFIX);
        info.savedPath = MzsConstant.DOWNLOAD_PATH;
        mDao.save(info);
    }

    @Override
    public void run() {
        // 下载开始
        onPreDownload();
        long result = -1;
        try {
            result = download();
        } catch (IOException e) {
            downloadError(ErrorCode.IO_ERROR);
        } catch (DownloadException e) {
            downloadError(e.type);
        }
        onPostExecute(result);
    }

    protected void onPreDownload() {
        mInterrupt = false;
        mStartTime = System.currentTimeMillis();
        if (mListener != null) {
            mListener.startDownload(mUrl);
        }
    }

    protected void onPostExecute(long result) {
        if (result == -1 || mInterrupt) {
            return;
        }
        mTempFile.renameTo(mFile);
        mDao.updateStatusByUrl(mUrl, MzsConstant.Status.STATUS_COMPLETE);
        if (mListener != null) {
            mListener.finishDownload(mUrl);
        }
    }

    private void downloadError(int type) {
        // 下载过程中遇到错误就重置下载状态
        pause();
        if (mListener != null) {
            mListener.errorDownload(mUrl, type);
        }
    }

    public void pause() {
        mInterrupt = true;
        mDao.updateStatusByUrl(mUrl, MzsConstant.Status.STATUS_PAUSE);
        if (mListener != null) {
            mListener.pauseTask(mUrl);
        }
    }

    public void delete() {
        mInterrupt = true;
        mDao.deleteByUrl(mUrl);
        if (mListener != null) {
            mListener.deleteTask(mUrl);
        }
    }

    protected void onProgressUpdate(int size) {
        mDownloadSize = size;
        long totalTime = System.currentTimeMillis() - mStartTime;
        long tempSize = mDownloadSize + mPreviousFileSize;
        mDownloadSpeed = mDownloadSize / totalTime;// kbps
        int temp = (int) (tempSize * 100 / mTotalSize);
        if (mDownloadPercent != temp) {
            mDownloadPercent = temp;
            mDao.updateCurrentSizeByUrl(mUrl, tempSize); // 更新最新进度
            if (mListener != null) {
                mListener.updateProgress(mUrl, mTotalSize, tempSize, temp, (int) mDownloadSpeed);
            }
        }
    }

    /**
     * 初始化保存totalSize
     */
    private void saveTotalSize() {
        mDao.updateStatusByUrl(mUrl, MzsConstant.Status.STATUS_DOWNLOADING);
        mDao.updateTotalSizeByUrl(mUrl, mTotalSize);
    }

    /**
     * 下载主函数
     * 
     * @return
     * @throws NetworkErrorException
     * @throws IOException
     * @throws DownloadException
     */
    private long download() throws IOException, DownloadException {
        if (!NetworkUtils.isNetworkAvailable(MzsApplication.APP)) { // 检查网络状态
            throw new DownloadException(ErrorCode.NO_INTERNET);
        }
        mTotalSize = mDao.getTotalSizeByUrl(mUrl);
        if (mTotalSize > 0) { // 继续下载
            if (mFile.exists() && mFile.length() == mTotalSize) {
                throw new DownloadException(ErrorCode.FILE_IS_EXISTS);
            } else if (mTempFile.exists() && mTempFile.length() > 0
                    && mTempFile.length() < mTotalSize) {// 已经下载过了，断点下载
                mPreviousFileSize = mTempFile.length() - 1;
            } else { // 文件大小不对，自动纠正
                mTempFile.delete();
                mPreviousFileSize = 0;
            }
        } else {
            mPreviousFileSize = 0;
        }
        HttpURLConnection connection = getNetWorkConnect(mUrl, mPreviousFileSize);
        connection.connect();
        if (mTotalSize == 0) {
            mTotalSize = connection.getContentLength();
        }
        if (mTotalSize < 1024) {
            throw new DownloadException(ErrorCode.ERROR_URL);
        }
        long storage = MzsStorageUtils.getAvailableStorage();
        if (mTotalSize - mTempFile.length() > storage) { // 没有足够的空间
            throw new DownloadException(ErrorCode.NO_MEMORY);
        }
        saveTotalSize();
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream(), BUFFER_SIZE);
        RandomAccessFile accessFile = new RandomAccessFile(mTempFile, "rw");
        byte[] buffer = new byte[BUFFER_SIZE];
        int totalCount = 0;
        int readCount = 0;
        try {
            accessFile.seek(mPreviousFileSize);
            while (!mInterrupt && (readCount = bis.read(buffer)) != -1) {
                accessFile.write(buffer, 0, readCount);
                totalCount += readCount;
                onProgressUpdate(totalCount);
            }
        } finally {
            bis.close();
            accessFile.close();
            connection.disconnect();
        }
        return mInterrupt ? -1 : totalCount;
    }

    private HttpURLConnection getNetWorkConnect(String path, long range) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);
        connection.setRequestProperty("Accept", MzsConstant.CONNECT_ACCEPT);
        connection.setRequestProperty("Accept-Language", "zh-CN");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("User-Agent", MzsConstant.USER_AGENT);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.addRequestProperty("Range", "bytes=" + range + "-");
        connection.connect();
        return connection;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getDownloadPercent() {
        return mDownloadPercent;
    }

    public long getDownloadSpeed() {
        return mDownloadSpeed;
    }

    public long getDownloadSize() {
        return mDownloadSize + mPreviousFileSize;
    }

}
