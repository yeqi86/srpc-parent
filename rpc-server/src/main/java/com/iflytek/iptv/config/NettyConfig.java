package com.iflytek.iptv.config;

import com.iflytek.sdk.server.Server;
import com.iflytek.sdk.server.rpc.RpcServer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class NettyConfig {


    @Bean(name="rpcServer")
    public Server getRpcServer() {
            return new Server(1232);
    }
}
