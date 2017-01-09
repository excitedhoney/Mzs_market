
package com.mzs.market.utils;

import com.mzs.market.config.MzsConstant;

import java.io.File;
import java.text.DecimalFormat;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class MzsStorageUtils {

    private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;

    @SuppressWarnings("deprecation")
    public static long getAvailableStorage() {
         String storageDirectory = Environment.getExternalStorageDirectory().toString();
        try {
            StatFs stat = new StatFs(storageDirectory);

            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    public static boolean checkAvailableStorage() {
        if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
            return false;
        }
        return true;
    }

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void mkdir(){
        File file = new File(MzsConstant.DOWNLOAD_PATH);
        if (!file.exists()) {
        	file.mkdirs();
        }
    }

    public static String size(long size) {
        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "MB";
        } else if (size / 1024 > 0) {
            return "" + (size / (1024)) + "KB";
        } else
            return "" + size + "B";
    }

    public static void installAPK(Context context, final String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fileName = MzsConstant.DOWNLOAD_PATH + NetworkUtils.getFileNameFromUrl(url);
        intent.setDataAndType(Uri.fromFile(new File(fileName)),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.packageinstaller",
                "com.android.packageinstaller.PackageInstallerActivity");
        context.startActivity(intent);
    }
    
    public static void openAPK(PackageManager pm, String packageName, Context context) {
    	Intent intent = pm.getLaunchIntentForPackage(packageName);
    	context.startActivity(intent);
    }

    public static boolean delete(File path) {
        boolean result = true;
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File child : path.listFiles()) {
                    result &= delete(child);
                }
                result &= path.delete(); // Delete empty directory.
            }
            if (path.isFile()) {
                result &= path.delete();
            }
            if (!result) {
                Log.e(null, "Delete failed;");
            }
            return result;
        } else {
            Log.e(null, "File does not exist.");
            return false;
        }
    }
}
