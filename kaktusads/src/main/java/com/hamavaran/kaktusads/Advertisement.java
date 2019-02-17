package com.hamavaran.kaktusads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hamavaran.kaktusads.interfaces.BannerClickListener;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.hamavaran.kaktusads.rest.Model.BaseResponse;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;
import com.hamavaran.kaktusads.rest.RestClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hamavaran.kaktusads.KaktusAdsActivity.ADS_LINK;
import static com.hamavaran.kaktusads.KaktusAdsActivity.ADS_TOKEN;
import static com.hamavaran.kaktusads.KaktusAdsActivity.ADS_URL;


public class Advertisement extends AppCompatActivity implements FullPageAdsListener{

    private View view;
    private boolean closeButtonEnabled = false;
    private RelativeLayout bottomBannerRL;
    private String serviceToken = null;
    private Context context;
    private BannerClickListener listener;
    @SuppressLint("StaticFieldLeak")
    private final static Advertisement advertisement = new Advertisement();
    private String link = null;
    private int timeInterval = -1;
    private String FULL_SIZE;
    private RelativeLayout rootRL;
    private String packageName = null;

    public Advertisement setCloseButtonEnabled(boolean isEnabled) {
        closeButtonEnabled = isEnabled;
        return this;
    }

    public Advertisement setTimeInterval(int interval) {
        timeInterval = interval;
        return this;
    }


    public Advertisement setPackageName(String packageName) {
        this.packageName = packageName;
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
        if (checkBannerCallValidation()) return;
        GifImageView myImage = initBottomAdUI();
        getBanner(size.SIZE, myImage, false);
    }

    public void loadFullPageBanner() {
        if (checkBannerCallValidation()) return;
        GifImageView myImage = initFullPageAdUI();
        getBanner(FULL_SIZE, myImage, true);
    }

    private boolean checkBannerCallValidation() {
        if(packageName == null){
            Toast.makeText(context, "Please set package name", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(serviceToken == null){
            Toast.makeText(context, "Please set your token", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
                listener.onBottomBannerClick();
                ((ViewGroup) view).removeView(rootRL);
            }
        });
        return myImage;
    }

    private RelativeLayout initMainView() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(config);
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
        RestClient.getInstance(RestClient.API_URL).getBottomBanner(serviceToken, size, getDeviceId(), Base64.encodeToString(packageName.getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
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
        final String imageUrl = response.body().getResult().getImage();
        link = response.body().getResult().getLink();

        ImageLoader.getInstance().displayImage(imageUrl.startsWith("http") ? imageUrl : "http:" + imageUrl, myImage, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isFullPage) {
                            setImageContainerLayoutParams(myImage);
                            sendFeedbackOnBannerLoaded(response.body().getResult().getToken());
                            rootRL.setVisibility(View.VISIBLE);
                        }else{
                            SharedMethode.getInstance().setContext(Advertisement.this);

                            Intent adsIntent = new Intent(context, KaktusAdsActivity.class);
                            adsIntent.putExtra(ADS_LINK, link);
                            adsIntent.putExtra(ADS_URL, imageUrl);
                            adsIntent.putExtra(ADS_TOKEN, response.body().getResult().getToken());
                            context.startActivity(adsIntent);
                        }
                    }
                }, 1000);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }

    private void sendFeedbackOnBannerLoaded(String token) {
        RestClient.getInstance(RestClient.API_URL).sendFeedback(token).enqueue(new Callback<BaseResponse>() {
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
        return String.valueOf(Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    @Override
    public void onCloseButtonClick() {

    }

    @Override
    public void onAdsClick(String adsLink) {
        if (!adsLink.startsWith("http://") && !adsLink.startsWith("https://"))
            adsLink = "http://" + adsLink;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adsLink));
        context.startActivity(browserIntent);
    }

    @Override
    public void onImageLoaded(String serviceToken) {
        sendFeedbackOnBannerLoaded(serviceToken);

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
