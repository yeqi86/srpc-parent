package com.iflytek.sdk.client.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.net.URISyntaxException;


public class WebsocketClient {


    public static Channel connectToServer(String url) throws InterruptedException, URISyntaxException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer());//引用自己的协议
        //ws协议类型
        String[] query = url.split("\\?");
        URI oldsocketURI = new URI(query[0]);
        URI websocketURI = new URI(oldsocketURI.getScheme(), oldsocketURI.getAuthority(),
                oldsocketURI.getPath(), query[1], oldsocketURI.getFragment());

        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //需要协议的host和port
        Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);

        handshaker.handshake(channel);

        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        return channel;
    }
}
