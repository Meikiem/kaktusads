package com.hamavaran.kaktusads.activity;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Configutarion {

    private Context context;
    private String getServiceToken;
    private String getPackageName;
    private static int WIDTH;
    private static int HEIGHT;

    Configutarion(Context context, String serviceToken, String packageName) {
        this.context = context;
        getServiceToken = serviceToken;
        getPackageName = packageName;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WIDTH = displayMetrics.widthPixels;
        HEIGHT = displayMetrics.heightPixels;
    }

    public TimeInterval withSize(BANNER_SIZES size){
        return new TimeInterval(context, getServiceToken, getPackageName, size);
    }

    public enum BANNER_SIZES {
        SIZE_468_x_60(468, 60, false),
        SIZE_728_x_90(728, 90, false),
        SIZE_970_x_90(970, 90, false),
        SIZE_960_x_144(960, 144, false),
        FULL_SIZE(WIDTH, HEIGHT, true);

        final String SIZE;
        final boolean IS_FULL_SIZE;

        BANNER_SIZES(int width, int height, boolean isFullSize) {
            SIZE = width + "x" + height;
            IS_FULL_SIZE = isFullSize;
        }

    }


    public class TimeInterval{
        private Context context;
        private String serviceToken;
        private String packageName;
        private BANNER_SIZES adSize;

        TimeInterval(Context context, String serviceToken, String packageName, BANNER_SIZES adSize) {
            this.context = context;
            this.serviceToken = serviceToken;
            this.packageName = packageName;
            this.adSize = adSize;
        }

        public CloseButtonStatus withTimeInterval(int timeInterval){
            return new CloseButtonStatus(context, this.serviceToken, this.packageName, timeInterval, adSize);
        }
    }


    public class CloseButtonStatus{
        private int timeInterval;
        private Context context;
        private String serviceToken;
        private String packageName;
        private BANNER_SIZES adSize;

        CloseButtonStatus(Context context, String serviceToken, String packageName, int timeInterval, BANNER_SIZES adSize) {
            this.context = context;
            this.serviceToken = serviceToken;
            this.packageName = packageName;
            this.timeInterval = timeInterval;
            this.adSize = adSize;
        }

        public AdvertisementLoader withCloseButton(boolean closeButtonStatus){
            return new AdvertisementLoader(this.context, closeButtonStatus, this.serviceToken, this.packageName, this.timeInterval, adSize);
        }
    }

}
