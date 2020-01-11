package com.hamavaran.kaktusads.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hamavaran.kaktusads.R;
import com.hamavaran.kaktusads.interfaces.FullPageAdsListener;
import com.hamavaran.kaktusads.util.SharedMethode;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import pl.droidsonroids.gif.GifImageView;

public class KaktusAdsActivity extends AppCompatActivity {

    public static String ADS_LINK = "ads_link";
    public static String IS_VIDEO_AD = "is_video_ad";
    public static String ADS_URL = "ads_url";
    public static String ADS_TOKEN = "ads_token";
    private String loadedAdsUrl = null;
    private String loadedAdsToken = null;
    private String loadedAdsLink = null;
    private FullPageAdsListener fullPageAdsListener;
    private RelativeLayout rlVideoContainer;
    private VideoView vv;
    private ProgressBar pb;
    private boolean isVideoAd = false;
//    private CircleProgressBar pbClose;
    final int[] duration = {0};
    private CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaktus_ads);
        if (savedInstanceState == null) {
            if (SharedMethode.getInstance().contextAssigned()) {
                if (SharedMethode.getInstance().getContext() instanceof FullPageAdsListener)
                    fullPageAdsListener = (FullPageAdsListener) SharedMethode.getInstance().getContext();
            }

            loadExtras();
            initUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lockDeviceRotation(true);
    }

    @Override
    protected void onPause() {
        lockDeviceRotation(false);
        if(vv!= null)
            vv.pause();
        finish();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUI() {
        rlVideoContainer = findViewById(R.id.rlVideoContainer);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(pbClose.getVisibility() == View.GONE) {
//                    if (fullPageAdsListener != null)
//                        fullPageAdsListener.onCloseButtonClick();
//                    finish();
//                }
                if(circularProgressBar.getVisibility() == View.GONE) {
                    if (fullPageAdsListener != null)
                        fullPageAdsListener.onCloseButtonClick();
                    finish();
                }
            }
        });

        if (isVideoAd) {
            rlVideoContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.ivAdsContainer).setVisibility(View.GONE);
            initVideoAd();
        } else {
            rlVideoContainer.setVisibility(View.GONE);
            findViewById(R.id.ivAdsContainer).setVisibility(View.VISIBLE);
            initImageAd();
        }
    }

    private void initVideoAd() {
        pb = findViewById(R.id.pb);
//        pbClose = findViewById(R.id.pbClose);
        circularProgressBar.setProgressMax(100);
        pb.setProgress(0);
        pb.setMax(100);

        vv = findViewById(R.id.vv);
        vv.setVideoURI(Uri.parse(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl));

        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                duration[0] = vv.getDuration();
                startProgress(duration[0]);

                if (duration[0] >= 0) {
//                    pbClose.setVisibility(View.VISIBLE);
                    circularProgressBar.setVisibility(View.VISIBLE);
                    countDown();
                } else {
//                    pbClose.setVisibility(View.GONE);
                    circularProgressBar.setVisibility(View.GONE);
                }
            }
        });

        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPageAdsListener.onAdsClick(loadedAdsLink);
                finish();
            }
        });

    }


    private void countDown() {
        CountDownTimer countDownTimer;
        final int[] i = {0};

//        pbClose.setProgress(i[0]);
        circularProgressBar.setProgress(i[0]);
        countDownTimer = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i[0] + millisUntilFinished);
                i[0]++;
//                pbClose.setProgress(i[0] * 100 / (5000 / 1000));
                circularProgressBar.setProgress(i[0] * 100 / (5000 / 1000));
                if (circularProgressBar.getProgress() == 100) {
//                    pbClose.setVisibility(View.GONE);
                    circularProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFinish() {
                //Do what you want
                i[0]++;
//                pbClose.setProgress(100);
                circularProgressBar.setProgress(100);
            }
        };
        countDownTimer.start();
    }

    private void startProgress(final int duration) {
        if (pb.getProgress() < 100 && duration > 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pb.setProgress((vv.getCurrentPosition() * 100) / duration);
                    if (pb.getProgress() == 100)
                        finish();
                    handler.postDelayed(this, 500);
                }
            }, 500);
        }
    }

    private void initImageAd() {
        GifImageView myImage = findViewById(R.id.ivAdsContainer);

        Glide.with(KaktusAdsActivity.this).load(loadedAdsUrl.startsWith("http") ? loadedAdsUrl : "http:" + loadedAdsUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (fullPageAdsListener != null)
                    fullPageAdsListener.onImageLoaded(ADS_TOKEN);
                startProgress(3000);
                countDown();

                return false;
            }
        }).into(myImage);


        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPageAdsListener.onAdsClick(loadedAdsLink);
                finish();
            }
        });
    }

    private void loadExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            loadedAdsUrl = extras.getString(ADS_URL);
            loadedAdsToken = extras.getString(ADS_TOKEN);
            loadedAdsLink = extras.getString(ADS_LINK);
            isVideoAd = extras.getBoolean(IS_VIDEO_AD);
        }
    }

    public void lockDeviceRotation(boolean value) {
        if (value) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
    }


}
