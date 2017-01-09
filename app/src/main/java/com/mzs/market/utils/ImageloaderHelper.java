
package com.mzs.market.utils;

import com.mzs.market.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.content.Context;
import android.graphics.Bitmap;

public class ImageloaderHelper {

    public static void init(Context context) {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

    public static DisplayImageOptions getOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnLoading(R.drawable.pp_icon_default)
        .showImageForEmptyUri(R.drawable.pp_icon_default)
        .showImageOnFail(R.drawable.pp_icon_default)
        .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }
    

}
