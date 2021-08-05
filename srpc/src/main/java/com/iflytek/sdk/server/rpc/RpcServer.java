package com.iflytek.sdk.server.rpc;


import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
import com.iflytek.sdk.server.Rpc;
import com.iflytek.sdk.util.Logger;
import com.iflytek.sdk.util.PropertiesUtil;
import com.iflytek.sdk.util.RpcUtils;
import com.iflytek.sdk.util.ZookeeperUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.util.StringUtils;

/**
 * qiye2
 */
public class RpcServer implements Rpc {


    @Override
    public void makeServer(String serverNode, Integer port) {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            Serialize serialize = SerializeFactory
                                    .getSerialize(SerializeProtocol.KRYO);
                            ch.pipeline().addLast(new MessageEncoder(serialize),
                                    new MessageDecoder(serialize), new RpcServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            try {
                ChannelFuture f = bootstrap.bind(port).sync();
                Logger.info("Server is started...");
                RpcUtils.getMethods();
                f.channel().closeFuture().sync();
                String hostInfo = PropertiesUtil.getString("zkConnStr");
                if(!StringUtils.isEmpty(hostInfo)) {
                    String serverName = PropertiesUtil.getString("serverName");
                    String host = PropertiesUtil.getString("serverHost");
                    ZookeeperUtil.getInstance(hostInfo).addOrUpdateZnode(String.format(serverNode,serverName)+host+":"+port,host+":"+port);
                }else{
                    ZookeeperUtil.isAct = false;
                }
            } catch (InterruptedException e) {
                Logger.error(e.getMessage(), e);
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
    }

}


