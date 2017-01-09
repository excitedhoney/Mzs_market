
package com.mzs.market.exception;


public class DownloadException extends Exception {

    private static final long serialVersionUID = 1L;

    public int type;

    public static final String[] ERROR_MSGS = {
            "I/O错误", "网络连接超时", "错误的URL", "没有网络连接", "存储空间不足", "SD卡不可用", "文件已存在"
    };

    public DownloadException(int type) {
        super(ERROR_MSGS[type]);
        this.type = type;
    }
}
