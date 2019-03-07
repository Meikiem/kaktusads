package com.hamavaran.kaktusads.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.R;

import pl.droidsonroids.gif.GifImageView;


public class AdsDialogFragment extends DialogFragment {
    private GifImageView ivAdsContainer;
    private VideoView vv;
    private ProgressBar pb;
    private ImageView ivClose;
    private View view;

    private Context context;

    private AdsDialogListener listener;
    private static String ADS_LINK = "ads_link";
    private static String IS_VIDEO_AD = "is_video_ad";
    private static String ADS_URL = "ads_url";
    private static String ADS_TOKEN = "ads_token";

    private boolean isVideoAd;
    private String loadedAdsUrl;
    private String loadedAdsToken;
    private String loadedAdsLink;
    private final int[] duration = {0};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_ads, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        loadExtras();

        initUI();

        Dialog dialog = builder.create();
        if
        (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    private void loadExtras() {
        if (getArguments() != null) {
            isVideoAd = getArguments().getBoolean(IS_VIDEO_AD);
            loadedAdsUrl = getArguments().getString(ADS_URL);
            loadedAdsToken = getArguments().getString(ADS_TOKEN);
            loadedAdsLink = getArguments().getString(ADS_LINK);
        }
    }

    private void initUI() {
        ivAdsContainer = view.findViewById(R.id.ivAdsContainer);
        vv = view.findViewById(R.id.vv);
        pb = view.findViewById(R.id.pb);
        ivClose = view.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClose(AdsDialogFragment.this);
                getDialog().dismiss();
            }
        });

        if (isVideoAd) {
            vv.setVisibility(View.VISIBLE);
            ivAdsContainer.setVisibility(View.GONE);
            initVideoAd();
        } else {
            vv.setVisibility(View.GONE);
            ivAdsContainer.setVisibility(View.VISIBLE);
            initImageAd();
        }


    }

    private void initVideoAd() {
        pb.setProgress(0);
        pb.setMax(100);

        vv.setVideoURI(Uri.parse(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl));




        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                duration[0] = vv.getDuration();
                startProgress(duration[0]);

            }
        });

        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLinkClick(AdsDialogFragment.this, loadedAdsLink);
                getDialog().dismiss();
            }
        });


    }

    private void initImageAd() {

        Glide.with(context).load(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null)
                    listener.onImageLoaded(ADS_TOKEN);
                return false;
            }
        }).into(ivAdsContainer);


        ivAdsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLinkClick(AdsDialogFragment.this, loadedAdsLink);
                getDialog().dismiss();
            }
        });
    }

    private void startProgress(final int duration) {

        if (pb.getProgress() < 100 && duration > 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pb.setProgress((vv.getCurrentPosition() * 100) / duration);
                    if (pb.getProgress() == 100)
                        getDialog().dismiss();
                    handler.postDelayed(this, 500);
                }
            }, 500);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        lockDeviceRotation(true);
    }

    @Override
    public void onPause() {
        lockDeviceRotation(false);
        super.onPause();
    }

    public void lockDeviceRotation(boolean value) {
        if (getActivity() != null)
            if (value) {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }
    }


    public static AdsDialogFragment newInstance(String adsLink, boolean isVideoAd, String adsUrl, String adsToken, AdsDialogListener listener) {
        AdsDialogFragment dialog = new AdsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ADS_LINK, adsLink);
        args.putBoolean(IS_VIDEO_AD, isVideoAd);
        args.putString(ADS_URL, adsUrl);
        args.putString(ADS_TOKEN, adsToken);
        dialog.setArguments(args);
        dialog.listener = listener;
        return dialog;
    }


    public interface AdsDialogListener {
        void onClose(DialogFragment dialog);

        void onLinkClick(DialogFragment dialog, String loadedAdsLink);

        void onImageLoaded(String adsToken);

    }
}
