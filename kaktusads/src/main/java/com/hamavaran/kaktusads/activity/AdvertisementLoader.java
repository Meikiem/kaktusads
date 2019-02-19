package com.hamavaran.kaktusads.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.pm.PermissionInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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
import com.hamavaran.kaktusads.interfaces.BannerClickListener;
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

public class AdvertisementLoader extends AppCompatActivity implements FullPageAdsListener, NetworkChangeReceiver.NetworkReceiverListener {

    private Context context;
    private boolean closeButtonEnabled;
    private String serviceToken = null;
    private String packageName;
    private int timeInterval;
    private Configutarion.BANNER_SIZES adSize;
    private GifImageView myImage;
    private View view;
    private BannerClickListener listener;
    private RelativeLayout bottomBannerRL;
    private RelativeLayout rootRL;
    private String link = null;


    public AdvertisementLoader(Context context, boolean closeButtonEnabled, String servcieToken, String packageName, int timeInterval, Configutarion.BANNER_SIZES adSize) {
        this.context = context;
        this.closeButtonEnabled = closeButtonEnabled;
        this.packageName = packageName;
        this.serviceToken = servcieToken;
        this.timeInterval = timeInterval;
        this.adSize = adSize;
    }

    public AdvertisementLoader setListener(BannerClickListener listener) {
        this.listener = listener;
        return this;
    }


    public void loadAds() {

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
        myImage = adSize.IS_FULL_SIZE ? initFullPageAdUI() : initBottomAdUI();
        chectNetworkState();
    }


    public void chectNetworkState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
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
        view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootRL = new RelativeLayout(context);
        rootRL.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        rootRL.setVisibility(View.INVISIBLE);
        return rootRL;
    }

    private void getBanner(String size, final GifImageView myImage) {
        RestClient.getInstance(RestClient.API_URL).getBottomBanner(serviceToken, size, getDeviceId(), Base64.encodeToString(packageName.getBytes(), Base64.NO_WRAP)).enqueue(new Callback<GetBottomBannerResponse>() {
            @Override
            public void onResponse(Call<GetBottomBannerResponse> call, final Response<GetBottomBannerResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
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
        final String imageUrl = response.body().getResult().getImage();
        link = response.body().getResult().getLink();

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
                        if (!adSize.IS_FULL_SIZE) {
                            setImageContainerLayoutParams(myImage);
                            sendFeedbackOnBannerLoaded(response.body().getResult().getToken());
                            rootRL.setVisibility(View.VISIBLE);
                        } else {
                            SharedMethode.getInstance().setContext(AdvertisementLoader.this);

                            Intent adsIntent = new Intent(context, KaktusAdsActivity.class);
                            adsIntent.putExtra(ADS_LINK, link);
                            adsIntent.putExtra(ADS_URL, imageUrl);
                            adsIntent.putExtra(ADS_TOKEN, response.body().getResult().getToken());
                            context.startActivity(adsIntent);
                        }
                    }
                }, 1000);
                return false;
            }
        }).into(myImage);

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

    @Override
    public void onCloseButtonClick() {
        listener.onBottomBannerCloseClick();

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
        getBanner(adSize.SIZE, myImage);
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
}