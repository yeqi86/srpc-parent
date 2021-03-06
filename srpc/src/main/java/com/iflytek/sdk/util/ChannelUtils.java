package com.iflytek.sdk.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;

public class ChannelUtils {
    public static final int MESSAGE_LENGTH = 16;
    public static final AttributeKey<Map<Integer, Object>> dataMap = AttributeKey.valueOf("dataMap");
    public static <T> void putCallback2DataMap(Channel channel, int seq, T callback) {
        channel.attr(dataMap).get().put(seq, callback);
    }

    public static <T> T removeCallback(Channel channel, int seq) {
        return (T) channel.attr(dataMap).get().remove(seq);
    }
}

