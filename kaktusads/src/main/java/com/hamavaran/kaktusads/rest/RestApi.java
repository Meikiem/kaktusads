package com.hamavaran.kaktusads.rest;

import com.hamavaran.kaktusads.rest.Model.BaseResponse;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {
    //5.160.219.166:6065/device/{token}/120x240/{deviceId}?pkgn={packageName}

    @GET("device/banner/{token}/{bannerSize}/{deviceId}")
    Call<GetBottomBannerResponse> getBottomBanner(@Path("token") String token, @Path("bannerSize") String bannerSize, @Path("deviceId") String deviceId, @Query("pkgn") String packageName);

    @GET("device/sv/{receivedToken}")
    Call<BaseResponse> sendFeedback(@Path("receivedToken") String receivedTokeneviceId);

}
