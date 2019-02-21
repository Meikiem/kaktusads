package com.hamavaran.kaktusads.activity;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.R;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;
import com.hamavaran.kaktusads.rest.RestClient;
import com.hamavaran.kaktusads.util.SharedMethode;

public class KaktusAdsActivity extends AppCompatActivity {

    public static String ADS_LINK = "ads_link";
    public static String ADS_URL = "ads_url";
    public static String ADS_TOKEN = "ads_token";
    private String loadedAdsUrl = null;
    private String loadedAdsToken = null;
    private String loadedAdsLink = null;
    private FullPageAdsListener fullPageAdsListener;
    private String LISTENER = "full_page_listener";

    private int previousOrientation = Configuration.ORIENTATION_UNDEFINED;
    private boolean rotating = false;
    private String IS_ROTATITED = "is_rotated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaktus_ads);

        if (savedInstanceState == null) {
            if (SharedMethode.getInstance().contextAssigned()) {
                if (SharedMethode.getInstance().getContext() instanceof FullPageAdsListener)
                    fullPageAdsListener = (FullPageAdsListener) SharedMethode.getInstance().getContext();
            }

            loadExtras();
            initUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void initUI() {
        ImageView ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullPageAdsListener != null)
                    fullPageAdsListener.onCloseButtonClick();
                finish();
            }
        });

        GifImageView myImage = findViewById(R.id.ivAdsContainer);

        Glide.with(KaktusAdsActivity.this).load(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (fullPageAdsListener != null)
                    fullPageAdsListener.onImageLoaded(ADS_TOKEN);
                return false;
            }
        }).into(myImage);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPageAdsListener.onAdsClick(loadedAdsLink);
                finish();
            }
        });
    }

    private void loadExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            loadedAdsUrl = extras.getString(ADS_URL);
            loadedAdsToken = extras.getString(ADS_TOKEN);
            loadedAdsLink = extras.getString(ADS_LINK);
        }
    }

    private void checkAndSetOrientationInfo() {
        int currentOrientation = getResources().getConfiguration().orientation;
        debugDescribeOrientations(currentOrientation);
        if (previousOrientation != Configuration.ORIENTATION_UNDEFINED // starts undefined
                && previousOrientation != currentOrientation) rotating = true;

        previousOrientation = currentOrientation;
    }

    private String getOrientationAsString(final int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "Landscape";
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "Portrait";
        } else return "Undefined";
    }

    private void debugDescribeOrientations(final int currentOrientation) {
        Log.v("Orientation", "previousOrientation: " + getOrientationAsString(previousOrientation));
        Log.v("Orientation", "currentOrientation: " + getOrientationAsString(currentOrientation));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Log.v("onPause", "Finishing");
            SharedMethode.freeContext();
        } else {
            checkAndSetOrientationInfo();
            if (rotating) {
                Log.v("onPause", "Rotating");

            } else {
                Log.v("onPause", "Not rotating (task switch / home etc)");
                SharedMethode.freeContext();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_ROTATITED, rotating);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(IS_ROTATITED));
    }

    private void getBanner(String size, final GifImageView myImage) {
/*        RestClient.getInstance(RestClient.API_URL).getBottomBanner(serviceToken, size, getDeviceId(), Base64.encodeToString(packageName.getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
            @Override
            public void onResponse(Call<GetBottomBannerResponse> call, final Response<GetBottomBannerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    Glide.with(KaktusAdsActivity.this).load(response.body().getResult().getSrc().startsWith("http") ? response.body().getResult().getSrc() : "http:" + response.body().getResult().getSrc()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (fullPageAdsListener != null)
                                fullPageAdsListener.onImageLoaded(ADS_TOKEN);
                            return false;
                        }
                    }).into(myImage);
                }
            }

            @Override
            public void onFailure(Call<GetBottomBannerResponse> call, Throwable t) {
                Log.d("LOADING BOTTOM BANNER", "FAILURE");
            }
        });*/
    }


}
