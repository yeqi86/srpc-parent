package com.iflytek.sdk.util;

import java.util.concurrent.atomic.AtomicInteger;

public class NumberUtils {
    private static class SingletonHolder {
        private static final AtomicInteger INSTANCE = new AtomicInteger();
    }

    private NumberUtils(){}

    public static final AtomicInteger getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
