package com.iflytek.iptv.service;

import com.iflytek.iptv.api.ClientCallBackApi;
import com.iflytek.iptv.dto.EventMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientCallBackServiceImpl implements ClientCallBackApi {


    @Override
    public String callbackMsg(EventMsg msg) {
        log.info("msg:{}",msg);
        return "收到信息";
    }
}
