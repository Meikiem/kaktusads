package com.hamavaran.advertisement;

import android.app.Application;

import com.hamavaran.kaktusads.activity.Advertisement;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Advertisement.init(getApplicationContext().getPackageName(), "4yvdhua639");

    }
}
