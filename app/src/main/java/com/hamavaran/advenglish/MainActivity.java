package com.hamavaran.advenglish;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hamavaran.kaktusads.activity.Advertisement;
import com.hamavaran.kaktusads.activity.Configuration;
import com.hamavaran.kaktusads.interfaces.AdClickListener;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Advertisement.getInstance().into(MainActivity.this).withSize(Configuration.BANNER_SIZES.FULL_SIZE)
                .withTimeInterval(0).withCloseButton(true).position(Configuration.BANNER_POSITION.BOTTOM).setListener(new AdClickListener() {
            @Override
            public void onButtonCloseClick() {

            }

            @Override
            public void onAdClick() {

            }
        }).loadAds();

    }

}
