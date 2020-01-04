package com.hamavaran.advertisement;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamavaran.kaktusads.activity.Advertisement;
import com.hamavaran.kaktusads.activity.Configuration;
import com.hamavaran.kaktusads.interfaces.AdClickListener;


public class BlankFragment extends Fragment {

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Advertisement.getInstance().into(getContext()).withSize(Configuration.BANNER_SIZES.SIZE_468_x_60)
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
