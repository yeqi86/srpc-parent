package com.iflytek.sdk.server.websocket;


import com.iflytek.sdk.decoder.MessageDecoder;
import com.iflytek.sdk.encoder.MessageEncoder;
import com.iflytek.sdk.protocol.Serialize;
import com.iflytek.sdk.protocol.SerializeFactory;
import com.iflytek.sdk.protocol.SerializeProtocol;
import com.iflytek.sdk.server.rpc.RpcServerHandler;
import com.iflytek.sdk.util.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RPC Server
 *
 *
 */
public class RpcWebsocketServer implements ApplicationListener {


    private Integer port;

    private Map<String, Object> handlerMap = new HashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public RpcWebsocketServer(Integer port) {
        this.port = port;
    }





    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        try {
            start();
        } catch (Exception e) {
            Logger.error( "netty启动报错", e);
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
            synchronized (RpcWebsocketServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L,
                            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public RpcWebsocketServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            Logger.info("Loading service: ," + interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }

        return this;
    }

    public void start() throws Exception {
        if (bossGroup == null && workerGroup == null) {
            System.out.println("===========================Netty端口启动========");
// Boss线程：由这个线程池提供的线程是boss种类的，用于创建、连接、绑定socket， （有点像门卫）然后把这些socket传给worker线程池。
// 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
            EventLoopGroup bossGroup = new NioEventLoopGroup();
// Worker线程：Worker线程执行所有的异步I/O，即处理操作
            EventLoopGroup workGroup = new NioEventLoopGroup();
            try {
// ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workGroup);
// 设置非阻塞,用它来建立新accept的连接,用于构造serversocketchannel的工厂类
                b.channel(NioServerSocketChannel.class);
// ChildChannelHandler 对出入的数据进行的业务操作,其继承ChannelInitializer
                b.childHandler(new ChildChannelHandler());
                System.out.println("服务端开启等待客户端连接 ... ...");
                Channel ch = b.bind(port).sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        }
    }
}


