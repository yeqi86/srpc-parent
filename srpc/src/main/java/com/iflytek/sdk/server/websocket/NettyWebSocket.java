package com.iflytek.sdk.server.websocket;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 2020/4/22.
 */
public class NettyWebSocket {
    private   static ChannelGroup GlobalGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public  static ConcurrentMap<String, ChannelId> ChannelMap=new ConcurrentHashMap();
    //保存用户未收到信息
    public static ConcurrentHashMap<String, WebMessage> maps = new ConcurrentHashMap<String, WebMessage>();
    private static final Gson gson = new Gson();
    public  static void addChannel(String userid, Channel channel){
        GlobalGroup.add(channel);
        ChannelMap.put(userid,channel.id());
    }
    public static void removeChannel(String userid, Channel channel){
        GlobalGroup.remove(channel);
        ChannelMap.remove(userid);
    }

    public static void removeUser(String userid){
        ChannelId channelId =ChannelMap.get(userid);
        Channel channel = GlobalGroup.find(channelId);
        if(channel != null) {
            GlobalGroup.remove(channel);
        }
        ChannelMap.remove(userid);
    }

    public static Channel findChannel(String userid){
        return GlobalGroup.find(ChannelMap.get(userid));
    }
    public static void send2All(TextWebSocketFrame tws){
        GlobalGroup.writeAndFlush(tws);
    }

    public static void send(String userid, WebMessage webMessage){
        ChannelId channelId =  ChannelMap.get(userid);
        if(channelId != null){
            Channel channel =  GlobalGroup.find(channelId);
            TextWebSocketFrame tws = new TextWebSocketFrame(gson.toJson(webMessage));
            channel.writeAndFlush(tws);
        }else if(webMessage.ExportMsg.equals(webMessage.getType())){
                maps.put(userid, webMessage);
        }

    }

}
