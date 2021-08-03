package com.iflytek.sdk.client.websocket;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class PingClient extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("------长期未收到服务器反馈数据------");
                //根据具体的情况 在这里也可以重新连接
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                //System.out.println("------长期未向服务器发送数据 发送心跳------");
                //System.out.println("------发送心跳包------ping\r\n");
//                ctx.writeAndFlush(getSendByteBuf("ping"));
                PingWebSocketFrame p = new PingWebSocketFrame();
                ctx.writeAndFlush(p);

            } else if (event.state().equals(IdleState.ALL_IDLE)) {

            }

        }
    }
}
