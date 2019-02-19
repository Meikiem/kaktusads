package com.hamavaran.advertisement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.hamavaran.kaktusads.activity.Advertisement;
import com.hamavaran.kaktusads.activity.Configutarion;
import com.hamavaran.kaktusads.interfaces.BannerClickListener;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Advertisement.getInstance().into(MainActivity.this)
                .withSize(Configutarion.BANNER_SIZES.SIZE_FOUR)
                .withTimeInterval(0).withCloseButton(false).setListener(new BannerClickListener() {
            @Override
            public void onBottomBannerCloseClick() {

            }

            @Override
            public void onBottomBannerClick() {

            }
        }).loadAds();







    }
}
