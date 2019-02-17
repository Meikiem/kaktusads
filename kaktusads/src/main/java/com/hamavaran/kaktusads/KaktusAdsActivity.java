package com.hamavaran.kaktusads;

import pl.droidsonroids.gif.GifImageView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;

public class KaktusAdsActivity extends AppCompatActivity {

    public static String ADS_LINK = "ads_link";
    public static String ADS_URL = "ads_url";
    public static String ADS_TOKEN = "ads_token";
    private String loadedAdsLink = null;
    private String loadedAdsUrl = null;
    private String loadedAdsToken = null;
    private FullPageAdsListener fullPageAdsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaktus_ads);

        if (SharedMethode.getInstance().contextAssigned()) {
            if (SharedMethode.getInstance().getContext() instanceof FullPageAdsListener)
                fullPageAdsListener = (FullPageAdsListener) SharedMethode.getInstance().getContext();
            SharedMethode.freeContext();
        }

        loadExtras();
        initUI();
    }

    private void initUI() {
        ImageView ivClose = findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                fullPageAdsListener.onImageLoaded(ADS_TOKEN);
                return false;
            }
        }).into(myImage);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPageAdsListener.onAdsClick(loadedAdsToken);
                finish();
            }
        });
    }

    private void loadExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            loadedAdsLink = extras.getString(ADS_LINK);
            loadedAdsUrl = extras.getString(ADS_URL);
            loadedAdsToken = extras.getString(ADS_TOKEN);
        }
    }



}
