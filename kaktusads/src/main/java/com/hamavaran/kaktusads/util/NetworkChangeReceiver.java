package com.hamavaran.kaktusads.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hamavaran.kaktusads.Constants;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Context mContext;
    protected NetworkReceiverListener listeners;
    protected Boolean connected;

    public NetworkChangeReceiver() {
        connected = null;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);

        Log.e("Receiver ", "" + status);

        if (status.equals(Constants.NOT_CONNECT)) {
            Log.e("Receiver ", "not connection");// your code when internet lost
            connected = false;
            listeners.unavailable();
        } else {
            Log.e("Receiver ", "connected to internet");//your code when internet connection come
            connected = true;
            listeners.available();
        }


    }

    public void addListener(NetworkReceiverListener l) {
        this.listeners = l;
    }


    public interface NetworkReceiverListener {
        void available();
        void unavailable();
    }

}
