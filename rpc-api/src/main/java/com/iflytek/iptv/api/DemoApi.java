package com.iflytek.iptv.api;

import com.iflytek.iptv.dto.EventMsg;
import com.iflytek.sdk.annotation.RpcService;

@RpcService
public interface DemoApi {


    public  String  sendMsg(EventMsg msg);

}
