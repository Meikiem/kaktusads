package com.hamavaran.kaktusads.rest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

//    public static final String API_URL = "http://mobile.kaktoos.net/";
    public static final String API_URL = "http://167.114.152.22:6065/";
    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder sRetrofitBuilder = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create());


    public static RestApi getInstance(String baseURL) {
        return sRetrofitBuilder.baseUrl(baseURL)
                .client(sHttpClient.connectTimeout(5, TimeUnit.MINUTES)
                        .readTimeout(10, TimeUnit.MINUTES)
                        .writeTimeout(10, TimeUnit.MINUTES)
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()).build().create(RestApi.class);
    }
}
