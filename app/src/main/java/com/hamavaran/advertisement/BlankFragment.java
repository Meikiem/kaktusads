package com.hamavaran.advertisement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamavaran.kaktusads.activity.Advertisement;
import com.hamavaran.kaktusads.activity.Configutarion;
import com.hamavaran.kaktusads.interfaces.AdClickListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        Advertisement.getInstance().into(getContext()).withSize(Configutarion.BANNER_SIZES.FULL_SIZE)
                .withTimeInterval(0).withCloseButton(false).setListener(new AdClickListener() {
            @Override
            public void onButtonCloseClick() {

            }

            @Override
            public void onAdClick() {

            }
        }).loadAds(view);
    }



}