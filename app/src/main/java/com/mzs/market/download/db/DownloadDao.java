
package com.mzs.market.download.db;

import com.mzs.market.bean.DownloadInfo;
import com.mzs.market.config.MzsConstant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DownloadDao {

    private SQLiteDatabase mDb;

    public DownloadDao() {
        mDb = DownloadDBHelper.getInstance();
    }

    public void save(DownloadInfo info) {
        if (!exist(info.url)) {
            ContentValues cv = new ContentValues();
            cv.put("url", info.url);
            cv.put("name", info.name);
            cv.put("saved_path", info.savedPath);
            cv.put("total_size", info.totalSize);
            cv.put("current_size", info.currentSize);
            cv.put("icon_url", info.iconUrl);
            cv.put("status", info.status);
            synchronized (mDb) {
                mDb.insert(DownloadDBHelper.TABLE_DOWNLOAD, null, cv);
            }
        }
    }

    public ArrayList<DownloadInfo> queryAllDownloads() {
        ArrayList<DownloadInfo> infos = new ArrayList<DownloadInfo>();
        synchronized (mDb) {
            Cursor cursor = null;
            try {
                cursor = mDb.query(DownloadDBHelper.TABLE_DOWNLOAD, null, null, null, null, null,
                        null);
                while (cursor.moveToNext()) {
                    DownloadInfo info = new DownloadInfo();
                    info.url = cursor.getString(1);
                    info.name = cursor.getString(2);
                    info.savedPath = cursor.getString(3);
                    info.totalSize = cursor.getInt(4);
                    info.currentSize = cursor.getInt(5);
                    info.status = cursor.getInt(6);
                    info.iconUrl = cursor.getString(7);
                    infos.add(info);
                }
            } catch (Exception e) {
                Log.e(MzsConstant.LOG_TAG, "查询下载列表出错", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return infos;
    }

    public void updateTotalSizeByUrl(String url, long totalSize) {
        ContentValues cv = new ContentValues();
        cv.put("total_size", totalSize);
        synchronized (mDb) {
            mDb.update(DownloadDBHelper.TABLE_DOWNLOAD, cv, "url=?", new String[] {
                    url
            });
        }
    }

    public void updateCurrentSizeByUrl(String url, long currentSize) {
        ContentValues cv = new ContentValues();
        cv.put("current_size", currentSize);
        synchronized (mDb) {
            mDb.update(DownloadDBHelper.TABLE_DOWNLOAD, cv, "url=?", new String[] {
                    url
            });
        }
    }

    public synchronized void updateStatusByUrl(String url, int status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        synchronized (mDb) {
            mDb.update(DownloadDBHelper.TABLE_DOWNLOAD, cv, "url=?", new String[] {
                    url
            });
        }
    }

    public int getStatusByUrl(String url) {
        String sql = "select status from " + DownloadDBHelper.TABLE_DOWNLOAD + " where url=?";
        int status = 0;
        synchronized (mDb) {
            Cursor cursor = mDb.rawQuery(sql, new String[] {
                    url
            });
            if (cursor.moveToFirst()) {
                status = cursor.getInt(0);
            }
            cursor.close();
        }
        return status;
    }
    
    public long getTotalSizeByUrl(String url){
        String sql = "select total_size from " + DownloadDBHelper.TABLE_DOWNLOAD + " where url=?";
        long totalSize = 0;
        synchronized (mDb) {
            Cursor cursor = mDb.rawQuery(sql, new String[] {
                    url
            });
            if (cursor.moveToFirst()) {
                totalSize = cursor.getLong(0);
            }
            cursor.close();
        }
        return totalSize;
    }
    

    public boolean exist(String url) {
        String sql = "select * from " + DownloadDBHelper.TABLE_DOWNLOAD + " where url=?";
        boolean exist = false;
        synchronized (mDb) {
            Cursor cursor = mDb.rawQuery(sql, new String[] {
                    url
            });
            if (cursor.moveToFirst()) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    public void deleteByUrl(String url) {
        synchronized (mDb) {
            mDb.delete(DownloadDBHelper.TABLE_DOWNLOAD, "url=?", new String[] {
                    url
            });
        }
    }

}
