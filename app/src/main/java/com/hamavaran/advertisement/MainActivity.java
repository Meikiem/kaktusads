package com.hamavaran.advertisement;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hamavaran.kaktusads.Advertisement;
import com.hamavaran.kaktusads.interfaces.BannerClickListener;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        Advertisement.into(MainActivity.this)
                .setCloseButtonEnabled(true)
                .setServiceToken("4yvdhua639")
                .listener(new BannerClickListener() {
            @Override
            public void onBottomBannerCloseClick() {
                Toast.makeText(MainActivity.this, "kjsdbkjd", Toast.LENGTH_SHORT).show();

            }
        }).loadBottomBanner(Advertisement.BANNER_SIZES.SIZE_ONE);*/

        Advertisement.into(MainActivity.this)
                .setCloseButtonEnabled(true)
                .setServiceToken("4yvdhua639")
                .listener(new BannerClickListener() {
                    @Override
                    public void onBottomBannerCloseClick() {
                        Toast.makeText(MainActivity.this, "kjsdbkjd", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBottomBannerClick() {

                    }
                }).loadFullPageBanner();

    }
}
