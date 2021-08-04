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
![image](https://user-images.githubusercontent.com/5287563/128147929-cdb6e706-c1bf-4088-af1c-612d59f72473.png)
（2）服务端实现：
![image](https://user-images.githubusercontent.com/5287563/128148051-370a44d0-547a-45b6-b1bd-c71c588ab500.png)
（3）服务端启动：
![image](https://user-images.githubusercontent.com/5287563/128148150-e13e5854-f4f3-4881-aaab-9a89795856c9.png)
（4）客户端调用：
![image](https://user-images.githubusercontent.com/5287563/128148362-05990be3-2208-49c4-b92b-48a6c06e4048.png)

4、websocket使用：
 (1)服务端启动：
 (2)服务端实现：
 (3)客户端调用:
 (4)客户端回调:
 (5)支持指定channel,群发，离线发送
 (6)逻辑图如下：
![image](https://user-images.githubusercontent.com/5287563/128147053-2b3a8c72-21e8-4deb-b875-47f23618af37.png)



 




