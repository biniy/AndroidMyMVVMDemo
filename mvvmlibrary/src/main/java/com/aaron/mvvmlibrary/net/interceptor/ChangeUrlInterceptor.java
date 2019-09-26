package com.aaron.mvvmlibrary.net.interceptor;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：Aaron
 * 时间：2019/9/18:15:29
 * 邮箱：
 * 说明：根据header的值，动态修改url的地址
 * <p>
 * <p>
 * 使用方式：
 * <p>
 * 1.通过给接口添加注解，设置其请求头属性的值,例如：@Headers({“url_name:xxx”})
 *
 * @Headers({"url_name:weather"})
 * @GET("/data/sk/{cityId}.html") Observable<ResponseBody> getWeatherByCityId(@Path("cityId") String cityId);
 * @Headers({"url_name:book"})
 * @GET("/v2/book/search") Observable<ResponseBody> getBook(@Query("q") String bookName);
 * <p>
 * 2.在ChangeUrlInterceptor中根据header的属性url_name的值修改请求地址
 * 3.在OkHttpClient.Builder中设置转换器，放在所有拦截器前面
 * new OkHttpClient.Builder().addInterceptor(new ChangeUrlInterceptor(headers))
 */
public class ChangeUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取request
        Request request = chain.request();
        // 从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        // 获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        // 从request中获取headers，通过给定的键url_name
        List<String> headerValues = request.headers("url_name");
        if (headerValues.size() > 0) {
            // 如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader("url_name");
            // 匹配获得新的BaseUrl
            String headerValue = headerValues.get(0);
            HttpUrl newBaseUrl = null;
            if ("weather".equals(headerValue)) {
                newBaseUrl = HttpUrl.parse("http://weather.com");
            } else if ("book".equals(headerValue)) {
                newBaseUrl = HttpUrl.parse("http://book.com");
            } else {
                newBaseUrl = oldHttpUrl;
            }
            // 重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    // 更换网络协议
                    .scheme(newBaseUrl.scheme())
                    // 更换主机名
                    .host(newBaseUrl.host())
                    // 更换端口
                    .port(newBaseUrl.port())
                    .build();
            // 重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            return chain.proceed(builder.url(newFullUrl).build());
        }
        return chain.proceed(request);
    }
}
