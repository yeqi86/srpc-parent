package com.iflytek.iptv;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = {"com.iflytek.sdk","com.iflytek.iptv"})
@EnableScheduling
@Slf4j
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

    }

}
