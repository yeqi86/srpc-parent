package com.iflytek.iptv.dto;


import com.iflytek.sdk.protocol.Serialize;

import java.io.Serializable;

public class EventMsg implements Serializable {
    private String name;
    private String msg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
