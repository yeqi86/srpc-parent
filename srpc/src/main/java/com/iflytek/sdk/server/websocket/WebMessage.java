package com.iflytek.sdk.server.websocket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/14.
 */
public class WebMessage {

    private String type;
    private Map<String,String> messageObj= new HashMap<>();
    public static final String KickOut = "kickOut";
    public static final String ExportMsg = "exportMsg";
    public static final String ExportRate = "exportRate";

    public WebMessage(String type, String msg){
        this.type = type;
        this.messageObj.put(type,msg);
    }

    public WebMessage(String type, String msg, Map<String,String> map){
        this.type = type;
        this.messageObj = map;
        this.messageObj.put(type,msg);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getMessageObj() {
        return messageObj;
    }

    public void setMessageObj(Map<String, String> messageObj) {
        this.messageObj = messageObj;
    }

    public String toString(){
        return this.getType()+"|"+this.getMessageObj().get(this.getType());
    }
}
