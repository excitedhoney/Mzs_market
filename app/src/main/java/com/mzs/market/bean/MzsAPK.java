package com.mzs.market.bean;

import java.io.Serializable;

public class MzsAPK implements Serializable{
    private static final long serialVersionUID = 6112167435080257502L;
    public int apkId;
    public String nick;
    public String name;
    public String iconUrl;
    public String downloadUrl;
    public String size;
    public String desc;
    public int downloadCount;
    public int status;
    public int type;
    
    /*
     * 下载状态使用参数
     */
    public int downloadStatus;
    public int progress;
    public long totalSize;
    public long currentSize;
    public int speed;
    
}
