package com.iflytek.sdk.server.websocket;


import io.netty.channel.Channel;

public interface ServerListener {

    public void doAction(String msg, Channel channel);
    }

