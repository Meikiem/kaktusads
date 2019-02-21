package com.hamavaran.kaktusads.interfaces;

import java.io.Serializable;

public interface FullPageAdsListener{

    void onCloseButtonClick();
    void onAdsClick(String adsLink);
    void onImageLoaded(String serviceToken);
}
