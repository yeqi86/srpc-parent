package com.iflytek.sdk.server;


import com.iflytek.sdk.server.rpc.RpcServer;
import com.iflytek.sdk.server.transfer.FileTransferServer;
import com.iflytek.sdk.server.websocket.RpcWebsocketServer;

/**
 * RPC Server
 */
public class ServerFactroy {
    public static final  String RPC = "rpc";
    public static final  String WEBSOCKET = "websocket";
    public static final  String TRANSFER = "transfer";



    public static Rpc getServer(String type) {
        switch (type) {
            case WEBSOCKET:
                return new RpcWebsocketServer();
            case TRANSFER:
                return new FileTransferServer();
            default:
                return new RpcServer();
        }


    }


}


