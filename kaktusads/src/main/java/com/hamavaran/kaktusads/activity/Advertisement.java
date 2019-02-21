package com.hamavaran.kaktusads.activity;

import android.content.Context;

public class Advertisement {
    private static String serviceToken = null;
    private static String packageName = null;
    private static Advertisement instance = null;

    public static Advertisement getInstance(){
        if(instance!= null)
            return instance;{
        }
        instance = new Advertisement();
        return instance;
    }

    public static void init(String packageName, String serviceToken) {
        Advertisement.packageName = packageName;
        Advertisement.serviceToken = serviceToken;
    }

    public Configutarion into(Context context) {
        return new Configutarion(context, serviceToken, packageName);
    }

}
