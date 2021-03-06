package com.iflytek.sdk.decoder;

import com.iflytek.sdk.protocol.Serialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;


/**
 * <br/>==========================
 * 消息解码器，把客户端发过来的信息进行解码，转换为request对象
 *
 * @author qiye2
 * <br/>==========================
 */
public class MessageDecoder extends ByteToMessageDecoder{

    private Serialize serialize;
    public MessageDecoder(Serialize serialize) {
        this.serialize = serialize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object obj = serialize.deserialize(in);
        if (obj != null) {
            out.add(obj);
        }
    }
}
