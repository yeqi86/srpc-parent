package com.iflytek.iptv.service;

import com.iflytek.sdk.client.websocket.BaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class SimpleListener implements BaseListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleListener.class);

    /**
     * 一个简单的Listener方法
     * @param event Guava规定此处只能有一个参数
     */

    @Override
    public void doAction(final String event){
        if (LOGGER.isInfoEnabled()){
            LOGGER.info("Received event [{}] and will take a action", event);
        }
    }
}

