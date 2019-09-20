package com.aaron.mvvmlibrary.net.interceptor.logging;

import okhttp3.internal.platform.Platform;

public interface Logger {
    void log(int level, String tag, String msg);

    // 接口Logger默认实现
    Logger DEFAULT = new Logger() {
        @Override
        public void log(int level, String tag, String message) {
            Platform.get().log(level, message, null);
        }
    };
}
