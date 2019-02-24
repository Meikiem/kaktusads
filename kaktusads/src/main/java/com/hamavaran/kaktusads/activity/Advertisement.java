package com.hamavaran.kaktusads.activity;

import android.content.Context;

public class Advertisement {
    private static String serviceToken = null;
    private static String packageName = null;
    private static Advertisement instance = null;
    private Configuration configuration;

    public static Advertisement getInstance() {
        if (instance != null)
            return instance;
        {
        }
        instance = new Advertisement();
        return instance;
    }

    public static void init(String packageName, String serviceToken) {
        Advertisement.packageName = packageName;
        Advertisement.serviceToken = serviceToken;
    }

    public Configuration into(Context context) {
        configuration = new Configuration(context, serviceToken, packageName);
        return configuration;
    }

    public void closeAds() {
        if (configuration != null) {
            configuration.closeAds();
        }
    }

}
