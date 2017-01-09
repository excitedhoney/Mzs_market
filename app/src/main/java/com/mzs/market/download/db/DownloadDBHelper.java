
package com.mzs.market.download.db;

import com.mzs.market.MzsApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "mzs_download.db";

    public static final String TABLE_DOWNLOAD = "downloads";

    private static final String CREATE_DOWNLOAD = "create table " + TABLE_DOWNLOAD
            + "(_id integer primary key autoincrement,"
            + "url text, name text, saved_path text, total_size integer, "
            + "current_size integer, status integer,icon_url text)";

    private volatile static SQLiteDatabase mDb = null;

    public DownloadDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SQLiteDatabase getInstance() {
        if (mDb == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (mDb == null) {
                    mDb = new DownloadDBHelper(MzsApplication.APP).getWritableDatabase();
                }
            }
        }
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DOWNLOAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void closeDb() {
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
            mDb = null;
        }
    }
}
