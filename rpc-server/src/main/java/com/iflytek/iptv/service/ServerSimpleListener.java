package com.iflytek.iptv.service;

import com.iflytek.sdk.server.websocket.NettyWebSocket;
import com.iflytek.sdk.server.websocket.ServerListener;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ServerSimpleListener implements ServerListener {
    @Override
    public void doAction(String msg, Channel channel) {
        log.info(msg+":"+msg);
        TextWebSocketFrame tws = new TextWebSocketFrame("收到信息");
        channel.writeAndFlush(tws);
        TextWebSocketFrame tws2 = new TextWebSocketFrame("大家都收到信息");
        NettyWebSocket.send2All(tws2);
    }
}
