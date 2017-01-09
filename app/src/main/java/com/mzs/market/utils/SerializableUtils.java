
package com.mzs.market.utils;

import com.mzs.market.config.MzsConstant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.text.TextUtils;
import android.util.Log;

public class SerializableUtils {

    public static void writeSerToFile(Serializable object, String file) {
        if (TextUtils.isEmpty(file) || null == object)
            return;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file, false));
            oos.writeObject(object);
        } catch (Exception e) {
            Log.e(MzsConstant.LOG_TAG, "write object exception ! ");
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e2) {
                Log.e(MzsConstant.LOG_TAG, "close stream exception ! ");
            }
        }
    }

    public static Serializable readSerFromFile(String file) {
        if (TextUtils.isEmpty(file))
            return null;
        ObjectInputStream bis = null;
        Object o = null;
        try {
            bis = new ObjectInputStream(new FileInputStream(file));
            o = bis.readObject();
        } catch (Exception e) {
            Log.e(MzsConstant.LOG_TAG, "read object exception ! ");
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                Log.e(MzsConstant.LOG_TAG, "close stream exception ! ");
            }
        }
        return null != o ? (Serializable) o : null;
    }

}
