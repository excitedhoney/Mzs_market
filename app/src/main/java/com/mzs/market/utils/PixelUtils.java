package com.mzs.market.utils;

import android.content.Context;

import com.mzs.market.config.AppConfig;

public class PixelUtils {

    public static int dip2px(float dipValue) {
        final float scale = AppConfig.density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = AppConfig.density;
        return (int)(pxValue / scale + 0.5f);
    }
}
