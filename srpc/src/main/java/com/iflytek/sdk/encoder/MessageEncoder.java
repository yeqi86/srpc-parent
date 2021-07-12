package com.iflytek.sdk.encoder;

import com.iflytek.sdk.protocol.Serialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * <br/>==========================
 *
 * <br/>==========================
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

    private Serialize serialize;
    public MessageEncoder(Serialize serialize) {
        this.serialize = serialize;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        serialize.serialize(msg, out);
    }
}
