package com.iflytek.sdk.util;

import com.iflytek.sdk.annotation.RpcService;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RpcUtils {

   public static List<String> getMethods(){
       //入参 要扫描的包名
       Reflections f = new Reflections("com.iflytek.iptv");
       //入参 目标注解类
       Set<Class<?>> set = f.getTypesAnnotatedWith(RpcService.class);
       List<String> rpcmethods = new ArrayList<>();
        for(Class obj: set) {
            Method[] methods = obj.getClass().getMethods();
            for(Method method : methods){
                rpcmethods.add(String.format("%s:%s",obj.getName(),method.getName()));
            }
        }
        return rpcmethods;

    }
}
