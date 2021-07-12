package com.iflytek.sdk.exception;

/**
 * class is not qualified to be execute as rpc class
 * @author qiye2
 **/
public class ClassNotQualifiedException extends Exception{

    private String msg;

    public ClassNotQualifiedException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }
}
