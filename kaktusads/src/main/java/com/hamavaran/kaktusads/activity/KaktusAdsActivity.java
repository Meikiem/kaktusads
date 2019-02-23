package com.hamavaran.kaktusads.activity;

import pl.droidsonroids.gif.GifImageView;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.R;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.hamavaran.kaktusads.util.SharedMethode;

public class KaktusAdsActivity extends AppCompatActivity {

    public static String ADS_LINK = "ads_link";
    public static String ADS_URL = "ads_url";
    public static String ADS_TOKEN = "ads_token";
    private String loadedAdsUrl = null;
    private String loadedAdsToken = null;
    private String loadedAdsLink = null;
    private FullPageAdsListener fullPageAdsListener;

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
    protected void onResume() {
        super.onResume();
        lockDeviceRotation(true);
    }

    @Override
    protected void onPause() {
        lockDeviceRotation(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void lockDeviceRotation(boolean value) {
        if (value) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
    }


}
