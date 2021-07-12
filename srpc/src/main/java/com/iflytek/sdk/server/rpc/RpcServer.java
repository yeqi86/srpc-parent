package com.iflytek.sdk.server.rpc;


import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

/**
 * RPC Server
 */
public class RpcServer implements ApplicationListener {


    private Integer port;

    private boolean open = true;

    public static String server_node ="/server/%s/";
    public static String client_node ="/server/%s/";


    private Map<String, Object> handlerMap = new HashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public RpcServer(Integer port, boolean open) {
        this.port = port;
        this.open = open;
    }
    public RpcServer(Integer port) {
        this.port = port;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        try {
            if(this.open) {
                startRpc();
            }
        } catch (Exception e) {
            Logger.error("netty启动报错", e);
        }
    }


    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L,
                            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public RpcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            Logger.info("Loading service: ," + interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }

        return this;
    }

    public void startRpc() throws Exception {
        if (bossGroup == null && workerGroup == null) {
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
                    ZookeeperUtil.getInstance(hostInfo).addOrUpdateZnode(String.format(server_node,serverName)+host+":"+port,host+":"+port);
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


}


