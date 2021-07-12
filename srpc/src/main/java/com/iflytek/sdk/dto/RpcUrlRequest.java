package com.iflytek.sdk.dto;

import java.io.Serializable;

/**
 * Created by qiye on 2019/11/2.
 */
public class RpcUrlRequest implements Serializable {
    private static final long serialVersionUID = -3432433811350669121L;
    public RpcUrlRequest( Class<?> clazz, String url) {
        this.clazz = clazz;
        this.url = url;
    }

    Class<?> clazz;
    String url;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
