package com.hamavaran.kaktusads.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.R;
import com.hamavaran.kaktusads.fragment.AdsDialogFragment;
import com.hamavaran.kaktusads.interfaces.AdClickListener;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.hamavaran.kaktusads.rest.Model.BaseResponse;
import com.hamavaran.kaktusads.rest.Model.GetBottomBannerResponse;
import com.hamavaran.kaktusads.rest.RestClient;
import com.hamavaran.kaktusads.util.NetworkChangeReceiver;
import com.hamavaran.kaktusads.util.SharedMethode;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hamavaran.kaktusads.Constants.CONNECTIVITY_ACTION;
import static com.hamavaran.kaktusads.activity.KaktusAdsActivity.ADS_LINK;
import static com.hamavaran.kaktusads.activity.KaktusAdsActivity.ADS_TOKEN;
import static com.hamavaran.kaktusads.activity.KaktusAdsActivity.ADS_URL;
import static com.hamavaran.kaktusads.activity.KaktusAdsActivity.IS_VIDEO_AD;

public class AdvertisementLoader extends AppCompatActivity implements FullPageAdsListener, NetworkChangeReceiver.NetworkReceiverListener {

    private Context context;
    private boolean closeButtonEnabled;
    private String serviceToken;
    private String packageName;
    private int timeInterval;
    private Configuration.BANNER_SIZES adSize;
    private GifImageView myImage;
    private View view;
    private AdClickListener listener;
    private RelativeLayout bottomBannerRL;
    private RelativeLayout rootRL;
    private String link = null;
    private NetworkChangeReceiver receiver;
    private static int rootViewId = -1;
    private static int currentRotation = -1;
    private boolean closeAds = false;
    private Configuration.BANNER_POSITION position;
    private boolean isFragment = false;


