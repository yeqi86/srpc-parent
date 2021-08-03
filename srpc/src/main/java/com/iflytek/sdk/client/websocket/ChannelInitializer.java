package com.iflytek.sdk.client.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new ChannelHandler[]{new HttpClientCodec(),new HttpObjectAggregator(1024*1024*10)});
        p.addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS));	//心跳
        p.addLast(new PingClient());								//心跳 机制
        p.addLast("hookedHandler", new WebSocketClientHandler());
    }
}

