# rpc-parent
快速基于spring容器管理的rpc服务
支持tcp,websocket协议

使用说明：
1、pom依赖:
![image](https://user-images.githubusercontent.com/5287563/128147577-85a10830-d2ab-4b18-9072-01003b52b46c.png)
        
2、项目说明:
srpc：       rpc组件
rpc-api：    接口组件(demo):封装tcp协议接口
rpc-client:  客户端组件(demo)
rpc-server:  服务端组件

3、tcp使用：
（1）定义接口：
    例： ![image](https://user-images.githubusercontent.com/5287563/128143359-87c2b8d4-289e-4f30-89b1-f2f1b25f9964.png)
（2）服务端实现：
    例：![image](https://user-images.githubusercontent.com/5287563/128143609-1dff155c-dc2a-4dd5-b459-6b1ff17886af.png)
（3）服务端启动：
    例：![image](https://user-images.githubusercontent.com/5287563/128143783-e5319fa6-3112-4161-9c88-ad920a602a8a.png)
（4）客户端调用：
    例:![image](https://user-images.githubusercontent.com/5287563/128144097-1fcdb213-476f-409d-ac2a-c6fd891171ca.png)

4、websocket使用：
 (1)服务端启动：
        同上
 (2)服务端实现：
    例：![image](https://user-images.githubusercontent.com/5287563/128144557-4633b76a-3192-4788-81b6-db808e1ee5ce.png)
 (3)客户端调用:
    例: ![image](https://user-images.githubusercontent.com/5287563/128144914-c6e83706-4309-41fb-a126-bc970177278d.png)
 (4)客户端回调:
    例:![image](https://user-images.githubusercontent.com/5287563/128145153-9ca75e8f-b066-4e4d-a6b1-27057252a301.png)
消息：支持指定channel,群发，离线发送
逻辑图如下：
![image](https://user-images.githubusercontent.com/5287563/128147053-2b3a8c72-21e8-4deb-b875-47f23618af37.png)



 




