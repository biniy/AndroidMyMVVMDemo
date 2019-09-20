package com.aaron.mvvmlibrary.net.converter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 作者：Aaron
 * 时间：2018/10/25:17:46
 * 邮箱：
 * 说明：响应解密
 */
public class JsonResponseBodyDecryptConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    JsonResponseBodyDecryptConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        Log.e("retrofitResponse====", response.toString());
//        try {
//            return adapter.fromJson(AnalyzeJsonDataUtils.jsonToJavaBean(response, ResultBean.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            value.close();
//        }
        return null;
    }
}