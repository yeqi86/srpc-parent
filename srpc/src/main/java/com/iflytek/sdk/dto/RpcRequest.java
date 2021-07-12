package com.iflytek.sdk.dto;

import java.io.Serializable;

/**
 * <br/>==========================
 * RPC请求对象
 * @author qiye2
 * <br/>==========================
 */
public class RpcRequest implements Serializable{
    private static final long serialVersionUID = -3432433811350669741L;
    private  int seq;
    private String clsName;
    private String methodName;
    private Object[] parameters;

    public RpcRequest() {}

    public RpcRequest(String clsName, String methodName, Object[] parameters, int seq) {
        this.clsName = clsName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
