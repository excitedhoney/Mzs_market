
package com.mzs.market.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetUtils {

    public static String getString(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName) || context == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while (br.ready()) {
                sb.append(br.readLine());
            }
        } catch (IOException e) {
            Log.e("wh", "catch exception in read assets", e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e("wh", "close inputstream exception", e);
            }
        }
        return sb.toString();
    }

}