    public AdvertisementLoader(Context context, boolean closeButtonEnabled, String servcieToken, String packageName, int timeInterval, Configuration.BANNER_SIZES adSize, Configuration.BANNER_POSITION position) {
        this.context = context;
        this.closeButtonEnabled = closeButtonEnabled;
        this.packageName = packageName;
        this.serviceToken = servcieToken;
        this.timeInterval = timeInterval;
        this.adSize = adSize;
        if (currentRotation == -1)
            currentRotation = context.getResources().getConfiguration().orientation;
        if (currentRotation != context.getResources().getConfiguration().orientation) {
            rootViewId = -1;
            currentRotation = context.getResources().getConfiguration().orientation;
        }
        this.position = position;
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public AdvertisementLoader setListener(AdClickListener listener) {
        this.listener = listener;
        return this;
    }


    public void loadAds() {
        isFragment = false;
        if (timeInterval > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    load();
                }
            }, timeInterval);
        else
            load();
    }

    public void loadAds(View view) {
        this.view = view;
        isFragment = true;
        if (timeInterval > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    load();
                }
            }, timeInterval);
        else
            load();
    }

    private void load() {
        if (checkBannerCallValidation()) return;
        if (rootViewId != -1)
            return;
        if (!adSize.IS_FULL_SIZE)
            myImage = initBottomAdUI();
        checkNetworkState();
    }


    public void checkNetworkState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        receiver.addListener(this);

        context.registerReceiver(receiver, intentFilter);
    }

    private boolean checkBannerCallValidation() {
        if (packageName == null) {
            Toast.makeText(context, "Please set package name", Toast.LENGTH_SHORT).show();
            return true;

        }
        if (serviceToken == null) {
            Toast.makeText(context, "Please set your token", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private GifImageView initBottomAdUI() {

        final RelativeLayout rootRL = initMainView();
        bottomBannerRL = new RelativeLayout(view != null ? view.getContext() : context);
        bottomBannerRL.setVisibility(View.INVISIBLE);


        rootRL.addView(bottomBannerRL);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adSize.BANNER_WIDTH / 2, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adSize.BANNER_HEIGHT / 2, context.getResources().getDisplayMetrics()));

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        myImage = new GifImageView(view != null ? view.getContext() : context);
        myImage.setAdjustViewBounds(true);
        bottomBannerRL.addView(myImage);
        myImage.setLayoutParams(layoutParams);
        myImage.setScaleType(ImageView.ScaleType.FIT_CENTER);


        ImageView closeButton = new ImageView(view != null ? view.getContext() : context);
        closeButton.setImageResource(R.drawable.ic_close);
        closeButton.setColorFilter(ContextCompat.getColor(context, android.R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        bottomBannerRL.addView(closeButton);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeButtonEnabled) {
                    ((ViewGroup) view).removeView(rootRL);
                    rootViewId = -1;
                }
                listener.onButtonCloseClick();
            }
        });


        ((ViewGroup) view).addView(rootRL);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://" + link;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
                listener.onAdClick();
            }
        });

        closeButton.setVisibility(closeButtonEnabled ? View.VISIBLE : View.GONE);

        return myImage;
    }

    private RelativeLayout initMainView() {
        if (view == null)
            view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootRL = new RelativeLayout(view.getContext());
        rootRL.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        rootRL.setVisibility(View.INVISIBLE);
        rootViewId = View.generateViewId();
        rootRL.setId(rootViewId);
        rootRL.setFitsSystemWindows(true);
        return rootRL;
    }

    private void getBanner(String size) {
        RestClient.getInstance(RestClient.API_URL).getBottomBanner(serviceToken, size, getDeviceId(), Base64.encodeToString(packageName.getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
            @Override
            public void onResponse(Call<GetBottomBannerResponse> call, final Response<GetBottomBannerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null && !closeAds) {
                    handleGetBannerResponse(response, myImage);
                }
            }

            @Override
            public void onFailure(Call<GetBottomBannerResponse> call, Throwable t) {
                Log.d("LOADING BOTTOM BANNER", "FAILURE");
            }
        });
    }

    private void handleGetBannerResponse(final Response<GetBottomBannerResponse> response, final GifImageView myImage) {
        assert response.body() != null;
        final String imageUrl = response.body().getResult().getSrc();
        link = response.body().getResult().getLink();


        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }


        switch (adSize) {
            case SIZE_468_x_60:
            case SIZE_728_x_90:
            case SIZE_960_x_144:
            case SIZE_970_x_90:
                Glide.with(view).load(imageUrl.startsWith("http") ? imageUrl : "http:" + imageUrl).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setImageContainerLayoutParams();
                                sendFeedbackOnBannerLoaded(response.body().getResult().getToken());
                                rootRL.setVisibility(View.VISIBLE);
                                try {
                                    context.unregisterReceiver(receiver);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                        return false;
                    }
                }).into(myImage);
                break;
            case FULL_SIZE:
            case FULL_SIZE_VIDEO:
                SharedMethode.getInstance().setContext(AdvertisementLoader.this);
                Intent adsIntent = new Intent(context, KaktusAdsActivity.class);
                adsIntent.putExtra(ADS_LINK, link);
                adsIntent.putExtra(ADS_URL, imageUrl);
                if (response.body() != null) {
                    adsIntent.putExtra(ADS_TOKEN, response.body().getResult().getToken());
                }
                adsIntent.putExtra(IS_VIDEO_AD, adSize == Configuration.BANNER_SIZES.FULL_SIZE_VIDEO);
                adsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(adsIntent);


                try {
                    context.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case VIDEO_POP_UP:
            case IMAGE_POP_UP:
                String adsToken = null;
                if (response.body() != null) {
                    adsToken = response.body().getResult().getToken();
                }
                AdsDialogFragment.newInstance(link, adSize == Configuration.BANNER_SIZES.VIDEO_POP_UP, imageUrl, adsToken, new AdsDialogFragment.AdsDialogListener() {
                    @Override
                    public void onClose(DialogFragment dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onLinkClick(DialogFragment dialog, String loadedAdsLink) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onImageLoaded(String adsToken) {
                        Log.d("", "");
                    }
                }).show(((AppCompatActivity) context).getSupportFragmentManager(), "tag");
                break;
        }


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

    private void setImageContainerLayoutParams() {
        RelativeLayout.LayoutParams bottomBannerLayoutParams = new RelativeLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adSize.BANNER_WIDTH / 2, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adSize.BANNER_HEIGHT / 2, context.getResources().getDisplayMetrics()));
        if (position == Configuration.BANNER_POSITION.BOTTOM)
            bottomBannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        bottomBannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        bottomBannerRL.setVisibility(View.VISIBLE);
        bottomBannerRL.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));

        bottomBannerRL.setLayoutParams(bottomBannerLayoutParams);

    }

    @Override
    public void onCloseButtonClick() {
        listener.onButtonCloseClick();

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

    @Override
    public void available() {
        if (adSize == Configuration.BANNER_SIZES.FULL_SIZE_VIDEO || adSize == Configuration.BANNER_SIZES.VIDEO_POP_UP) {
            getVideoBanner();
        } else
            getBanner(adSize.SIZE);
    }

    private void getVideoBanner() {
        RestClient.getInstance(RestClient.API_URL).getVideoBanner(serviceToken, getDeviceId(), Base64.encodeToString(packageName.getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
            @Override
            public void onResponse(Call<GetBottomBannerResponse> call, Response<GetBottomBannerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null && !closeAds) {
                    handleGetBannerResponse(response, myImage);
                }
            }

            @Override
            public void onFailure(Call<GetBottomBannerResponse> call, Throwable t) {
                Log.d("LOAD VIDEO AD: ", "FAILURE");
            }
        });
    }

    @Override
    public void unavailable() {
        Log.d("NETWORK STATE: ", "UNAVAILABLE");
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        return String.valueOf(Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    public void closeAds() {
        try {
            closeAds = true;
            if (view != null && view.findViewById(rootViewId) != null) {
                ((ViewGroup) view).removeView(rootRL);
                rootViewId = -1;
                closeAds = false;
            }
        } catch (Exception ignored) {
            Log.d("ClOSE ADDS FACED: ", "ERROR");
        }
    }


}
