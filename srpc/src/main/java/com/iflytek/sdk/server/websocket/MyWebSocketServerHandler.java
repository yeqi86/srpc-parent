package com.iflytek.sdk.server.websocket;

import com.google.gson.Gson;
import com.iflytek.sdk.client.rpc.RpcServiceFactory;
import com.iflytek.sdk.client.websocket.BaseListener;
import com.iflytek.sdk.exception.ClassNotQualifiedException;
import com.iflytek.sdk.util.DynamicProxyUtil;
import com.iflytek.sdk.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;

/**
 * Created by Administrator on 2020/4/22.
 */
public class MyWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketServerHandler.class);
    private WebSocketServerHandshaker handshaker;

    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static volatile int onlineCount = 0;

    private int lossConnectCount = 0;

    private static final Gson gson = new Gson();
    /**
     * channel 通道 action 活跃的 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
// 添加
        //   String userid = String.valueOf(ctx.attr(AttributeKey.valueOf("userid")).get());
        //    NettyWebSocket.addChannel(userid,ctx.channel());
        if(logger.isDebugEnabled()) {
            logger.debug("客户端与服务端连接开启：" + ctx.channel().remoteAddress().toString());
        }
    }

    /**
     * channel 通道 Inactive 不活跃的 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    // 移除
        String userid = String.valueOf(ctx.attr(AttributeKey.valueOf("userid")).get());
        logger.info("userid:" + userid);
        NettyWebSocket.removeChannel(userid, ctx.channel());
        this.subOnlineCount();
        logger.info("有一连接关闭！当前在线人数为" + this.getOnlineCount());
        System.out.println("客户端与服务端连接关闭：" + ctx.channel().remoteAddress().toString());
    }

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
     */
  @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
// 传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest rq = (FullHttpRequest) (msg);
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
// WebSocket接入
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);

        }
    }

    /**
     * channel 通道 Read 读取 Complete 完成 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws ClassNotQualifiedException {
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            if(logger.isDebugEnabled()) {
                logger.debug("├ [Ping消息]");
            }
            return;
        }
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否pong消息
        if (frame instanceof PongWebSocketFrame) {
            if(logger.isDebugEnabled()) {
                logger.debug("├ [Pong消息]");
            }
            return;
        }

        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            ByteBuf content = binaryWebSocketFrame.content();
            if(logger.isDebugEnabled()) {
                logger.debug("├ [二进制数据]:{}" , content);
            }
            final int length = content.readableBytes();
            final byte[] array = new byte[length];
            content.getBytes(content.readerIndex(), array, 0, length);
            return;
        }
        // 应答消息
        String request = ((TextWebSocketFrame) frame).text();
          // System.out.println("服务端收到：" + request);
       // logger.info(String.format("%s received %s", ctx.channel(), request));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%s received %s", ctx.channel(), request));
        }
        ServerListener sl =SpringUtil.getBean(ServerListener.class);
        sl.doAction(request,ctx.channel());
              //   TextWebSocketFrame tws = new TextWebSocketFrame(
              //      new Date().toString() + ctx.channel().id() + "：" + request);
             // 群发
             //     Global.group.writeAndFlush(tws);
             // 返回【谁发的发给谁】
            // ctx.channel().writeAndFlush(tws);
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果HTTP解码失败，返回HHTP异常
        if (!req.getDecoderResult().isSuccess() || (!"websocket"
                .equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //获取url后置参数
        HttpMethod method = req.getMethod();
        String uri = req.getUri();
        String userid = uri.split("\\?")[1];

        //用户是否已在其他客户端登录，登出
//        if (NettyWebSocket.ChannelMap.get(userid) != null) {
//            WebMessage webMessage = new WebMessage(WebMessage.KickOut, "该用户已在其他地方登录，请重新登录！");
//            NettyWebSocket.send(userid, webMessage);
//            NettyWebSocket.removeUser(userid);
//        }

        NettyWebSocket.addChannel(userid, ctx.channel());
        ctx.attr(AttributeKey.valueOf("userid")).set(userid);
        // 构造握手响应返回，
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://" + req.headers().get(HttpHeaders.Names.HOST) + uri, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
        //是否有未发送消息
        if (NettyWebSocket.maps.containsKey(userid)) {
            TextWebSocketFrame tws = new TextWebSocketFrame(gson.toJson(NettyWebSocket.maps.get(userid)));
            ctx.channel().writeAndFlush(tws);
            NettyWebSocket.maps.remove(userid);
        }
        this.addOnlineCount();
        logger.info("有新连接加入！当前在线人数为" + this.getOnlineCount());
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
                                         DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * exception 异常 Caught 抓住 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String userid = String.valueOf(ctx.attr(AttributeKey.valueOf("userid")).get());
        NettyWebSocket.removeChannel(userid, ctx.channel());
        cause.printStackTrace();
        this.subOnlineCount();
        logger.info("有一连接关闭！当前在线人数为" + this.getOnlineCount());
        ctx.close();
    }

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        System.out.println("未收到客户端的消息了！");
//        if (evt instanceof IdleStateEvent){
//            IdleStateEvent event = (IdleStateEvent)evt;
//            if (event.state()== IdleState.READER_IDLE){
//                lossConnectCount++;
//                if (lossConnectCount>2){
//                    String userid = String.valueOf(ctx.attr(AttributeKey.valueOf("userid")).get());
//                    System.out.println(userid+"关闭这个不活跃通道！");
//                    WebMessage webMessage = new WebMessage(WebMessage.KickOut, "关闭这个不活跃通道！");
//                    NettyWebSocket.send(userid, webMessage);
//                    ctx.channel().close();
//                }
//            }
//        }else {
//            super.userEventTriggered(ctx,evt);
//        }
//    }

    public synchronized void addOnlineCount() {
        this.onlineCount++;
    }


    public synchronized void subOnlineCount() {
        this.onlineCount--;
    }

    public synchronized int getOnlineCount() {
        return onlineCount;
    }



}
