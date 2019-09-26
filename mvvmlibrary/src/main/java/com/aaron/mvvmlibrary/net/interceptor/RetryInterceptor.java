package com.aaron.mvvmlibrary.net.interceptor;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：Aaron
 * 时间：2019/9/18:15:50
 * 邮箱：
 * 说明：重试N次的拦截器 * 通过：addInterceptor 设置
 */
public class RetryInterceptor implements Interceptor {
    private int retryNum = 0; //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
    private int maxRetry = 0;// 最大重试次数

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = retryNum;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Log.i("Retry", "num:$retryNum");
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            Log.i("Retry", "num:$retryNum");
            response = chain.proceed(request);
        }
        return response;
    }
}
