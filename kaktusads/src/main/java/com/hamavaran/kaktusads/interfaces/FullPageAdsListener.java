package com.hamavaran.kaktusads.interfaces;

public interface FullPageAdsListener {

    void onCloseButtonClick();
    void onAdsClick(String adsLink);
    void onImageLoaded(String serviceToken);
}
