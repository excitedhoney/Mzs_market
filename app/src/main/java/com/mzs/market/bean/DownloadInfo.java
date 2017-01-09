
package com.mzs.market.bean;

import com.mzs.market.config.MzsConstant;

public class DownloadInfo {
	
	public DownloadInfo(String url,String name,String iconUrl){
		this.url=url;
		this.name=name;
		this.iconUrl=iconUrl;
	}
	
	public DownloadInfo(){}
	
	public String url;
	public String name;
	public String savedPath;
	public long totalSize;
	public long currentSize;
	public String  iconUrl;
	public int status = MzsConstant.Status.STATUS_DEFAULT;
	
	/**
	 *  0 : downloadInfo  1：下载中label  2 : 下载完成label
	 */
	public int item_type;
	
	
	/**
	 * 将apk信息转为下载info
	 * 
	 * @param apk
	 * @return
	 */
	public static DownloadInfo convertInfo(MzsAPK apk) {
		if (apk == null)
			return null;
		DownloadInfo info = new DownloadInfo(apk.downloadUrl, apk.nick,
				apk.iconUrl);
		return info;
	}
}
