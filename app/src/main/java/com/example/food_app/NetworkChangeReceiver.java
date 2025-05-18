package com.example.food_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static boolean networkStatus = true;


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            //Toast.makeText(context, "Network is connected", Toast.LENGTH_SHORT).show();
            networkStatus=true;
            Log.d("TAG", "networkStatus: "+networkStatus);
        } else {

            //Toast.makeText(context, "Network is disconnected", Toast.LENGTH_SHORT).show();
            networkStatus=false;
            Log.d("TAG", "networkStatus: "+networkStatus);
        }
    }
}
