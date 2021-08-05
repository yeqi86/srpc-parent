package com.iflytek.sdk.server.rpc;

import com.iflytek.sdk.dto.RpcRequest;
import com.iflytek.sdk.dto.RpcResponse;
import com.iflytek.sdk.dto.RpcResponse.ResponseCode;
import com.iflytek.sdk.util.Logger;
import com.iflytek.sdk.util.SpringUtil;
import io.netty.channel.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.stream.Stream;


/**
**qiye2
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("Received connect from client...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Logger.info("Received request from client...");

        if (!ctx.channel().isWritable()) {
            return;
        }

        //得到具体的内容
        RpcRequest request = (RpcRequest) msg;

        //找到客户端请求的指定类的bean
        Object obj = SpringUtil.getBean(Class.forName(request.getClsName()));
        if (obj == null) {
            ctx.channel().writeAndFlush(new RpcResponse(null, new ResponseCode(500, "找不到[%s]spring bean",
                request.getClsName()),request.getSeq()));
            return;
        }
        //找到客户端请求的方法
        final boolean[] hasMethod = new boolean[1];
        Method[] methods = obj.getClass().getMethods();
        Stream.of(methods).forEach(method -> {
            if (method.getName().equalsIgnoreCase(request.getMethodName())) {
                try {
                    Object result = method.invoke(obj, request.getParameters());
                    ctx.channel().writeAndFlush(new RpcResponse(result, ResponseCode.SUCCESS,request.getSeq())).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            Logger.debug("Send response for request " + request.getMethodName()+":"+request.getParameters());
                        }
                    });
                    hasMethod[0] = true;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Logger.error(e.getMessage(), e);
                }
            }
        });

        //如果找不到方法，返回错误给客户端
        if (!hasMethod[0]) {
            ctx.channel().writeAndFlush(new RpcResponse(null, new ResponseCode(500, "找不到[%s]方法",
                request.getMethodName()),request.getSeq()));
            Logger.error("找不到指定的[{}]方法", request.getMethodName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Exception exception = (Exception) cause;
        Logger.error(exception.getMessage(), exception);
        ctx.close();
    }
}
