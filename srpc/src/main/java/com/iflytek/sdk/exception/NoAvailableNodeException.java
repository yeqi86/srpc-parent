package com.iflytek.sdk.exception;

/**
 * <br/>==========================
 *
 * <br/>==========================
 */
public class NoAvailableNodeException extends Exception{

    private String msg;

    public NoAvailableNodeException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}
