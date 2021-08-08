# rpc-parent
快速基于spring容器管理的rpc服务

支持tcp,websocket协议

一、背景

在开发过程中，为实现服务端，客户端调用方式，简化web调用流程，快速完成第三方接口对接。整理了基于netty集成rpc方案的集成，为了提供消息推送，服务监控提供了websocket协议的实现。

二、	Rpc服务框架组成：

客户端（Client）：服务调用方。

客户端存根（Client Stub）：存放服务端地址信息，将客户端的请求参数数据信息打包成网络消息，再通过网络传输发送给服务端。

服务端存根（Server Stub）：接收客户端发送过来的请求消息并进行解包，然后再调用本地服务进行处理。

服务端（Server）：服务的真正提供者。

Network Service：底层传输，可以是TCP或HTTP。

三、环境支持
    
    netty 4.1.8
    spring  4.0+
    kryo 4.0.2

四、	Rpc流程序列图：

流程分工实现序列图：

![image](https://user-images.githubusercontent.com/5287563/128637934-bc2618c7-fcee-4d77-8bc8-efd7daeca9e5.png)

五、Websocket流程图：

六、使用说明：

1、pom依赖:

![image](https://user-images.githubusercontent.com/5287563/128147577-85a10830-d2ab-4b18-9072-01003b52b46c.png)
        
2、项目说明:

	srpc：       rpc组件
	rpc-api：    接口组件(demo):封装tcp协议接口
	rpc-client:  客户端组件(demo)
	rpc-server:  服务端组件

3、tcp使用：

    （1）定义接口：

![image](https://user-images.githubusercontent.com/5287563/128147929-cdb6e706-c1bf-4088-af1c-612d59f72473.png)

    （2）服务端实现：

![image](https://user-images.githubusercontent.com/5287563/128148051-370a44d0-547a-45b6-b1bd-c71c588ab500.png)

    （3）服务端启动：

![image](https://user-images.githubusercontent.com/5287563/128148150-e13e5854-f4f3-4881-aaab-9a89795856c9.png)

    （4）客户端调用：

![image](https://user-images.githubusercontent.com/5287563/128148362-05990be3-2208-49c4-b92b-48a6c06e4048.png)

4、websocket使用：
 
    (1)服务端启动：
    
    如上
 
    (2)服务端实现：
    @Service
    public class ServerSimpleListener implements ServerListener {
        @Override
        public void doAction(String msg, Channel channel) {
            log.info(msg+":"+msg);
            TextWebSocketFrame tws = new TextWebSocketFrame("收到信息");
            channel.writeAndFlush(tws);
            TextWebSocketFrame tws2 = new TextWebSocketFrame("大家都收到信息");
            NettyWebSocket.send2All(tws2);
        }
    }
 
    (3)客户端调用:
           if(channel == null || !channel.isActive()) {
                channel = WebsocketClient.connectToServer("http://127.0.0.1:1232?665887");
            }
            TextWebSocketFrame frame = new TextWebSocketFrame("你好\r\n");
            channel.writeAndFlush(frame);
 
    (4)客户端回调:
        @Service
        public class SimpleListener implements BaseListener {
            private final static Logger LOGGER = LoggerFactory.getLogger(SimpleListener.class);
            /**
             * 一个简单的Listener方法
             * @param event Guava规定此处只能有一个参数
             */
            @Override
            public void doAction(final String event){
                if (LOGGER.isInfoEnabled()){
                    LOGGER.info("Received event [{}] and will take a action", event);
                }
            }
 
    (5)支持指定channel,群发，离线发送
 
    (6)逻辑图如下：
    
![image](https://user-images.githubusercontent.com/5287563/128147053-2b3a8c72-21e8-4deb-b875-47f23618af37.png)

5、github地址:https://github.com/yeqi86/srpc-parent



 
