package com.iflytek.sdk.client.transfer;


import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.dto.RequestFile;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.encoder.MessageEncoderBySize;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
import com.iflytek.sdk.server.rpc.RpcServerHandler;
import com.iflytek.sdk.util.MD5FileUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

/**
 * @author： liwh
 * @Date: 2016/11/17.
 * @Description：
 */
public class FileTransferClient {

    public void connect(int port, String host, final RequestFile echoFile) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture f = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {

                    Serialize serialize = SerializeFactory
                            .getSerialize(SerializeProtocol.HESSIAN);
                 //   ch.pipeline().addLast(new FixedLengthFrameDecoder(1000));
                    ch.pipeline().addLast(new MessageDecoder(serialize), new MessageEncoderBySize(serialize),
                            new FileTransferClientHandler(echoFile));
                }
            });//设置服务器端的编码和解码
            //   ch.pipeline().addLast(new FileTransferClientHandler(echoFile));

            f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            if (f != null) {
                f.channel().close();
            }

        }
    }

    private static String getSuffix(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return fileType;
    }

    public static void main(String[] args) {
        int port = 1232;
		/*if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}*/
        try {
            RequestFile echo = new RequestFile();
            // File file = new File("D:\\ffmpeg_124162.rar");  //  "D://files/xxoo"+args[0]+".amr"
            File file = new File("D:\\back.dat");  //  "D://files/xxoo"+args[0]+".amr"
           // File file = new File("D:\\小学英语随堂练原型-20201207\\音频媒资-20220105.7z");  //  "D://files/xxoo"+args[0]+".amr"
            String fileName = file.getName();// 文件名
            echo.setFile(file);
            echo.setFile_md5(MD5FileUtil.getFileMD5String(file));
            echo.setFile_name(fileName);
            echo.setFile_type(getSuffix(fileName));
            echo.setStarPos(0);// 文件开始位置
            new FileTransferClient().connect(port, "127.0.0.1", echo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
