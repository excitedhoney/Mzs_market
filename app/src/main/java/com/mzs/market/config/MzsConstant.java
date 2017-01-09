package com.mzs.market.config;

import android.os.Environment;

public class MzsConstant {

    public static final String LOG_TAG="wh";
    
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/";
    public static final String DOWNLOAD_PATH = ROOT_PATH + "mzs_market/download/";
    
    public static final String CONNECT_ACCEPT="image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
    
    public static final String USER_AGENT="Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
    
    /**
     * APK下载状态
     */
    public static class Status {
        public static final int STATUS_DEFAULT = 0;
        public static final int STATUS_DOWNLOADING = 1;
        public static final int STATUS_PAUSE = 2;
        public static final int STATUS_WAIT = 3;
        public static final int STATUS_COMPLETE = 4;
        public static final int STATUS_INSTALLING=5;
        public static final int STATUS_INSTALLED=6;
        public static final int STATUS_ERROR=7;
    }
    
    /**
     * apk卸载状态
     */
    public static class UninstallStatus{
        public static final int STATUS_DEFAULT=0;
        public static final int STATUS_UNINSTALLED=1;
        public static final int STATUS_UNINSTALLING=2;
        public static final int STATUS_INSTALLING=3;
        public static final int STATUS_DELETEING=4;
    }
    
    
    //获取数据的方式
    public static final int REQUEST_INIT=0;
    public static final int REQUEST_REFRESH=1;
    public static final int REQUEST_MORE=2;
    
    //服务器地址
    public static final String SERVER_URL="http://www.muzhishow.com:63660/index.php/home/Android/";
    //各个接口的子接口名
    public static final String CHOICE_LIST="featureList";
    public static final String ADS_IMG="adsImage";
    public static final String MUST_HAVE_LIST="mustHave";
    public static final String ONLINE_LIST="online";
    public static final String RANK_LIST="";
    public static final String GAME_DETAIL="gamedetails";
    public static final String FD_FEEDBACK = "feedback";	//反馈

    
    
}
