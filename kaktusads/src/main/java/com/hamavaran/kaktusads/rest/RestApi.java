package com.hamavaran.kaktusads.rest;

import com.hamavaran.kaktusads.rest.Model.BaseResponse;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("device/banner/{token}/{bannerSize}/{deviceId}")
    Call<GetBottomBannerResponse> getBottomBanner(@Path("token") String token, @Path("bannerSize") String bannerSize, @Path("deviceId") String deviceId, @Query("pkgn") String packageName);

    @GET("device/sv/{receivedToken}")
    Call<BaseResponse> sendFeedback(@Path("receivedToken") String receivedTokeneviceId);


    @GET("device/video/{token}/{deviceId}")
    Call<GetBottomBannerResponse> getVideoBanner(@Path("token") String token, @Path("deviceId") String deviceId, @Query("pkgn") String packageName);


}
