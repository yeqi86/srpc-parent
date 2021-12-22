package com.iflytek.sdk.server.transfer;

import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @author： qiye2
 * @Date: 2021/11/17.
 * @Description：
 */
public class FileChannelInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
//        ch.pipeline().addLast(new ObjectEncoder());
//        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
        Serialize serialize = SerializeFactory
                .getSerialize(SerializeProtocol.HESSIAN);
        // 这里将FixedLengthFrameDecoder添加到pipeline中，指定长度为20
      //  ch.pipeline().addLast(new FixedLengthFrameDecoder(1000));
        ch.pipeline().addLast(new MessageDecoder(serialize),
                new MessageEncoder(serialize), new SecureServerHandler(), new FileTransferServerHandler());
       
    }
}

