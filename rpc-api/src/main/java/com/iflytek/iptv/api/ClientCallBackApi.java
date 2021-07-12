package com.iflytek.iptv.api;

import com.iflytek.iptv.dto.EventMsg;

public interface ClientCallBackApi {

    public  String  callbackMsg(EventMsg msg);
}
