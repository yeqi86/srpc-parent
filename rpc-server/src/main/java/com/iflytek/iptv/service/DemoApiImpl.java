package com.iflytek.iptv.service;

import com.iflytek.iptv.api.DemoApi;
import com.iflytek.iptv.dto.EventMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DemoApiImpl implements DemoApi {
    @Override
    public String sendMsg(EventMsg msg) {
        System.out.println(msg.getMsg()+"|"+msg.getName());
        return "收到请求";
    }
}
