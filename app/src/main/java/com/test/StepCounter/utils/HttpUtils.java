package com.test.StepCounter.utils;

/**
 * Created by Administrator on 2017/4/20.
 */

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public static final MediaType KV = MediaType.parse("application/x-www-form-urlencoded");
    public String postKv(String url,String kv) throws IOException{
        RequestBody body = RequestBody.create(KV,kv);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        return result;
    }

}
