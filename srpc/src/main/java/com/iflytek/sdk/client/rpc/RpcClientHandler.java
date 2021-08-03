package com.iflytek.sdk.client.rpc;


import com.google.gson.Gson;
import com.iflytek.sdk.dto.RpcResponse;
import com.iflytek.sdk.util.ChannelUtils;
import com.iflytek.sdk.util.Logger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * <br/>==========================
 * RPC客户端处理
 * @author qiye2
 * <br/>===========
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {



    private static final Gson gson = new Gson();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Logger.info("Read message from server...");
        Channel channel = ctx.channel();
        RpcResponse response = (RpcResponse) msg;
        //获取消息对应的callback
        RpcServiceFactory.CallbackService callbackService = ChannelUtils.<RpcServiceFactory.CallbackService>removeCallback(channel, response.getSeq());
        callbackService.receiveMessage(response);
        Logger.info("Read message from server:"+gson.toJson(response));
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("Connection is established...");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Exception e = (Exception)cause;
        Logger.error(e.getMessage(), e);
        ctx.close();
    }
}
