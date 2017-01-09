
package com.mzs.market.net;

import com.mzs.market.config.MzsConstant;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtils {

    public static String encodeUrl(String sub_url) {
        return encodeUrl(sub_url, null);
    }

    public static String encodeUrl(String sub_url, ParamsList params) {
        StringBuilder result = new StringBuilder(MzsConstant.SERVER_URL);
        result.append(sub_url);
        if (params != null) {
            result.append('?').append(encodeParams(params));
        }
        return result.toString();
    }

    public static String encodeGetUrl(String sub_url, ParamsList params) {
        StringBuilder result = new StringBuilder(sub_url);
        if (params != null) {
            result.append('?').append(encodeParams(params));
        }
        return result.toString();
    }
    
    private static String encodeParams(ParamsList params) {
        StringBuilder sb = new StringBuilder();
        int _i = 0;
        for (RequestParams param : params) {
            if (_i > 0)
                sb.append("&");
            sb.append(param.name).append("=");
            try {
                sb.append(URLEncoder.encode(param.value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            _i++;
        }
        return sb.toString();
    }
}
