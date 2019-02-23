package com.hamavaran.advertisement;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hamavaran.kaktusads.activity.Advertisement;
import com.hamavaran.kaktusads.activity.Configuration;
import com.hamavaran.kaktusads.interfaces.AdClickListener;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        Advertisement.getInstance().into(MainActivity.this)
                .withSize(Configuration.BANNER_SIZES.FULL_SIZE)
                .withTimeInterval(0).withCloseButton(false).setListener(new AdClickListener() {
            @Override
            public void onButtonCloseClick() {

            }

            @Override
            public void onAdClick() {

            }
        }).loadAds();*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl, new BlankFragment());
        fragmentTransaction.commit();

        Advertisement.getInstance().into(MainActivity.this).withSize(Configuration.BANNER_SIZES.FULL_SIZE)
                .withTimeInterval(0).withCloseButton(false).setListener(new AdClickListener() {
            @Override
            public void onButtonCloseClick() {

            }

            @Override
            public void onAdClick() {

            }
        }).loadAds();






    }
}
