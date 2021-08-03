package com.iflytek.iptv;


import com.iflytek.sdk.server.Server;
import com.iflytek.sdk.server.rpc.RpcServer;
import com.iflytek.sdk.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = "com.iflytek")
@EnableScheduling
@Slf4j
public class TranscodeClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TranscodeClientApplication.class, args);
        Server rpc = (Server) SpringUtil.getBean(Server.class);
        rpc.startRpc();
    }





}
