package com.iflytek.sdk.client.rpc;

import io.netty.channel.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <br/>==========================
 * RPC客户端处理
 * @author qiye2
 * <br/>===========
 */
public class NettyChannelPool {

    private String url;

    public NettyChannelPool(String url){
        this.url = url;
        this.channels = new Channel[MAX_CHANNEL_COUNT];
        this.locks = new Object[MAX_CHANNEL_COUNT];
        for (int i = 0; i < MAX_CHANNEL_COUNT; i++) {
            this.locks[i] = new Object();
        }
    }

    private static class SingletonHolder {
        private static Map<String, NettyChannelPool> maps = new ConcurrentHashMap<>();
    }

    public static final NettyChannelPool getInstance(String url) {
        if(SingletonHolder.maps.containsKey(url)){
           return SingletonHolder.maps.get(url);
        }else{
            SingletonHolder.maps.put(url, new NettyChannelPool(url));
            return SingletonHolder.maps.get(url);
        }
    }

    private Channel[] channels;
    private Object [] locks;
    private static final int MAX_CHANNEL_COUNT = 4;


    /**
     * 同步获取netty channel
     */
    public Channel syncGetChannel() throws InterruptedException {
        //产生一个随机数,随机的从数组中获取channel
        int index = new Random().nextInt(MAX_CHANNEL_COUNT);
        Channel channel = channels[index];
        //如果能获取到,直接返回
        if (channel != null && channel.isActive()) {
            return channel;
        }

        synchronized (locks[index]) {
            channel = channels[index];
            //这里必须再次做判断,当锁被释放后，之前等待的线程已经可以直接拿到结果了。
            if (channel != null && channel.isActive()) {
                return channel;
            }
            //开始跟服务端交互，获取channel
            String[] serverConfig = this.url.split(":");
            channel = RpcServiceFactory.connectToServer(serverConfig[0],Integer.parseInt(serverConfig[1]));
            channels[index] = channel;
        }

        return channel;
    }



}
