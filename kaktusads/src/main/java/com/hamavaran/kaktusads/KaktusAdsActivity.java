package com.hamavaran.kaktusads;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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


        ImageLoader.getInstance().displayImage(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl, myImage, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                fullPageAdsListener.onImageLoaded(ADS_TOKEN);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });

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
