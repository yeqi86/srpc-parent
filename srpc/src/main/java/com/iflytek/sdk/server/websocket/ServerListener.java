package com.iflytek.sdk.server.websocket;


import io.netty.channel.Channel;
/**
 * C@author qiye2
 *
 */
public interface ServerListener {

    public void doAction(String msg, Channel channel);
    }

