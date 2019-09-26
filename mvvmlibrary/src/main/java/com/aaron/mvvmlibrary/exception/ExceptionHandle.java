package com.aaron.mvvmlibrary.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * 统一异常处理，提取错误信息展示
 */
public class ExceptionHandle {
    private static final int ERRORREQUEST = 400;//错误请求 — 请求中有语法问题，或不能满足请求
    private static final int UNAUTHORIZED = 401;//未授权 — 未授权客户机访问数据
    private static final int FORBIDDEN = 403;//禁止 — 即使有授权也不需要访问
    private static final int NOT_FOUND = 404;//找不到 — 服务器找不到给定的资源；文档不存在。
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;//内部错误 — 因为意外情况，服务器不能完成请求
    private static final int INTERNAL_SERVER_UNEXCUTE = 501;//未执行 — 服务器不支持请求的工具
    private static final int INTERNAL_SERVER_GATEWAY_ERROR = 502;//错误网关 — 服务器接收到来自上游服务器的无效响应
    private static final int SERVICE_UNAVAILABLE = 503;//无法获得服务 — 由于临时过载或维护，服务器无法处理请求。

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            ex.setErrorMessage(httpException.message());
            switch (httpException.code()) {
                case ERRORREQUEST:
                    ex.setMessage("错误请求");
                    break;
                case UNAUTHORIZED:
                    ex.setMessage("操作未授权");
                    break;
                case FORBIDDEN:
                    ex.setMessage("请求被拒绝");
                    break;
                case NOT_FOUND:
                    ex.setMessage("资源不存在");
                    break;
                case REQUEST_TIMEOUT:
                    ex.setMessage("服务器执行超时");
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex.setMessage("服务器内部错误");
                    break;
                case INTERNAL_SERVER_UNEXCUTE:
                    ex.setMessage("未执行");
                    break;
                case INTERNAL_SERVER_GATEWAY_ERROR:
                    ex.setMessage("网关错误");
                    break;
                case SERVICE_UNAVAILABLE:
                    ex.setMessage("服务器不可用");
                    break;
                default:
                    ex.setMessage("网络错误");
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {
            ex = new ResponseThrowable(e, ERROR.PARSE_ERROR);
            ex.setMessage("解析错误");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, ERROR.NETWORD_ERROR);
            ex.setMessage("连接失败");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponseThrowable(e, ERROR.SSL_ERROR);
            ex.setMessage("证书验证失败");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.setMessage("连接超时");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.setMessage("连接超时");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else if (e instanceof java.net.UnknownHostException) {
            ex = new ResponseThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.setMessage("主机地址未知");
            ex.setErrorMessage(e.getMessage());
            return ex;
        } else {
            ex = new ResponseThrowable(e, ERROR.UNKNOWN);
            ex.setMessage("未知错误");
            ex.setErrorMessage(e.getMessage());
            return ex;
        }
    }


    /**
     * 约定异常 这个具体规则需要与服务端或者领导商讨定义
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

}

