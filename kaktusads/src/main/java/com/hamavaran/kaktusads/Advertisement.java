package com.hamavaran.kaktusads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.interfaces.BannerClickListener;
import com.hamavaran.kaktusads.rest.Model.BaseResponse;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;
import com.hamavaran.kaktusads.rest.RestClient;


import androidx.annotation.Nullable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Advertisement {

    private View view;
    private boolean closeButtonEnabled = false;
    private RelativeLayout bottomBannerRL;
    private String serviceToken;
    private Context context;
    private BannerClickListener listener;
    @SuppressLint("StaticFieldLeak")
    private final static Advertisement advertisement = new Advertisement();
    private String link = null;
    private int timeInterval = -1;
    private String FULL_SIZE;
    private RelativeLayout rootRL;


    public Advertisement setCloseButtonEnabled(boolean isEnabled) {
        closeButtonEnabled = isEnabled;
        return this;
    }

    public Advertisement setTimeInterval(int interval) {
        timeInterval = interval;
        return this;
    }


    public Advertisement setServiceToken(String token) {
        serviceToken = token;
        return this;
    }


    public static Advertisement into(Context context) {
        advertisement.context = context;
        return advertisement;
    }

    public Advertisement listener(BannerClickListener listener) {
        this.listener = listener;
        return this;

    }


    public void loadBottomBanner(final BANNER_SIZES size) {

        if (timeInterval > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadBanner(size);
                }
            }, timeInterval);
        else
            loadBanner(size);

    }

    private void loadBanner(BANNER_SIZES size) {
        GifImageView myImage = initBottomAdUI();
        getBanner(size.SIZE, myImage, false);
    }

    public void loadFullPageBanner() {
        GifImageView myImage = initFullPageAdUI();
        getBanner(FULL_SIZE, myImage, true);
    }


    private GifImageView initFullPageAdUI() {
        final RelativeLayout rootRL = initMainView();
        GifImageView myImage = new GifImageView(context);
        myImage.setAdjustViewBounds(true);
        rootRL.addView(myImage);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        myImage.setLayoutParams(layoutParams);
        myImage.setScaleType(ImageView.ScaleType.FIT_CENTER);


        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.ic_close);
        rootRL.addView(closeButton);

        ((ViewGroup) view).addView(rootRL);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeButtonEnabled)
                    ((ViewGroup) view).removeView(rootRL);
                listener.onBottomBannerCloseClick();
            }
        });

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://" + link;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
                ((ViewGroup) view).removeView(rootRL);
            }
        });
        return myImage;
    }

    private GifImageView initBottomAdUI() {

        final RelativeLayout rootRL = initMainView();
        bottomBannerRL = new RelativeLayout(context);
        bottomBannerRL.setVisibility(View.INVISIBLE);

        rootRL.addView(bottomBannerRL);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        GifImageView myImage = new GifImageView(context);
        myImage.setAdjustViewBounds(true);
        bottomBannerRL.addView(myImage);
        myImage.setLayoutParams(layoutParams);
        myImage.setScaleType(ImageView.ScaleType.FIT_CENTER);


        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.ic_close);
        bottomBannerRL.addView(closeButton);

        ((ViewGroup) view).addView(rootRL);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeButtonEnabled)
                    ((ViewGroup) view).removeView(rootRL);
                listener.onBottomBannerCloseClick();
            }
        });

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://" + link;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            }
        });
        return myImage;
    }

    private RelativeLayout initMainView() {
        view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootRL = new RelativeLayout(context);
        rootRL.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        rootRL.setVisibility(View.INVISIBLE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        FULL_SIZE = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
        return rootRL;
    }

    private void getBanner(String size, final GifImageView myImage, final boolean isFullPage) {
        RestClient.getInstance(RestClient.API_FULL_PAGE_URL).getBottomBanner(serviceToken, size, getDeviceId(), Base64.encodeToString(MyApplication.getContext().getPackageName().getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
            @Override
            public void onResponse(Call<GetBottomBannerResponse> call, final Response<GetBottomBannerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    handleGetBannerResponse(response, myImage, isFullPage);
                }
            }

            @Override
            public void onFailure(Call<GetBottomBannerResponse> call, Throwable t) {
                Log.d("LOADING BOTTOM BANNER", "FAILURE");
            }
        });
    }

    private void handleGetBannerResponse(final Response<GetBottomBannerResponse> response, final GifImageView myImage, final boolean isFullPage) {
        assert response.body() != null;
        String imageUrl = response.body().getResult().getImage();
        link = response.body().getResult().getLink();
        Glide.with(view).load(imageUrl.startsWith("http") ? imageUrl : "http:" + imageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFullPage)
                            setImageContainerLayoutParams(myImage);
                        sendFeedbackOnBannerLoaded(response.body().getResult().getToken());
                        rootRL.setVisibility(View.VISIBLE);
                    }
                }, 1000);
                return false;
            }
        }).into(myImage);
    }

    private void sendFeedbackOnBannerLoaded(String token) {
        RestClient.getInstance(RestClient.API_FULL_PAGE_URL).sendFeedback(token).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200)
                    Log.d("LOAD AD FEEDBACK", "SUCCESSFUL");
                else
                    Log.d("FAILED AD FEEDBACK", "STATUS: **");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.d("LOAD AD FEEDBACK", "FAILED");
            }
        });

    }

    private void setImageContainerLayoutParams(GifImageView myImage) {
        RelativeLayout.LayoutParams bottomBannerLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                myImage.getHeight());
        bottomBannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        bottomBannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);


        bottomBannerRL.setLayoutParams(bottomBannerLayoutParams);
        bottomBannerRL.setVisibility(View.VISIBLE);
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        return String.valueOf(Settings.Secure.getString(MyApplication.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    public enum BANNER_SIZES {
        SIZE_ONE(468, 60),
        SIZE_TWO(728, 90),
        SIZE_THREE(970, 90),
        SIZE_FOUR(960, 144);

        private final String SIZE;

        BANNER_SIZES(int width, int height) {
            SIZE = width + "x" + height;
        }
    }

}
