
package com.mzs.market.net;

public class RequestParams {
    
    public String value;
    public String name;

    public RequestParams(String name, String value) {
        super();
        if (name == null || value == null)
            throw new RuntimeException("args can not be null");
        this.name = name;
        this.value = value;
    }

}
