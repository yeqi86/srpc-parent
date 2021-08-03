package com.iflytek.sdk.client.rpc;

import com.iflytek.sdk.annotation.RpcService;
import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.dto.RpcRequest;
import com.iflytek.sdk.dto.RpcResponse;
import com.iflytek.sdk.dto.RpcUrlRequest;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.exception.ClassNotQualifiedException;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
import com.iflytek.sdk.util.ChannelUtils;
import com.iflytek.sdk.util.Logger;
import com.iflytek.sdk.util.NumberUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <br/>==========================
 * RPC代理工厂类，使用java动态代理来实现
 * <p>
 * <br/>==========================
 */

public class RpcServiceFactory implements InvocationHandler {


    private static ThreadLocal<RpcUrlRequest> clazzLocal = new ThreadLocal<>();



    @SuppressWarnings("unchecked")
    public static <T> T getClass(Class<?> clazz, String url) throws ClassNotQualifiedException {
        if (!clazz.isAnnotationPresent(RpcService.class)) {
            Logger.error("class must be annotated with RpcService");
            throw new ClassNotQualifiedException("class must be annotation with RpcService");
        }
        RpcUrlRequest req = new RpcUrlRequest(clazz, url);
        clazzLocal.set(req);
        RpcServiceFactory serviceFactory = new RpcServiceFactory();
        return (T) Proxy.newProxyInstance(serviceFactory.getClass().getClassLoader(), new Class[]{clazz}, serviceFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getClass(Class<?> clazz) throws ClassNotQualifiedException {
        RpcServiceFactory serviceFactory = new RpcServiceFactory();
        return (T) Proxy.newProxyInstance(serviceFactory.getClass().getClassLoader(), new Class[]{clazz}, serviceFactory);
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        //构造请求对象
        RpcUrlRequest req = (RpcUrlRequest) clazzLocal.get();
        String name = req.getClazz().getName();


        try {
            //得到可以连接的server
            NettyChannelPool nettyChannelPool = NettyChannelPool.getInstance(clazzLocal.get().getUrl());
            Channel channel = nettyChannelPool.syncGetChannel();
            //为每个线程建立一个callback,当消息返回的时候,在callback中获取结果
            CallbackService callbackService = new CallbackService();
            synchronized (callbackService) {
                //给消息分配一个唯一的消息序列号
                int seq = NumberUtils.getInstance().getAndIncrement();
                //利用Channel的attr方法,建立消息与callback的对应关系
                ChannelUtils.putCallback2DataMap(channel, seq, callbackService);
                RpcRequest request = new RpcRequest(name, method.getName(), args, seq);
                channel.writeAndFlush(request);
                callbackService.wait();
            }
            return callbackService.result != null ? callbackService.result.getObj():callbackService.result;

        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            throw e;
        }
    }


    public static class CallbackService {
        public volatile RpcResponse result;

        public void receiveMessage(RpcResponse response) throws Exception {
            synchronized (this) {
                result = response;
                this.notify();
            }
        }
    }


    public static Channel connectToServer(String ip, Integer port) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final RpcClientHandler  clientHandler = new RpcClientHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        Serialize serialize = SerializeFactory.getSerialize(SerializeProtocol.KRYO);
                        ch.pipeline().addLast(new MessageEncoder(serialize), new MessageDecoder(serialize), clientHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(ip, port);
        Channel channel = channelFuture.sync().channel();

        //为刚刚创建的channel，初始化channel属性
        Attribute<Map<Integer,Object>> attribute = channel.attr(ChannelUtils.dataMap);
        ConcurrentHashMap<Integer, Object> dataMap = new ConcurrentHashMap<>();
        attribute.set(dataMap);
        return channel;
    }
}
